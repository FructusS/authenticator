package com.example.itplaneta.ui.screens.pin

import com.example.itplaneta.ui.base.UiState

enum class PinScenario {
    UNLOCK,
    ENABLE,
    DISABLE;

    companion object {
        fun fromName(name: String?): PinScenario =
            entries.firstOrNull { it.name == name } ?: UNLOCK
    }
}

enum class PinStage {
    INPUT,
    CONFIRM
}

sealed class PinCodeScreenState : UiState {
    object Idle : PinCodeScreenState()
    object Success : PinCodeScreenState()
}

data class PinUiState(
    val scenario: PinScenario,
    val stage: PinStage = PinStage.INPUT,
    val firstValue: String? = null,
    val value: String = "",
    val isError: Boolean = false,
    val canUseBiometric: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isPinEnabled: Boolean = false,
    val isInputLocked: Boolean = false,
    val screenState: PinCodeScreenState = PinCodeScreenState.Idle
)
