package com.blac.ai.utils

import android.text.SpannableString

/**
 * Temporary simplified highlighter that doesn't require external libraries.
 * 
 * This version just returns the original text without any syntax coloring.
 * Syntax highlighting will be added back once the build is stable and
 * the Prism4j dependency issues are resolved.
 * 
 * All other app features (chat, OCR, voice, file upload) continue to work.
 */
class CodeHighlighter {
    
    /**
     * Returns the code as a plain SpannableString without any highlighting.
     * 
     * @param code The code text to display
     * @param language The programming language (ignored in this version)
     * @return SpannableString containing the original code
     */
    fun highlight(code: String, language: String = ""): SpannableString {
        // Simply return the code without any highlighting
        // This allows the app to build and function while we debug Prism4j
        return SpannableString(code)
    }
}