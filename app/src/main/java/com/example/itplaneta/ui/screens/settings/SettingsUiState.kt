package com.example.itplaneta.ui.screens.settings

import androidx.annotation.StringRes
import com.example.itplaneta.data.backup.BackupMessage
import com.example.itplaneta.ui.base.UiEvent
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
    val lastBackupMessage: BackupMessage? = null
) : UiState

