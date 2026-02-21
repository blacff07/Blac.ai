package com.blac.ai.data

import android.graphics.Bitmap

sealed class Attachment {
    data class Image(val uri: String, val bitmap: Bitmap?) : Attachment()
    data class File(val uri: String, val fileName: String, val mimeType: String) : Attachment()
}