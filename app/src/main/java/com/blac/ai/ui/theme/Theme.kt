package com.blac.ai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val primaryDark = Color(0xFF1E1E2F)
private val secondaryDark = Color(0xFF2D2D3A)
private val accent = Color(0xFF7C4DFF)

private val DarkColorScheme = darkColorScheme(
    primary = accent,
    secondary = secondaryDark,
    background = primaryDark,
    surface = secondaryDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun BlacaiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}