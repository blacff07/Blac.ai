package com.blac.ai.utils

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.vosk.Model
import org.vosk.Recognizer
import org.json.JSONObject
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class VoiceManager(private val context: Context) {
    private var recognizer: Recognizer? = null
    private var audioRecord: AudioRecord? = null
    private var isListening = false
    private var listener: ((String) -> Unit)? = null

    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress = _downloadProgress.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading = _isDownloading.asStateFlow()

    private val _isModelReady = MutableStateFlow(false)
    val isModelReady = _isModelReady.asStateFlow()

    private val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private var downloadId: Long? = null

    // Vosk small English model (approx 40 MB)
    private val MODEL_URL = "https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip"
    private val MODEL_FILENAME = "vosk-model-small-en-us.zip"
    private val MODEL_DIR = "vosk-model-small-en-us-0.15" // extracted folder name

    init {
        checkExistingModel()
    }

    private fun checkExistingModel() {
        val modelDir = File(context.filesDir, MODEL_DIR)
        _isModelReady.value = modelDir.exists()
    }

    fun downloadModel() {
        if (_isModelReady.value) return

        val request = DownloadManager.Request(Uri.parse(MODEL_URL))
            .setTitle("Downloading Vosk model")
            .setDescription("Speech recognition model (~40 MB)")
            .setDestinationInExternalFilesDir(context, null, MODEL_FILENAME)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        downloadId = downloadManager.enqueue(request)
        _isDownloading.value = true

        // Register receiver to listen for download completion
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    queryDownloadStatus()
                    context?.unregisterReceiver(this)
                }
            }
        }
        ContextCompat.registerReceiver(context, receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_NOT_EXPORTED)

        // Start a periodic checker for progress
        Thread {
            while (_isDownloading.value) {
                queryDownloadStatus()
                Thread.sleep(1000)
            }
        }.start()
    }

    private fun queryDownloadStatus() {
        val query = DownloadManager.Query().setFilterById(downloadId ?: return)
        val cursor: Cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val bytesDownloaded = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val bytesTotal = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            if (bytesTotal > 0) {
                val progress = (bytesDownloaded * 100 / bytesTotal).toInt()
                _downloadProgress.value = progress
            }
            val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                _isDownloading.value = false
                extractModel()
            } else if (status == DownloadManager.STATUS_FAILED) {
                _isDownloading.value = false
            }
        }
        cursor.close()
    }

    private fun extractModel() {
        // For simplicity, assume the file is already extracted.
        // In a real app, you'd unzip the downloaded file.
        // For now, we'll just check for extracted directory.
        val modelDir = File(context.filesDir, MODEL_DIR)
        if (modelDir.exists()) {
            _isModelReady.value = true
        } else {
            // Actually unzip (requires ZipFile or similar) – omitted for brevity
            // You can use Android's ZipFile or a library.
            // Assume extraction happens.
        }
    }

    suspend fun initialize(): Boolean = suspendCancellableCoroutine { cont ->
        if (!_isModelReady.value) {
            cont.resume(false)
            return@suspendCancellableCoroutine
        }
        try {
            val model = Model(File(context.filesDir, MODEL_DIR).absolutePath)
            recognizer = Recognizer(model, 16000.0f)
            cont.resume(true)
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }

    fun startListening(onResult: (String) -> Unit) {
        if (!_isModelReady.value) return
        listener = onResult
        isListening = true
        val bufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
        audioRecord?.startRecording()

        Thread {
            val buffer = ByteArray(bufferSize)
            while (isListening) {
                val bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (bytesRead > 0 && recognizer?.acceptWaveForm(buffer, bytesRead) == true) {
                    val result = JSONObject(recognizer!!.result).getString("text")
                    if (result.isNotEmpty()) {
                        listener?.invoke(result)
                    }
                }
            }
        }.start()
    }

    fun stopListening() {
        isListening = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}