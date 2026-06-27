package com.example.itplaneta

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.example.itplaneta.ui.theme.AppTheme
import com.example.itplaneta.data.SettingsManager
import com.example.itplaneta.ui.theme.AuthenticatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsManager = SettingsManager(LocalContext.current)
            val theme = settingsManager.getTheme.collectAsState(initial = AppTheme.Auto)
            AuthenticatorTheme(theme.value) {
                AuthenticatorApp()
            }
        }
    }
}


