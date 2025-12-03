package com.example.itplaneta.ui.screens.pin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.ui.base.BaseViewModel
import com.example.itplaneta.data.SettingsManager
import com.example.itplaneta.ui.navigation.PinDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val settingsManager: SettingsManager, savedStateHandle: SavedStateHandle
) : BaseViewModel<PinUiState, PinUiEvent>() {

    private val startMode: PinScenario = PinScenario.fromName(savedStateHandle.get<String>(PinDestination.modeArg))

    override val _uiState = MutableStateFlow(PinUiState(startMode))

    init {
        viewModelScope.launch {
            // флаг "биометрия включена пользователем" из DataStore
            settingsManager.isBiometricEnabledFlow.collect { enabled ->
                updateState { it.copy(isBiometricEnabled = enabled) }
            }
        }

        if (startMode == PinScenario.UNLOCK) {
            viewModelScope.launch {
                val enabled = settingsManager.isPinEnabledFlow.first()
                updateState { it.copy(isPinEnabled = enabled) }
                if (uiState.value.isPinEnabled) {
                    emitEvent(PinUiEvent.OpenApp)
                }
            }
        }
    }

    // вызываться из Composable после проверки BiometricManager
    fun onBiometricAvailability(canUse: Boolean) {
        updateState { it.copy(canUseBiometric = canUse) }
    }

    // кнопка/автостарт биометрии
    fun onBiometricRequested() {
        val s = uiState.value
        if (s.canUseBiometric && s.isBiometricEnabled && s.scenario == PinScenario.UNLOCK) {
            postEvent(PinUiEvent.LaunchBiometric)
        }
    }

    fun onBiometricSuccess() {
        // биометрия прошла — считается как успешный PIN UNLOCK
        when (uiState.value.scenario) {
            PinScenario.UNLOCK -> postEvent(PinUiEvent.OpenApp)
            // при желании можно использовать и для ENABLE/DISABLE как доп. проверку
            else -> { /* no-op */ }
        }
    }

    fun onBiometricError() {
        // ничего не делаем, остаётся PIN какfallback
        // можно добавить короткий blinkError() если хочешь подсветку
    }

    fun onDigitClick(digit: Char) {
        updateState { state ->
            if (state.value.length >= 6) state
            else state.copy(value = state.value + digit)
        }
    }

    fun onBackspaceLongClick() {
        updateState { state ->
            if (state.value.isEmpty()) state
            else state.copy(value = "")
        }
    }

    fun onBackspaceClick() {
        updateState { state ->
            if (state.value.isEmpty()) state
            else state.copy(value = state.value.dropLast(1))
        }
    }

    fun onSubmit() {
        val state = uiState.value
        if (state.value.isEmpty()) {
            showPinErrorAndClear()
            return
        }

        when (state.scenario) {
            PinScenario.UNLOCK -> handleUnlock()
            PinScenario.DISABLE -> handleDisable()
            PinScenario.ENABLE -> handleEnable()
        }
    }
    // --- UNLOCK при старте ---

    private fun handleUnlock() {
        val pin = uiState.value.value
        viewModelScope.launch {
            val valid = settingsManager.isPinValid(pin)
            if (valid) {
                updateState { it.copy(value = "", isError = false) }
                emitEvent(PinUiEvent.OpenApp)
            } else {
                showPinErrorAndClear()
            }
        }
    }

    // --- DISABLE в настройках ---

    private fun handleDisable() {
        val pin = uiState.value.value
        viewModelScope.launch {
            val valid = settingsManager.isPinValid(pin)
            if (valid) {
                settingsManager.setPinEnabled(false)
                updateState { it.copy(value = "", isError = false) }
                emitEvent(PinUiEvent.NavigateBackToSettings)
            } else {
                showPinErrorAndClear()
            }
        }
    }

    // --- ENABLE: ввод + подтверждение ---

    private fun handleEnable() {
        when (uiState.value.stage) {
            PinStage.INPUT -> saveFirstPin()
            PinStage.CONFIRM -> confirmNewPin()
        }
    }

    // шаг 1: ввод нового PIN
    private fun saveFirstPin() {
        val pin = uiState.value.value
        updateState {
            it.copy(
                firstValue = pin, value = "", stage = PinStage.CONFIRM, isError = false
            )
        }
    }

    // шаг 2: подтверждение PIN
    private fun confirmNewPin() {
        val state = uiState.value
        val first = state.firstValue
        val confirm = state.value

        if (first == null) {
            // что-то пошло не так — откат в начало сценария ENABLE
            updateState {
                it.copy(
                    stage = PinStage.INPUT, value = "", firstValue = null, isError = false
                )
            }
            return
        }

        if (confirm != first) {
            // не совпало — ошибка и возврат к первому шагу
            updateState {
                it.copy(
                    stage = PinStage.INPUT, value = "", firstValue = null
                )
            }
            showPinErrorAndClear()
            return
        }

        // всё ок — сохраняем PIN и включаем
        viewModelScope.launch {
            settingsManager.savePin(confirm)
            settingsManager.setPinEnabled(true)
            updateState {
                it.copy(
                    value = "",
                    firstValue = null,
                    stage = PinStage.INPUT,
                    isError = false
                )
            }
            emitEvent(PinUiEvent.NavigateBackToSettings)
        }
    }


    private fun showPinErrorAndClear() {
        viewModelScope.launch {
            updateState { it.copy(isError = true, value = "") }
            delay(500) // 0.5 сек подсветки
            updateState { it.copy(isError = false) }
        }
    }
}