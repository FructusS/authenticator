package com.example.itplaneta.ui.screens.settings.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.itplaneta.R
import com.example.itplaneta.ui.screens.settings.AppTheme
import com.example.itplaneta.ui.screens.settings.SettingsViewModel

@Composable
fun ThemeOptions(
    selectedTheme: AppTheme, onThemeSelected: (AppTheme) -> Unit
) {
    Text(
        text = stringResource(id = R.string.theme),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    val themeItems = listOf(AppTheme.Auto, AppTheme.Light, AppTheme.Dark)

    themeItems.forEach { itemTheme ->
        val labelRes = when (itemTheme) {
            AppTheme.Auto -> R.string.theme_auto
            AppTheme.Dark -> R.string.theme_dark
            AppTheme.Light -> R.string.theme_light
        }

        ListItem(headlineContent = { Text(stringResource(id = labelRes)) }, trailingContent = {
            RadioButton(
                selected = selectedTheme == itemTheme,
                onClick = { onThemeSelected(itemTheme) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }, modifier = Modifier.clickable { onThemeSelected(itemTheme) })
    }
}
