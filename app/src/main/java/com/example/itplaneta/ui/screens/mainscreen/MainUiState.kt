package com.example.itplaneta.ui.screens.mainscreen

import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.ui.base.UiState

sealed class MainScreenState : UiState {
    object Idle : MainScreenState()
    object Loading : MainScreenState()
    object Success : MainScreenState()

    data class Error(val message: String) : MainScreenState()
}

data class MainUiState(
    val screenState: MainScreenState = MainScreenState.Idle,
    val accounts: List<Account> = emptyList(),
    val codes: Map<Int, String> = emptyMap(),
    val timerProgresses: Map<Int, Float> = emptyMap(),
    val timerValues: Map<Int, Long> = emptyMap(),
    val isFabExpanded: Boolean = false,
    val deleteDialogAccount: Account? = null
) : UiState
