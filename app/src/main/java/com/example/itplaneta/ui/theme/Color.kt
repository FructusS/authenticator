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

    background = Color(0xFFFDFDFE),
    onBackground = Color(0xFF1B1B1F),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B1F),

    surfaceVariant = Color(0xFFE1E3EA),
    onSurfaceVariant = Color(0xFF44474F),

    error = SemanticColors.Error,
    onError = Color.White,

    outline = Color(0xFF757780),
    outlineVariant = Color(0xFFC4C6D0)
)

// Dark Theme
val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF001F2A),

    secondary = SemanticColors.Secondary,
    onSecondary = Color(0xFF001F1C),

    tertiary = Color(0xFFCE93D8),
    onTertiary = Color(0xFF311634),

    error = Color(0xFFCF6679),
    onError = Color(0xFF370B1E),

    background = Color(0xFF101418),
    onBackground = Color(0xFFE1E3E8),

    surface = Color(0xFF111417),
    onSurface = Color(0xFFE1E3E8),

    surfaceVariant = Color(0xFF262A30),
    onSurfaceVariant = Color(0xFFC2C6CF),

    outline = Color(0xFF8A919C),
    outlineVariant = Color(0xFF414852)
)
