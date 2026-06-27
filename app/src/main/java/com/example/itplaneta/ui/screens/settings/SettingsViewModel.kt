package com.example.itplaneta.ui.screens.settings

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.R
import com.example.itplaneta.core.utils.Result
import com.example.itplaneta.ui.theme.AppTheme
import com.example.itplaneta.domain.IAccountBackupManager
import com.example.itplaneta.data.SettingsManager
import com.example.itplaneta.domain.usecase.BiometricSettingsUseCase
import com.example.itplaneta.domain.usecase.SetBiometricEnabledResult
import com.example.itplaneta.ui.base.BaseViewModel
import com.example.itplaneta.ui.screens.pin.PinScenario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val backupManager: IAccountBackupManager,
    private val biometricSettingsUseCase: BiometricSettingsUseCase
) : BaseViewModel<SettingsUiState, SettingsUiEvent>() {


    override val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState())

    init {
        viewModelScope.launch {
            settingsManager.getTheme.collect { theme ->
                updateState { it.copy(selectedTheme = theme) }
            }
        }

        viewModelScope.launch {
            biometricSettingsUseCase.observeSettings().collect { settings ->
                updateState {
                    it.copy(
                        isPinEnabled = settings.isPinEnabled,
                        isBiometricEnabled = settings.isBiometricEnabled,
                        biometricAvailability = settings.availability
                    )
                }
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
                            screenState = SettingsScreenState.BackupSuccess, lastBackupMessage = msg
                        )
                    }
                    postEvent(
                        SettingsUiEvent.ShowMessage(
                            resId = msg.resId, arg = msg.arg
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
                            screenState = SettingsScreenState.BackupSuccess, lastBackupMessage = msg
                        )
                    }
                    emitEvent(
                        SettingsUiEvent.ShowMessage(
                            resId = msg.resId, arg = msg.arg
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
                    emitEvent(SettingsUiEvent.ShowMessage(resId = msg.resId))
                }

                Result.Loading -> {
                    updateState { it.copy(screenState = SettingsScreenState.LoadingBackup) }
                }
            }
        }
    }

    fun onPinCheckedChange(value: Boolean) {
        viewModelScope.launch {
            val mode = if (value) PinScenario.ENABLE else PinScenario.DISABLE
            emitEvent(SettingsUiEvent.NavigateToPinScreen(mode))
        }
    }

    fun onBiometricCheckedChange(value: Boolean) {
        viewModelScope.launch {
            if (value) {
                when (biometricSettingsUseCase.validateCanEnableBiometric()) {
                    SetBiometricEnabledResult.Success -> {
                        emitEvent(SettingsUiEvent.LaunchBiometricToEnable)
                    }

                    SetBiometricEnabledResult.PinRequired -> Unit

                    is SetBiometricEnabledResult.Unavailable -> Unit
                }
                return@launch
            }

            emitEvent(SettingsUiEvent.LaunchBiometricToDisable)
        }
    }

    fun onBiometricEnableAuthenticated() {
        viewModelScope.launch {
            when (biometricSettingsUseCase.setBiometricEnabled(true)) {
                SetBiometricEnabledResult.Success -> Unit

                SetBiometricEnabledResult.PinRequired -> Unit

                is SetBiometricEnabledResult.Unavailable -> Unit
            }
        }
    }

    fun onBiometricDisableAuthenticated() {
        viewModelScope.launch {
            biometricSettingsUseCase.setBiometricEnabled(false)
        }
    }

    fun onBiometricEnableFailed() = Unit

    fun onBiometricDisableFailed() = Unit
}
