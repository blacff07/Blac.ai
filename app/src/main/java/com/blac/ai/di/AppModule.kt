package com.blac.ai.di

import android.content.Context
import com.blac.ai.repository.GeminiRepository
import com.blac.ai.utils.OcrHelper
import com.blac.ai.utils.SecurePrefs
import com.blac.ai.utils.VoiceManager

// Simple object to provide dependencies (manual DI)
object AppModule {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    fun provideGeminiRepository(): GeminiRepository = GeminiRepository(applicationContext)
    fun provideOcrHelper(): OcrHelper = OcrHelper()
    fun provideVoiceManager(): VoiceManager = VoiceManager(applicationContext)
    fun provideSecurePrefs() = SecurePrefs.getInstance(applicationContext)
}