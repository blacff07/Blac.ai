package com.blac.ai.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import java.util.regex.Pattern

/**
 * Lightweight syntax highlighter that runs on device without external libraries.
 * Supports basic keywords for common languages.
 */
class CodeHighlighter {

    private val keywordPatterns = mapOf(
        "kotlin" to listOf("fun", "class", "val", "var", "if", "else", "for", "while", "return", "when", "is", "as", "in", "try", "catch", "finally"),
        "java" to listOf("public", "private", "protected", "class", "void", "int", "String", "if", "else", "for", "while", "return", "new", "try", "catch"),
        "python" to listOf("def", "class", "if", "elif", "else", "for", "while", "return", "import", "from", "as", "try", "except", "finally"),
        "javascript" to listOf("function", "var", "let", "const", "if", "else", "for", "while", "return", "try", "catch", "class", "import", "export")
    )

    fun highlight(code: String, language: String = ""): SpannableString {
        val spannable = SpannableString(code)
        val keywords = keywordPatterns[language.lowercase()] ?: keywordPatterns.values.flatten()

        // Simple keyword highlighting (naive, but lightweight)
        keywords.forEach { keyword ->
            val pattern = Pattern.compile("\\b$keyword\\b")
            val matcher = pattern.matcher(code)
            while (matcher.find()) {
                spannable.setSpan(
                    ForegroundColorSpan(android.graphics.Color.BLUE),
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        // String literals (very basic)
        val stringPattern = Pattern.compile("\"(.*?)\"")
        val stringMatcher = stringPattern.matcher(code)
        while (stringMatcher.find()) {
            spannable.setSpan(
                ForegroundColorSpan(android.graphics.Color.parseColor("#008000")),
                stringMatcher.start(),
                stringMatcher.end(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Comments
        val commentPattern = Pattern.compile("//.*|/\\*.*?\\*/")
        val commentMatcher = commentPattern.matcher(code)
        while (commentMatcher.find()) {
            spannable.setSpan(
                ForegroundColorSpan(android.graphics.Color.GRAY),
                commentMatcher.start(),
                commentMatcher.end(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannable
    }
}