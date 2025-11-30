package com.example.itplaneta.ui.screens.account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.core.utils.Result
import com.example.itplaneta.data.repository.AccountRepository
import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.domain.AccountInputDto
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
) : BaseViewModel<AccountUiState, UiEvent>() {

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

            when (val result = accountRepository.addAccount(currentAccount.toAccount())) {
                is Result.Success -> {
                    updateState { it.copy(screenState = AccountScreenState.Success) }
                    postEvent(UiEvent.NavigateBack)
                }

                is Result.Error -> {
                    updateState {
                        it.copy(
                            screenState = AccountScreenState.Error(
                                result.message ?: "Failed"
                            )
                        )
                    }
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
                    updateState {
                        it.copy(
                            currentAccount = result.data.toRawAccount(),
                            screenState = AccountScreenState.Idle
                        )
                    }
                }

                is Result.Error -> {
                    updateState {
                        it.copy(
                            screenState = AccountScreenState.Error("")
                        )
                    }
                    Timber.e("Error loading account: ${result.message}")
                }

                is Result.Loading -> {}
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(
            screenState = AccountScreenState.Idle
        )
    }

// Private conversion functions

    private fun Account.toRawAccount(): AccountInputDto = AccountInputDto(
        id = id,
        digits = digits.toString(),
        counter = counter.toString(),
        secret = secret,
        label = label,
        issuer = issuer,
        period = period.toString(),
        algorithm = algorithm,
        tokenType = tokenType
    )

    private fun AccountInputDto.toAccount(): Account = Account(
        id = id,
        digits = digits.toInt(),
        counter = counter.toLong(),
        secret = secret,
        label = label,
        issuer = issuer,
        period = period.toInt(),
        algorithm = algorithm,
        tokenType = tokenType
    )

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