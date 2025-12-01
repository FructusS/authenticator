package com.example.itplaneta.ui.screens.account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.core.utils.Result
import com.example.itplaneta.data.repository.AccountRepository
import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.domain.AccountInputDto
import com.example.itplaneta.domain.toAccount
import com.example.itplaneta.domain.toAccountInputDto
import com.example.itplaneta.domain.validation.AccountValidator
import com.example.itplaneta.domain.validation.FieldType
import com.example.itplaneta.ui.base.BaseViewModel
import com.example.itplaneta.ui.base.UiEvent
import com.example.itplaneta.ui.navigation.AccountDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val accountValidator: AccountValidator,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AccountUiState, AccountUiEvent>() {

    override val _uiState = MutableStateFlow(AccountUiState())

    private val currentAccount: AccountInputDto
        get() = uiState.value.currentAccount

    private val accountId: Int = savedStateHandle[AccountDestination.accountIdArg] ?: -1
    val isEditMode: Boolean get() = accountId != -1

    init {
        if (isEditMode) {
            loadAccountById(accountId)
        }
    }

    /**
     * Add new account to database
     */
    fun saveAccount() {
        viewModelScope.launch {
            // set loading
            updateState { it.copy(screenState = AccountScreenState.Loading) }

            val errors = accountValidator.validate(currentAccount)
            if (errors.values.any { it != null }) {
                updateState { it.copy(errors = errors, screenState = AccountScreenState.Idle) }
                return@launch
            }

            val result = if (isEditMode) {
                accountRepository.updateAccount(currentAccount.toAccount())
            } else {
                accountRepository.addAccount(currentAccount.toAccount())
            }
            when (result) {
                is Result.Success -> {
                    updateState {
                        it.copy(
                            screenState = AccountScreenState.Success,
                            originalAccount = currentAccount
                        )
                    }
                    postEvent(AccountUiEvent.NavigateBack)
                }

                is Result.Error -> {
                    updateState {
                        it.copy(
                            screenState = AccountScreenState.Error(
                                result.error
                            )
                        )
                    }

                    Timber.e("Error save account: ${result.exception}")
                }

                is Result.Loading -> { /* ignored */
                }
            }
        }
    }

    /**
     * Update current account from ui
     */
    fun updateAccountInputDto(field: FieldType, newValue: String) {
        viewModelScope.launch {
            val updatedAccount = when (field) {
                FieldType.LABEL -> currentAccount.copy(label = newValue)
                FieldType.SECRET -> currentAccount.copy(secret = newValue)
                FieldType.COUNTER -> currentAccount.copy(counter = newValue)
                FieldType.PERIOD -> currentAccount.copy(period = newValue)
                FieldType.DIGITS -> currentAccount.copy(digits = newValue)
                FieldType.ISSUER -> currentAccount.copy(issuer = newValue)
            }

            val fieldError = accountValidator.validateField(field, updatedAccount)
            updateState { state ->
                val newErrors = state.errors.toMutableMap()
                if (fieldError == null) newErrors.remove(field) else newErrors[field] = fieldError
                state.copy(
                    currentAccount = updatedAccount, errors = newErrors
                )
            }
        }
    }

    /**
     * Load account by ID for editing
     */
    fun loadAccountById(accountId: Int) {
        viewModelScope.launch {

            updateState { it.copy(screenState = AccountScreenState.Loading) }

            when (val result = accountRepository.getAccountById(accountId)) {
                is Result.Success -> {
                    val accountDto = result.data.toAccountInputDto()
                    updateState {
                        it.copy(
                            currentAccount = accountDto,
                            originalAccount = accountDto,
                            screenState = AccountScreenState.Idle
                        )
                    }
                }

                is Result.Error -> {
                    updateState {
                        it.copy(
                            screenState = AccountScreenState.Error(result.error)
                        )
                    }
                    Timber.e("Error loading account: ${result.exception}")
                }

                is Result.Loading -> {}
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        updateState { it.copy(screenState = AccountScreenState.Idle) }
    }

    fun onBackPressed() {
        if (uiState.value.hasUnsavedChanges) {
            updateState { it.copy(showUnsavedChangesDialog = true) }
        } else {
            postEvent(AccountUiEvent.NavigateBack)
        }
    }

    fun dismissUnsavedChangesDialog() {
        updateState { it.copy(showUnsavedChangesDialog = false) }
    }

    fun discardChanges() {
        updateState { it.copy(showUnsavedChangesDialog = false) }
        postEvent(AccountUiEvent.NavigateBack)
    }


    fun updateOtpTypeAccount(otpType: OtpType) {
        updateState { state ->
            val updatedAccount = state.currentAccount.copy(tokenType = otpType)
            state.copy(currentAccount = updatedAccount)
        }
    }

    fun updateAlgorithmAccount(algorithm: OtpAlgorithm) {
        updateState { state ->
            val updatedAccount = state.currentAccount.copy(algorithm = algorithm)
            state.copy(currentAccount = updatedAccount)
        }
    }
}