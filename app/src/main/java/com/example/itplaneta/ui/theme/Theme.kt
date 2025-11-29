package com.example.itplaneta.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun AuthenticatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColors  // From Color.kt (already Material 3)
        else -> LightColors      // From Color.kt (already Material 3)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,  // Material 3 compatible
        content = content
    )
}
