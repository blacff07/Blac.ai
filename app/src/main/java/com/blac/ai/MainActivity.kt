package com.blac.ai

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blac.ai.ui.ChatScreen
import com.blac.ai.ui.SettingsScreen
import com.blac.ai.ui.theme.BlacaiTheme
import kotlinx.coroutines.CoroutineExceptionHandler
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private val crashHandler = CoroutineExceptionHandler { _, throwable ->
        logCrash(throwable)
        runOnUiThread {
            Toast.makeText(this, "App encountered an error. Please restart.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Install global uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logCrash(throwable)
            // Show error UI
            runOnUiThread {
                setContent {
                    BlacaiTheme {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "Sorry, the app crashed.\nError: ${throwable.message}",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }

        try {
            setContent {
                BlacaiTheme {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "chat") {
                        composable("chat") {
                            ChatScreen(navController = navController)
                        }
                        composable("settings") {
                            SettingsScreen(onNavigateBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logCrash(e)
            // Fallback UI
            setContent {
                BlacaiTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Failed to initialize UI: ${e.message}",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }

    private fun logCrash(throwable: Throwable) {
        try {
            val crashFile = File(filesDir, "crash_log.txt")
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            FileWriter(crashFile, true).use { writer ->
                writer.appendLine("=== Crash at $timestamp ===")
                writer.appendLine("Message: ${throwable.message}")
                writer.appendLine("Stack trace:")
                throwable.printStackTrace(PrintWriter(writer))
                writer.appendLine("=======================\n")
            }
            Log.e("Blac.ai", "Crash logged", throwable)
        } catch (e: Exception) {
            // Ignore
        }
    }
}