package com.example.itplaneta.ui.theme

import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

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
fun AuthenticatorTheme(darkTheme: Boolean , content: @Composable () -> Unit) {

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
