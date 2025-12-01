package com.example.itplaneta.ui.screens.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.R
import com.example.itplaneta.core.utils.Result
import com.example.itplaneta.data.backup.BackupMessage
import com.example.itplaneta.data.backup.BackupResult
import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.domain.IAccountBackupManager
import com.example.itplaneta.domain.IAccountRepository
import com.example.itplaneta.ui.base.BaseViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.*
import javax.inject.Inject
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val backupManager: IAccountBackupManager
) : BaseViewModel<SettingsUiState, SettingsUiEvent>() {


    override val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState())

    init {
        viewModelScope.launch {
            settingsManager.getTheme.collect { theme ->
                updateState { it.copy(selectedTheme = theme) }
            }
        }
    }
    fun saveTheme(theme: AppTheme) {
        viewModelScope.launch {
            try {
                settingsManager.saveTheme(theme)
                updateState { it.copy(selectedTheme = theme) }
                Timber.d("Theme saved: $theme")
            } catch (e: Exception) {
                Timber.e(e, "Error saving theme")
                postEvent(SettingsUiEvent.ShowMessage(R.string.backup_error_unexpected))
            }
        }
    }

    fun saveBackupToExternal(uri: Uri) {
        viewModelScope.launch {
            updateState { it.copy(screenState = SettingsScreenState.LoadingBackup) }

            when (val result = backupManager.backupToUri(uri)) {
                is Result.Success -> {
                    val msg = result.data
                    updateState {
                        it.copy(
                            screenState = SettingsScreenState.BackupSuccess,
                            lastBackupMessage = msg
                        )
                    }
                    postEvent(
                        SettingsUiEvent.ShowMessage(
                            resId = msg.resId,
                            arg = msg.arg
                        )
                    )
                }

                is Result.Error -> {
                    val msg = result.error
                    updateState {
                        it.copy(
                            screenState = SettingsScreenState.BackupError("backup error"),
                            lastBackupMessage = msg
                        )
                    }
                    postEvent(SettingsUiEvent.ShowMessage(resId = msg.resId))
                }

                Result.Loading -> {
                    updateState { it.copy(screenState = SettingsScreenState.LoadingBackup) }
                }
            }
        }
    }

    fun restoreBackupFromExternal(uri: Uri) {
        viewModelScope.launch {
            updateState { it.copy(screenState = SettingsScreenState.LoadingBackup) }

            when (val result = backupManager.restoreFromUri(uri)) {
                is Result.Success -> {
                    val msg = result.data
                    updateState {
                        it.copy(
                            screenState = SettingsScreenState.BackupSuccess,
                            lastBackupMessage = msg
                        )
                    }
                    postEvent(
                        SettingsUiEvent.ShowMessage(
                            resId = msg.resId,
                            arg = msg.arg
                        )
                    )
                }

                is Result.Error -> {
                    val msg = result.error
                    updateState {
                        it.copy(
                            screenState = SettingsScreenState.BackupError("restore error"),
                            lastBackupMessage = msg
                        )
                    }
                    postEvent(SettingsUiEvent.ShowMessage(resId = msg.resId))
                }

                Result.Loading -> {
                    updateState { it.copy(screenState = SettingsScreenState.LoadingBackup) }
                }
            }
        }
    }
}
