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
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountRepository: IAccountRepository, private val otpCodeManager: OtpCodeManager
) : BaseViewModel<MainUiState, MainUiEvent>() {

    override val _uiState = MutableStateFlow(MainUiState())

    private val accountsFlow = accountRepository.getAccounts()


    init {
        // стартуем генерацию OTP
        otpCodeManager.start(viewModelScope, accountsFlow)

        // аккаунты
        viewModelScope.launch {
            accountsFlow.collect { list ->
                updateState {
                    it.copy(
                        accounts = list, screenState = MainScreenState.Success
                    )
                }
            }
        }

        // коды
        viewModelScope.launch {
            otpCodeManager.codes.collect { codes ->
                updateState { it.copy(codes = codes) }
            }
        }

        // прогресс таймера
        viewModelScope.launch {
            otpCodeManager.timerProgresses.collect { progresses ->
                updateState { it.copy(timerProgresses = progresses) }
            }
        }

        // значения таймера
        viewModelScope.launch {
            otpCodeManager.timerValues.collect { values ->
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
