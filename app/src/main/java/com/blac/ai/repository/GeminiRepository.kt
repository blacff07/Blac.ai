package com.blac.ai.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.blac.ai.BuildConfig
import com.blac.ai.data.ToggleOptions
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import java.io.IOException

class GeminiRepository(private val context: Context) {
    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun getApiKey(): String {
        val userKey = prefs.getString("user_gemini_key", "")
        return if (!userKey.isNullOrBlank()) userKey else "AIzaSyAwTyMf4G1iTaolser_HJd_EFpNDfh_5Kg"
    }

    suspend fun sendMessage(prompt: String, options: ToggleOptions = ToggleOptions()): String {
        val finalPrompt = buildString {
            if (options.thinkMode) appendLine("(Think step by step)")
            if (options.realTimeSearch) appendLine("(Use live web data if needed)")
            if (options.codeMode) appendLine("(Format as code with explanations)")
            appendLine(prompt)
        }

        val config = GenerationConfig.builder()
            .apply {
                if (options.thinkMode) {
                    temperature = 0.1f
                    maxOutputTokens = 8192
                } else {
                    temperature = 0.7f
                    maxOutputTokens = 4096
                }
            }
            .build()

        val model = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = getApiKey(),
            generationConfig = config
        )

        return withContext(Dispatchers.IO) {
            try {
                withTimeout(30000) { // 30 seconds timeout
                    val response = model.generateContent(finalPrompt)
                    response.text ?: "No response"
                }
            } catch (e: TimeoutCancellationException) {
                "Request timed out. Please try again."
            } catch (e: IOException) {
                "Network error: ${e.localizedMessage}"
            } catch (e: Exception) {
                "Error: ${e.localizedMessage}"
            }
        }
    }

    fun saveUserKey(key: String) {
        prefs.edit().putString("user_gemini_key", key).apply()
    }
}