package com.blac.ai.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.blac.ai.data.Attachment
import com.blac.ai.data.ChatMessage
import com.blac.ai.data.ToggleOptions
import com.blac.ai.repository.GeminiRepository
import com.blac.ai.utils.OcrHelper
import com.blac.ai.utils.VoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GeminiRepository(application)
    private val ocrHelper = OcrHelper()
    val voiceManager = VoiceManager(application)

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _options = MutableStateFlow(ToggleOptions())
    val options = _options.asStateFlow()

    init {
        viewModelScope.launch {
            voiceManager.initialize()
        }
    }

    fun sendMessage(text: String, attachments: List<Attachment> = emptyList()) {
        viewModelScope.launch {
            _isLoading.value = true
            val userMsg = ChatMessage(
                content = text,
                isUser = true,
                attachments = attachments
            )
            _messages.update { it + userMsg }

            val finalPrompt = if (attachments.isNotEmpty()) {
                val imageAttachments = attachments.filterIsInstance<Attachment.Image>()
                if (imageAttachments.isNotEmpty()) {
                    val bitmaps = imageAttachments.mapNotNull { it.bitmap }
                    val extracted = ocrHelper.extractTextFromImages(bitmaps)
                    "Extracted text from image(s):\n$extracted\n\nUser question: $text"
                } else text
            } else text

            val response = repository.sendMessage(finalPrompt, _options.value)
            _messages.update { it + ChatMessage(content = response, isUser = false) }
            _isLoading.value = false
        }
    }

    fun processMultiImageOcr(bitmaps: List<Bitmap>) {
        viewModelScope.launch {
            _isLoading.value = true
            val extracted = ocrHelper.extractTextFromImages(bitmaps)
            val prompt = "I have extracted the following text from multiple images:\n\n$extracted\n\nPlease summarize or answer based on this content."
            val response = repository.sendMessage(prompt, _options.value)
            _messages.update { it + ChatMessage(content = response, isUser = false) }
            _isLoading.value = false
        }
    }

    fun startVoiceInput() {
        viewModelScope.launch {
            if (!voiceManager.isModelReady.value) {
                voiceManager.downloadModel()
                return@launch
            }
            voiceManager.startListening { recognizedText ->
                sendMessage(recognizedText)
            }
        }
    }

    fun stopVoiceInput() {
        voiceManager.stopListening()
    }

    fun toggleThinkMode() {
        _options.update { it.copy(thinkMode = !it.thinkMode) }
    }

    fun toggleSearch() {
        _options.update { it.copy(realTimeSearch = !it.realTimeSearch) }
    }

    fun toggleCodeMode() {
        _options.update { it.copy(codeMode = !it.codeMode) }
    }

    fun clearChat() {
        _messages.value = emptyList()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return ChatViewModel(application) as T
            }
        }
    }
}