package com.blac.ai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.blac.ai.viewmodel.ChatViewModel
import com.blac.ai.ui.components.ChatBubble
import com.blac.ai.ui.components.OptionsBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory)
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val options by viewModel.options.collectAsState()
    val voiceManager = viewModel.voiceManager
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blac.ai") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            ChatInput(
                onSendMessage = { text, attachments ->
                    viewModel.sendMessage(text, attachments)
                },
                isLoading = isLoading,
                voiceManager = voiceManager,
                onVoiceInputStart = { viewModel.startVoiceInput() },
                onVoiceInputStop = { viewModel.stopVoiceInput() },
                onMultiImageSelected = { bitmaps ->
                    viewModel.processMultiImageOcr(bitmaps)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OptionsBar(
                options = options,
                onToggleThink = { viewModel.toggleThinkMode() },
                onToggleSearch = { viewModel.toggleSearch() },
                onToggleCode = { viewModel.toggleCodeMode() }
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                reverseLayout = true,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                items(messages.reversed()) { message ->
                    ChatBubble(message)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}