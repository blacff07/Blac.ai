package com.blac.ai.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blac.ai.data.Attachment
import com.blac.ai.utils.VoiceManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(
    onSendMessage: (String, List<Attachment>) -> Unit,
    onVoiceInputStart: () -> Unit,
    onVoiceInputStop: () -> Unit,
    onAttachImages: () -> Unit,
    onAttachFiles: () -> Unit,
    voiceManager: VoiceManager,
    isLoading: Boolean
) {
    var text by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf<List<Attachment>>(emptyList()) }
    var isRecording by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    // Animation states
    val recordingAnim = remember { MutableTransitionState(false) }
    val buttonScale = remember { Animatable(1f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        // Attachments preview
        AnimatedVisibility(
            visible = attachments.isNotEmpty(),
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            AttachmentPreviewRow(
                attachments = attachments,
                onRemoveAll = { attachments = emptyList() }
            )
        }

        // Main input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Attachment button with submenu
            AttachmentMenu(
                onAttachImages = onAttachImages,
                onAttachFiles = onAttachFiles,
                enabled = !isLoading && !isRecording
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Text input field
            Card(
                modifier = Modifier
                    .weight(1f)
                    .shadow(4.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (text.isEmpty()) {
                                    Text(
                                        text = if (isRecording) 
                                            "Listening..." 
                                        else 
                                            "Type a message...",
                                        style = LocalTextStyle.current.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (text.isNotBlank()) {
                                onSendMessage(text, attachments)
                                text = ""
                                attachments = emptyList()
                                keyboardController?.hide()
                            }
                        }
                    ),
                    enabled = !isLoading && !isRecording
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Voice/Send button with animation
            Box(contentAlignment = Alignment.Center) {
                if (text.isBlank() && attachments.isEmpty()) {
                    // Voice button with recording animation
                    VoiceButton(
                        isRecording = isRecording,
                        onToggleRecording = {
                            if (isRecording) {
                                onVoiceInputStop()
                                isRecording = false
                            } else {
                                onVoiceInputStart()
                                isRecording = true
                            }
                        },
                        enabled = !isLoading && voiceManager.isModelReady.value
                    )
                } else {
                    // Send button
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                buttonScale.animateTo(0.8f, tween(50))
                                buttonScale.animateTo(1f, tween(100))
                            }
                            onSendMessage(text, attachments)
                            text = ""
                            attachments = emptyList()
                            keyboardController?.hide()
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttachmentMenu(
    onAttachImages: () -> Unit,
    onAttachFiles: () -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        FloatingActionButton(
            onClick = { expanded = true },
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(2.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Attach",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = { Text("Images") },
                onClick = {
                    expanded = false
                    onAttachImages()
                },
                leadingIcon = {
                    Icon(Icons.Default.Image, contentDescription = null)
                },
                enabled = enabled
            )
            DropdownMenuItem(
                text = { Text("Files") },
                onClick = {
                    expanded = false
                    onAttachFiles()
                },
                leadingIcon = {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                },
                enabled = enabled
            )
        }
    }
}

@Composable
private fun VoiceButton(
    isRecording: Boolean,
    onToggleRecording: () -> Unit,
    enabled: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )

    FloatingActionButton(
        onClick = onToggleRecording,
        modifier = Modifier.size(48.dp),
        containerColor = if (isRecording) 
            MaterialTheme.colorScheme.error 
        else 
            MaterialTheme.colorScheme.surfaceVariant,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(2.dp)
    ) {
        Icon(
            if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
            contentDescription = if (isRecording) "Stop" else "Voice input",
            tint = if (isRecording)
                MaterialTheme.colorScheme.onError
            else
                MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(if (isRecording) scale else 1f)
        )
    }
}

@Composable
private fun AttachmentPreviewRow(
    attachments: List<Attachment>,
    onRemoveAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Default.AttachFile,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${attachments.size} file(s) attached",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        IconButton(onClick = onRemoveAll) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove all",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}