package com.blac.ai.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import io.noties.prism4j.Prism4j
import io.noties.prism4j.annotations.PrismBundle
import io.noties.prism4j.bundler.Prism4jBundler

@PrismBundle
class CodeHighlighter {
    private val prism4j: Prism4j
    private val grammarLocator = Prism4jBundler.create()

    init {
        prism4j = Prism4j(grammarLocator)
    }

    fun highlight(code: String, language: String = ""): SpannableString {
        val detectedLanguage = when {
            language.isNotBlank() -> language
            code.contains("fun ") || code.contains("class ") -> "kotlin"
            code.contains("def ") || code.contains("import ") -> "python"
            code.contains("function") || code.contains("var ") -> "javascript"
            code.contains("public static") -> "java"
            else -> "text"
        }

        val grammar = prism4j.grammar(detectedLanguage)
            ?: prism4j.grammar("text") ?: return SpannableString(code)

        val node = prism4j.tokenize(code, grammar)
        val spannable = SpannableString(code)
        
        highlightNode(node, spannable)
        return spannable
    }

    private fun highlightNode(node: Prism4j.Node, spannable: SpannableString) {
        // Apply color to current node
        val color = when (node.type()) {
            "keyword" -> Color.BLUE
            "string" -> Color.parseColor("#008000")
            "comment" -> Color.GRAY
            "function" -> Color.MAGENTA
            "number" -> Color.RED
            "operator", "punctuation" -> Color.DKGRAY
            else -> null
        }

        color?.let {
            spannable.setSpan(
                ForegroundColorSpan(it),
                node.start(),
                node.end(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Process children recursively
        node.children()?.forEach { child ->
            highlightNode(child, spannable)
        }
    }
}