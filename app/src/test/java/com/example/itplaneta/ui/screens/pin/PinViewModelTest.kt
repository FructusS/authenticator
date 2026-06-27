package com.example.itplaneta.ui.screens.pin

import androidx.lifecycle.SavedStateHandle
import com.example.itplaneta.MainDispatcherRule
import com.example.itplaneta.R
import com.example.itplaneta.domain.IPinRepository
import com.example.itplaneta.ui.navigation.PinDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PinViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun disabledPinOpensAppOnStartup() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = createViewModel(FakePinRepository(enabled = false), PinScenario.UNLOCK)

        advanceUntilIdle()

        assertEquals(PinCodeScreenState.Success, viewModel.uiState.value.screenState)
    }

    @Test
    fun enablingPinStoresConfirmedPinAndEnablesLock() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakePinRepository()
        val viewModel = createViewModel(repository, PinScenario.ENABLE)
        val events = collectEvents(viewModel)
        runCurrent()

        enterPin(viewModel, "123456")
        advanceUntilIdle()
        enterPin(viewModel, "123456")
        advanceUntilIdle()

        assertEquals("123456", repository.savedPin)
        assertTrue(repository.enabled.value)
        assertEquals(PinCodeScreenState.Success, viewModel.uiState.value.screenState)
        assertTrue(events.contains(PinUiEvent.NavigateBackToSettings))
    }

    @Test
    fun firstPinMovesToConfirmationStage() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = createViewModel(FakePinRepository(), PinScenario.ENABLE)

        enterPin(viewModel, "123456")
        advanceUntilIdle()

        assertEquals(PinStage.CONFIRM, viewModel.uiState.value.stage)
        assertEquals("123456", viewModel.uiState.value.firstValue)
        assertEquals("", viewModel.uiState.value.value)
    }

    @Test
    fun mismatchedPinConfirmationShowsErrorAndDoesNotEnableLock() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakePinRepository()
        val viewModel = createViewModel(repository, PinScenario.ENABLE)
        val events = collectEvents(viewModel)
        runCurrent()

        enterPin(viewModel, "123456")
        advanceUntilIdle()
        enterPin(viewModel, "654321")
        advanceUntilIdle()

        assertFalse(repository.enabled.value)
        assertNull(repository.savedPin)
        assertEquals(PinStage.CONFIRM, viewModel.uiState.value.stage)
        assertEquals("123456", viewModel.uiState.value.firstValue)
        assertEquals("", viewModel.uiState.value.value)
        assertTrue(events.contains(PinUiEvent.ShowMessage(R.string.pin_error_mismatch)))
    }

    @Test
    fun incorrectPinDuringUnlockShowsErrorAndKeepsAppLocked() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = createViewModel(
            FakePinRepository(enabled = true, savedPin = "123456"),
            PinScenario.UNLOCK
        )
        val events = collectEvents(viewModel)
        runCurrent()

        enterPin(viewModel, "000000")
        advanceUntilIdle()

        assertFalse(events.contains(PinUiEvent.OpenApp))
        assertTrue(events.contains(PinUiEvent.ShowMessage(R.string.pin_error_invalid)))
        assertEquals("", viewModel.uiState.value.value)
    }

    @Test
    fun correctPinDuringUnlockOpensApp() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = createViewModel(
            FakePinRepository(enabled = true, savedPin = "123456"),
            PinScenario.UNLOCK
        )

        enterPin(viewModel, "123456")
        advanceUntilIdle()

        assertEquals(PinCodeScreenState.Success, viewModel.uiState.value.screenState)
    }

    @Test
    fun disablingPinRequiresCurrentPinAndDisablesLock() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakePinRepository(enabled = true, savedPin = "123456")
        val viewModel = createViewModel(repository, PinScenario.DISABLE)
        val events = collectEvents(viewModel)
        runCurrent()

        enterPin(viewModel, "123456")
        advanceUntilIdle()

        assertFalse(repository.enabled.value)
        assertEquals(PinCodeScreenState.Success, viewModel.uiState.value.screenState)
        assertTrue(events.contains(PinUiEvent.NavigateBackToSettings))
    }

    @Test
    fun shortPinShowsValidationError() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = createViewModel(FakePinRepository(enabled = true), PinScenario.UNLOCK)
        val events = collectEvents(viewModel)
        runCurrent()

        enterPin(viewModel, "123")
        viewModel.onSubmit()
        advanceTimeBy(500)

        assertTrue(events.contains(PinUiEvent.ShowMessage(R.string.pin_error_length)))
    }

    private fun createViewModel(
        repository: FakePinRepository,
        scenario: PinScenario
    ): PinViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf(PinDestination.modeArg to scenario.name)
        )
        return PinViewModel(repository, savedStateHandle)
    }

    private fun enterPin(viewModel: PinViewModel, pin: String) {
        pin.forEach(viewModel::onDigitClick)
    }

    private fun kotlinx.coroutines.test.TestScope.collectEvents(
        viewModel: PinViewModel
    ): MutableList<PinUiEvent> {
        val events = mutableListOf<PinUiEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiEvent.toList(events)
        }
        return events
    }

    private class FakePinRepository(
        enabled: Boolean = false,
        savedPin: String? = null
    ) : IPinRepository {
        val enabled = MutableStateFlow(enabled)
        var savedPin: String? = savedPin

        override val isPinEnabledFlow: Flow<Boolean> = this.enabled
        override val isBiometricEnabledFlow: Flow<Boolean> = MutableStateFlow(false)

        override suspend fun setPinEnabled(enabled: Boolean) {
            this.enabled.value = enabled
        }

        override suspend fun savePin(pin: String) {
            savedPin = pin
        }

        override suspend fun isPinValid(input: String): Boolean {
            return savedPin == input
        }
    }
}
