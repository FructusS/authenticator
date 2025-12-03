package com.example.itplaneta.ui.screens.mainscreen

import androidx.lifecycle.viewModelScope
import com.example.itplaneta.R
import com.example.itplaneta.core.otp.OtpCodeManager
import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.domain.IAccountRepository
import com.example.itplaneta.ui.base.BaseViewModel
import com.example.itplaneta.ui.base.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountRepository: IAccountRepository, private val otpCodeManager: OtpCodeManager
) : BaseViewModel<MainUiState, MainUiEvent>() {

    override val _uiState = MutableStateFlow(MainUiState())


    init {
        loadAccounts()
        subscribeToOtpData()
    }

    private fun loadAccounts() {
        val accountsFlow = accountRepository.getAccounts()

        // стартуем генерацию OTP
        otpCodeManager.start(accountsFlow)

        viewModelScope.launch {
            accountsFlow.catch { error ->
                Timber.e(error, "Error loading accounts")
                updateState {
                    it.copy(
                        screenState = MainScreenState.Error(
                            error.message ?: "Unknown error"
                        )
                    )
                }
            }.collect { accounts ->
                updateState {
                    it.copy(
                        accounts = accounts, screenState = MainScreenState.Success
                    )
                }
            }
        }
    }

    private fun subscribeToOtpData() {
        // Коды
        viewModelScope.launch {
            otpCodeManager.codes.catch { Timber.e(it, "Error in codes flow") }.collect { codes ->
                    updateState { it.copy(codes = codes) }
                }
        }

        // Прогресс таймера
        viewModelScope.launch {
            otpCodeManager.timerProgresses.catch { Timber.e(it, "Error in progresses flow") }
                .collect { progresses ->
                    updateState { it.copy(timerProgresses = progresses) }
                }
        }

        // Значения таймера
        viewModelScope.launch {
            otpCodeManager.timerValues.catch { Timber.e(it, "Error in timer values flow") }
                .collect { values ->
                    updateState { it.copy(timerValues = values) }
                }
        }
    }


    fun onFabToggle() {
        updateState { it.copy(isFabExpanded = !it.isFabExpanded) }
    }

    fun onRequestDelete(account: Account) {
        updateState { it.copy(deleteDialogAccount = account) }
    }

    fun onConfirmDelete() {
        val account = uiState.value.deleteDialogAccount ?: return
        viewModelScope.launch {
            accountRepository.deleteAccount(account)
            updateState { it.copy(deleteDialogAccount = null) }
        }
    }

    fun onDismissDeleteDialog() {
        updateState { it.copy(deleteDialogAccount = null) }
    }

    fun incrementHotpCounter(account: Account) {
        viewModelScope.launch {
            accountRepository.incrementHotpCounter(account)
        }
    }

    override fun onCleared() {
        otpCodeManager.stop()
        Timber.d("MainViewModel cleared")
        super.onCleared()
    }

    fun onCodeCopied() {
        postEvent(MainUiEvent.ShowMessage(R.string.copied))
    }
}
