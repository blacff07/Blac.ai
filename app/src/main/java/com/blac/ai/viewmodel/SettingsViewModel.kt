package com.blac.ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.blac.ai.repository.GeminiRepository
import com.blac.ai.utils.SecurePrefs
import com.blac.ai.utils.VoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = SecurePrefs.getInstance(application)
    private val voiceManager = VoiceManager(application)

    private val _apiKey = MutableStateFlow(prefs.getString("user_gemini_key", "") ?: "")
    val apiKey = _apiKey.asStateFlow()

    val isModelReady = voiceManager.isModelReady
    val isDownloading = voiceManager.isDownloading
    val downloadProgress = voiceManager.downloadProgress

    fun saveApiKey(key: String) {
        _apiKey.value = key
        prefs.edit().putString("user_gemini_key", key).apply()
    }

    fun downloadVoiceModel() {
        viewModelScope.launch {
            voiceManager.downloadModel()
        }
    }

    fun clearAllChats() {
        // Clear chat history from persistent storage if implemented
        // For now, just clear SharedPreferences entry
        prefs.edit().remove("chat_history").apply()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return SettingsViewModel(application) as T
            }
        }
    }
}