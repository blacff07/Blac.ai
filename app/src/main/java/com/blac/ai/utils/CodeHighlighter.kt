package com.blac.ai.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import io.noties.prism4j.Prism4j
import io.noties.prism4j.bundler.Prism4jBundler
import io.noties.prism4j.GrammarLocator

class CodeHighlighter {
    private val prism4j: Prism4j
    private val grammarLocator: GrammarLocator

    init {
        grammarLocator = Prism4jBundler.create()
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
            ?: prism4j.grammar("text") // Fallback to plain text

        val node = prism4j.tokenize(code, grammar)
        val spannable = SpannableString(code)

        // Apply syntax highlighting
        applyHighlighting(spannable, node)
        
        return spannable
    }

    private fun applyHighlighting(spannable: SpannableString, node: Prism4j.Node) {
        // Process all nodes recursively
        processNode(spannable, node)
    }

    private fun processNode(spannable: SpannableString, node: Prism4j.Node) {
        // Get color based on node type
        val color = when (node.type()) {
            "keyword" -> Color.BLUE
            "string" -> Color.parseColor("#008000")
            "comment" -> Color.GRAY
            "function" -> Color.MAGENTA
            "number" -> Color.RED
            "operator" -> Color.BLACK
            "punctuation" -> Color.DKGRAY
            else -> null
        }

        // Apply color to this node
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
            processNode(spannable, child)
        }
    }
}