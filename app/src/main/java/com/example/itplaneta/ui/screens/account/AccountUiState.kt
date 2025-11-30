package com.example.itplaneta.ui.screens.account

import com.example.itplaneta.domain.AccountInputDto
import com.example.itplaneta.domain.validation.AccountFieldError
import com.example.itplaneta.domain.validation.FieldType
import com.example.itplaneta.ui.base.UiState

/**
 * Sealed class for account screen states
 * Manages loading, success, and error states
 */
sealed class AccountScreenState : UiState {
    object Idle : AccountScreenState()
    object Loading : AccountScreenState()
    object Success : AccountScreenState()
    data class Error(val message: String) : AccountScreenState()
}

/**
 * UI state for account form
 */
data class AccountUiState(
    val currentAccount: AccountInputDto = AccountInputDto(),
    val screenState: AccountScreenState = AccountScreenState.Idle,
    val errors: Map<FieldType, AccountFieldError?> = emptyMap(),
) : UiState
