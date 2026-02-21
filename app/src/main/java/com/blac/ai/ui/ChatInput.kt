package com.blac.ai.ui

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.blac.ai.data.Attachment
import com.blac.ai.utils.VoiceManager

@Composable
fun ChatInput(
    onSendMessage: (String, List<Attachment>) -> Unit,
    isLoading: Boolean,
    voiceManager: VoiceManager,
    onVoiceInputStart: () -> Unit,
    onVoiceInputStop: () -> Unit,
    onMultiImageSelected: (List<Bitmap>) -> Unit
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf<List<Attachment>>(emptyList()) }
    var isRecording by remember { mutableStateOf(false) }

    // Single image picker (existing)
    val singleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = context.contentResolver.openInputStream(it)?.use { stream ->
                android.graphics.BitmapFactory.decodeStream(stream)
            }
            attachments = listOf(Attachment.Image(uri.toString(), bitmap))
        }
    }

    // Multi-image picker (new)
    val multiImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val bitmaps = uris.mapNotNull { uri ->
            context.contentResolver.openInputStream(uri)?.use { stream ->
                android.graphics.BitmapFactory.decodeStream(stream)
            }
        }
        if (bitmaps.isNotEmpty()) {
            onMultiImageSelected(bitmaps)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Show attachment preview if any
        if (attachments.isNotEmpty()) {
            AttachmentPreview(attachments, onRemove = { attachments = emptyList() })
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Single image attachment
            IconButton(onClick = { singleImagePickerLauncher.launch("image/*") }, enabled = !isLoading) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach single image")
            }

            // Multi-image picker
            IconButton(onClick = { multiImagePickerLauncher.launch("image/*") }, enabled = !isLoading) {
                Icon(Icons.Default.Collections, contentDescription = "Attach multiple images")
            }

            // Voice input button (offline)
            IconButton(
                onClick = {
                    if (isRecording) {
                        onVoiceInputStop()
                        isRecording = false
                    } else {
                        onVoiceInputStart()
                        isRecording = true
                    }
                },
                enabled = !isLoading && voiceManager.isModelReady.value
            ) {
                Icon(
                    if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = if (isRecording) "Stop recording" else "Voice input"
                )
            }

            // Text field
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask something...") },
                enabled = !isLoading
            )

            // Send button
            IconButton(
                onClick = {
                    if (text.isNotBlank() || attachments.isNotEmpty()) {
                        onSendMessage(text, attachments)
                        text = ""
                        attachments = emptyList()
                    }
                },
                enabled = !isLoading
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun AttachmentPreview(attachments: List<Attachment>, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${attachments.size} file(s) attached")
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Close, contentDescription = "Remove attachments")
        }
    }
}