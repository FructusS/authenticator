package com.example.itplaneta.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = primaryDarkMode,
    primaryVariant = primaryVariantDarkMode,
    secondary = secondaryDarkMode,
    background = backgroundDarkMode,
    error = errorDarkMode,
    secondaryVariant = secondaryVariantDarkMode,
    surface = surfaceDarkMode,
)

private val LightColorPalette = lightColors(
    primary = primaryLightMode,
    primaryVariant = primaryVariantMode,
    secondary = secondaryLightMode,
    background = backgroundLightMode,
    error = errorLightMode,
    secondaryVariant = secondaryVariantLightMode,
    surface = surfaceLightMode,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ItplanetaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}