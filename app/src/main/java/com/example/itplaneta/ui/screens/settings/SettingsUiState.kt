package com.example.itplaneta.ui.screens.settings

import com.example.itplaneta.data.backup.BackupMessage
import com.example.itplaneta.ui.theme.AppTheme
import com.example.itplaneta.ui.base.UiState

sealed class SettingsScreenState : UiState {
    object Idle : SettingsScreenState()
    object LoadingBackup : SettingsScreenState()
    object BackupSuccess : SettingsScreenState()
    data class BackupError(val message: String) : SettingsScreenState()
}

data class SettingsUiState(
    val screenState: SettingsScreenState = SettingsScreenState.Idle,
    val selectedTheme: AppTheme = AppTheme.Auto,
    val isPinEnabled: Boolean = false,
    val lastBackupMessage: BackupMessage? = null
) : UiState

