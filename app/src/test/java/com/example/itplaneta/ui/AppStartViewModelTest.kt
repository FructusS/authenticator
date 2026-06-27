package com.example.itplaneta.ui

import com.example.itplaneta.MainDispatcherRule
import com.example.itplaneta.domain.IPinRepository
import com.example.itplaneta.ui.navigation.MainDestination
import com.example.itplaneta.ui.navigation.PinDestination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppStartViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun disabledPinStartsAtMainScreen() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = AppStartViewModel(FakePinRepository(enabled = false))

        advanceUntilIdle()

        assertEquals(MainDestination.route, viewModel.startDestination.value)
    }

    @Test
    fun enabledPinStartsAtPinScreen() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = AppStartViewModel(FakePinRepository(enabled = true))

        advanceUntilIdle()

        assertEquals(PinDestination.route, viewModel.startDestination.value)
    }

    private class FakePinRepository(
        enabled: Boolean
    ) : IPinRepository {
        override val isPinEnabledFlow: Flow<Boolean> = MutableStateFlow(enabled)
        override val isBiometricEnabledFlow: Flow<Boolean> = MutableStateFlow(false)

        override suspend fun setPinEnabled(enabled: Boolean) = Unit
        override suspend fun setBiometricEnabled(enabled: Boolean) = Unit
        override suspend fun savePin(pin: String) = Unit
        override suspend fun isPinValid(input: String): Boolean = false
    }
}
