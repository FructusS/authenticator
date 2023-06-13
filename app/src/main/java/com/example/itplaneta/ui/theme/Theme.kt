package com.example.itplaneta.ui.theme

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
    onPrimary = onPrimaryDarkMode,
    onSecondary = onSecondaryDarkMode,
    onBackground = onBackgroundDarkMode,
    onSurface = onSurfaceDarkMode,
    onError = onErrorDarkMode,
)

private val LightColorPalette = lightColors(
    primary = primaryLightMode,
    primaryVariant = primaryVariantLightMode,
    secondary = secondaryLightMode,
    background = backgroundLightMode,
    error = errorLightMode,
    secondaryVariant = secondaryVariantLightMode,
    surface = surfaceLightMode,
    onPrimary = onPrimaryLightMode,
    onSecondary = onSecondaryLightMode,
    onBackground = onBackgroundLightMode,
    onSurface = onSurfaceLightMode,
    onError = onErrorLightMode,
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