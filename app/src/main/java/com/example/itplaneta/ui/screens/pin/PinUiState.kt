package com.example.itplaneta.ui.screens.pin

import com.example.itplaneta.ui.base.UiState

/**
 * Состояние экрана ввода PIN-кода.
 *
 * Экран используется в трёх сценариях:
 * - UNLOCK  – ввод PIN при входе в приложение
 * - ENABLE  – включение PIN в настройках (ввод + подтверждение)
 * - DISABLE – отключение PIN в настройках (проверка текущего PIN)
 *
 * Для сценария ENABLE есть два шага:
 * - INPUT   – ввод нового PIN
 * - CONFIRM – повторное подтверждение того же PIN
 */

enum class PinScenario {
    UNLOCK,    // разблокировка при старте
    ENABLE,    // включение PIN в настройках
    DISABLE;    // выключение PIN в настройках

    companion object {
        fun fromName(name: String?): PinScenario =
            PinScenario.entries.firstOrNull { it.name == name } ?: DISABLE
    }
}

enum class PinStage {
    INPUT,     // ввод PIN
    CONFIRM    // подтверждение нового PIN (только для ENABLE)
}

sealed class PinCodeScreenState : UiState {
    object Idle : PinCodeScreenState()
    object Success : PinCodeScreenState()
}

data class PinUiState(
    // Текущий сценарий использования экрана (разблокировка / включение / выключение)
    val scenario: PinScenario,
    // Текущий шаг внутри сценария (для ENABLE: ввод или подтверждение)
    val stage: PinStage = PinStage.INPUT,
    val firstValue: String? = null,
    val value: String = "",
    val isError: Boolean = false, // отображение ошибки
    val canUseBiometric: Boolean = false,   // есть ли HW + зарегистрированные данные
    val isBiometricEnabled: Boolean = false, // включено ли в настройках
    val isPinEnabled: Boolean = false // включено ли в настройках
)
