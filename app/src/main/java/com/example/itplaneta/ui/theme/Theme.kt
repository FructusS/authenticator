package com.example.itplaneta.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.itplaneta.ui.screens.settings.AppTheme

@Composable
fun AuthenticatorTheme(
    appTheme: AppTheme,
    content: @Composable (() -> Unit)
) {

    val darkTheme = when (appTheme) {
        AppTheme.Auto -> isSystemInDarkTheme()
        AppTheme.Dark -> true
        AppTheme.Light -> false
    }

    val context = LocalContext.current
    val colorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        } else {
            if (darkTheme) DarkColors else LightColors
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
