package com.example.itplaneta

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.itplaneta.ui.screens.settings.AppTheme
import com.example.itplaneta.ui.screens.settings.SettingsManager
import com.example.itplaneta.ui.theme.AuthenticatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsManager = SettingsManager(LocalContext.current)
            val theme = settingsManager.getTheme.collectAsState(initial = AppTheme.Auto)
            AuthenticatorTheme(
                darkTheme = when (theme.value) {
                    AppTheme.Light -> false
                    AppTheme.Dark -> true
                    AppTheme.Auto -> isSystemInDarkTheme()
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AuthenticatorApp()
                }
            }
        }
    }


}
//fun NavController.navigate(route: String){
//    popBackStack(route = route, inclusive = true)
//
//}

