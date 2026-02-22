package com.blac.ai.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import io.noties.prism4j.Prism4j
import io.noties.prism4j.bundler.Prism4jBundler

class CodeHighlighter {
    private val prism4j = Prism4j()
    private val grammarLocator = Prism4jBundler.create()

    fun highlight(code: String, language: String = ""): SpannableString {
        val detectedLanguage = when {
            language.isNotBlank() -> language
            code.contains("fun ") || code.contains("class ") -> "kotlin"
            code.contains("def ") || code.contains("import ") -> "python"
            code.contains("function") || code.contains("var ") -> "javascript"
            code.contains("public static") -> "java"
            else -> "text"
        }

        val grammar = grammarLocator.grammar(detectedLanguage)
        val node = prism4j.grammar(grammar, code)
        val spannable = SpannableString(code)

        for (i in 0 until node.children().size()) {
            val child = node.children().get(i)
            val color = when (child.type()) {
                "keyword" -> Color.BLUE
                "string" -> Color.parseColor("#008000")
                "comment" -> Color.GRAY
                "function" -> Color.MAGENTA
                "number" -> Color.RED
                else -> null
            }
            color?.let {
                spannable.setSpan(
                    ForegroundColorSpan(it),
                    child.start(),
                    child.end(),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return spannable
    }
}