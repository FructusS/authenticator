package com.example.itplaneta.ui.screens.settings

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsManager: SettingsManager) : ViewModel() {

    private val _themeState = MutableStateFlow(settingsManager.getTheme)
    val themeState = _themeState.asStateFlow()
    fun saveTheme(theme: AppTheme){
        viewModelScope.launch {
            settingsManager.saveTheme(theme)
        }
    }


}