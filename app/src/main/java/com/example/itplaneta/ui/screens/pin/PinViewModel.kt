package com.example.itplaneta.ui.screens.pin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.R
import com.example.itplaneta.domain.IPinRepository
import com.example.itplaneta.domain.validation.PinValidator
import com.example.itplaneta.ui.base.BaseViewModel
import com.example.itplaneta.ui.navigation.PinDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinViewModel @Inject constructor(
    private val pinRepository: IPinRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<PinUiState, PinUiEvent>() {
    private val startMode: PinScenario =
        PinScenario.fromName(savedStateHandle.get<String>(PinDestination.modeArg))

    override val _uiState = MutableStateFlow(PinUiState(startMode))

    init {
        viewModelScope.launch {
            pinRepository.isBiometricEnabledFlow.collect { enabled ->
                updateState { it.copy(isBiometricEnabled = enabled) }
            }
        }

        if (startMode == PinScenario.UNLOCK) {
            viewModelScope.launch {
                val enabled = pinRepository.isPinEnabledFlow.first()
                updateState { it.copy(isPinEnabled = enabled) }
                if (!enabled) {
                    openApp()
                }
            }
        }
    }

    fun onBiometricAvailability(canUse: Boolean) {
        updateState { it.copy(canUseBiometric = canUse) }
    }

    fun onBiometricRequested() {
        val state = uiState.value
        if (state.canUseBiometric &&
            state.isBiometricEnabled &&
            state.scenario == PinScenario.UNLOCK &&
            state.isPinEnabled
        ) {
            postEvent(PinUiEvent.LaunchBiometric)
        }
    }

    fun onBiometricSuccess() {
        if (uiState.value.scenario == PinScenario.UNLOCK) {
            openApp()
        }
    }

    fun onBiometricError() = Unit

    fun onDigitClick(digit: Char) {
        var shouldSubmit = false
        updateState { state ->
            if (state.isInputLocked ||
                state.screenState == PinCodeScreenState.Success ||
                state.value.length >= PinValidator.PIN_LENGTH ||
                !digit.isDigit()
            ) {
                state
            } else {
                val nextValue = state.value + digit
                shouldSubmit = nextValue.length == PinValidator.PIN_LENGTH
                state.copy(value = nextValue)
            }
        }
        if (shouldSubmit) {
            onSubmit()
        }
    }

    fun onBackspaceLongClick() {
        updateState { state ->
            if (state.value.isEmpty() || state.isInputLocked) state else state.copy(value = "")
        }
    }

    fun onBackspaceClick() {
        updateState { state ->
            if (state.value.isEmpty() || state.isInputLocked) {
                state
            } else {
                state.copy(value = state.value.dropLast(1))
            }
        }
    }

    fun onSubmit() {
        val state = uiState.value
        if (state.isInputLocked || state.screenState == PinCodeScreenState.Success) {
            return
        }

        if (!PinValidator.isValid(state.value)) {
            showPinErrorAndClear(R.string.pin_error_length)
            return
        }

        when (state.scenario) {
            PinScenario.UNLOCK -> handleUnlock()
            PinScenario.DISABLE -> handleDisable()
            PinScenario.ENABLE -> handleEnable()
        }
    }

    private fun handleUnlock() {
        val pin = uiState.value.value
        viewModelScope.launch {
            updateState { it.copy(isInputLocked = true) }
            val valid = pinRepository.isPinValid(pin)
            if (valid) {
                openApp()
            } else {
                showPinErrorAndClear(R.string.pin_error_invalid)
            }
        }
    }

    private fun handleDisable() {
        val pin = uiState.value.value
        viewModelScope.launch {
            updateState { it.copy(isInputLocked = true) }
            val valid = pinRepository.isPinValid(pin)
            if (valid) {
                pinRepository.setPinEnabled(false)
                updateState {
                    it.copy(
                        value = "",
                        isError = false,
                        isInputLocked = true,
                        isPinEnabled = false,
                        screenState = PinCodeScreenState.Success
                    )
                }
                delay(PinAnimationConstants.SUCCESS_DELAY_MS)
                emitEvent(PinUiEvent.NavigateBackToSettings)
            } else {
                showPinErrorAndClear(R.string.pin_error_invalid)
            }
        }
    }

    private fun handleEnable() {
        when (uiState.value.stage) {
            PinStage.INPUT -> saveFirstPin()
            PinStage.CONFIRM -> confirmNewPin()
        }
    }

    private fun saveFirstPin() {
        val pin = uiState.value.value
        viewModelScope.launch {
            updateState {
                it.copy(
                    isError = false,
                    isInputLocked = true,
                    screenState = PinCodeScreenState.Success
                )
            }
            delay(PinAnimationConstants.SUCCESS_DELAY_MS)
            updateState {
                it.copy(
                    firstValue = pin,
                    value = "",
                    stage = PinStage.CONFIRM,
                    isError = false,
                    isInputLocked = false,
                    screenState = PinCodeScreenState.Idle
                )
            }
        }
    }

    private fun confirmNewPin() {
        val state = uiState.value
        val first = state.firstValue
        val confirm = state.value

        if (first == null) {
            updateState {
                it.copy(stage = PinStage.INPUT, value = "", firstValue = null, isError = false)
            }
            return
        }

        if (confirm != first) {
            updateState {
                it.copy(value = "")
            }
            showPinErrorAndClear(R.string.pin_error_mismatch)
            return
        }

        viewModelScope.launch {
            updateState { it.copy(isInputLocked = true) }
            pinRepository.savePin(confirm)
            pinRepository.setPinEnabled(true)
            updateState {
                it.copy(
                    value = "",
                    firstValue = null,
                    isError = false,
                    isInputLocked = true,
                    isPinEnabled = true,
                    screenState = PinCodeScreenState.Success
                )
            }
            delay(PinAnimationConstants.SUCCESS_DELAY_MS)
            emitEvent(PinUiEvent.NavigateBackToSettings)
        }
    }

    private fun showPinErrorAndClear(messageRes: Int) {
        viewModelScope.launch {
            updateState { it.copy(isError = true, value = "", isInputLocked = true) }
            emitEvent(PinUiEvent.ShowMessage(messageRes))
            delay(PinAnimationConstants.ERROR_FEEDBACK_DELAY_MS)
            updateState { it.copy(isError = false, isInputLocked = false) }
        }
    }

    private fun openApp() {
        updateState {
            it.copy(
                value = "",
                isError = false,
                isInputLocked = true,
                screenState = PinCodeScreenState.Success
            )
        }
    }
}
