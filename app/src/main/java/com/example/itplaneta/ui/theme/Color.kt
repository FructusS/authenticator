package com.example.itplaneta.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Semantic Colors
object SemanticColors {
    val Primary = Color(0xFF2196F3)
    val PrimaryDark = Color(0xFF1565C0)
    val Secondary = Color(0xFF03DAC5)
    val Error = Color(0xFFB00020)
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFF9800)
}

// Light Theme
val LightColors = lightColorScheme(
    primary = SemanticColors.Primary,
    onPrimary = Color.White,
    secondary = SemanticColors.Secondary,
    onSecondary = Color.Black,
    error = SemanticColors.Error,
    onError = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000)
)

// Dark Theme
val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color.Black,
    secondary = SemanticColors.Secondary,
    onSecondary = Color.Black,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFFFFFFF)
)
