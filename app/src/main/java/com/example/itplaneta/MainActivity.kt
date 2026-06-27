package com.example.itplaneta

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.FragmentActivity
import com.example.itplaneta.domain.IAppSettingsRepository
import com.example.itplaneta.ui.theme.AppTheme
import com.example.itplaneta.ui.theme.AuthenticatorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var appSettingsRepository: IAppSettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val theme = appSettingsRepository.themeFlow.collectAsState(initial = AppTheme.Auto)
            AuthenticatorTheme(theme.value) {
                AuthenticatorApp()
            }
        }
    }
}


