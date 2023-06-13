package com.example.itplaneta.ui.screens.account

import androidx.annotation.StringRes
import com.example.itplaneta.R
import com.example.itplaneta.domain.RawAccount
import com.example.itplaneta.ui.base.UiState

data class AccountUiState(
    var account: RawAccount = RawAccount(),
    val errorType: ErrorType = ErrorType.Nothing,
    @StringRes val errorText: Int = R.string.app_name,
    val isEntryValid: Boolean = false
) : UiState

enum class ErrorType {
    LabelError,
    SecretError,
    DigitsError,
    CounterError,
    PeriodError,
    Nothing,
    IssuerError
}