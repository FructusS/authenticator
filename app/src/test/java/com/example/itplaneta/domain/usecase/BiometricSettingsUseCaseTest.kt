package com.example.itplaneta.domain.usecase

import androidx.fragment.app.FragmentActivity
import com.example.itplaneta.core.biometric.BiometricAvailability
import com.example.itplaneta.core.biometric.BiometricResult
import com.example.itplaneta.domain.IBiometricRepository
import com.example.itplaneta.domain.IPinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BiometricSettingsUseCaseTest {
    @Test
    fun enablingBiometricWhenPinEnabledStoresEnabledFlag() = runTest {
        val pinRepository = FakePinRepository(pinEnabled = true)
        val useCase = createUseCase(pinRepository, BiometricAvailability.Available)

        val result = useCase.setBiometricEnabled(true)

        assertEquals(SetBiometricEnabledResult.Success, result)
        assertTrue(pinRepository.biometricEnabled.value)
    }

    @Test
    fun validatingBiometricEnableDoesNotStoreEnabledFlagBeforeAuthentication() = runTest {
        val pinRepository = FakePinRepository(pinEnabled = true)
        val useCase = createUseCase(pinRepository, BiometricAvailability.Available)

        val result = useCase.validateCanEnableBiometric()

        assertEquals(SetBiometricEnabledResult.Success, result)
        assertFalse(pinRepository.biometricEnabled.value)
    }

    @Test
    fun enablingBiometricWithoutPinIsRejected() = runTest {
        val pinRepository = FakePinRepository(pinEnabled = false)
        val useCase = createUseCase(pinRepository, BiometricAvailability.Available)

        val result = useCase.setBiometricEnabled(true)

        assertEquals(SetBiometricEnabledResult.PinRequired, result)
        assertFalse(pinRepository.biometricEnabled.value)
    }

    @Test
    fun disablingBiometricStoresDisabledFlag() = runTest {
        val pinRepository = FakePinRepository(
            pinEnabled = true,
            biometricEnabled = true
        )
        val useCase = createUseCase(pinRepository, BiometricAvailability.Available)

        val result = useCase.setBiometricEnabled(false)

        assertEquals(SetBiometricEnabledResult.Success, result)
        assertFalse(pinRepository.biometricEnabled.value)
    }

    @Test
    fun disablingPinAutomaticallyDisablesBiometric() = runTest {
        val pinRepository = FakePinRepository(
            pinEnabled = true,
            biometricEnabled = true
        )

        pinRepository.setPinEnabled(false)

        assertFalse(pinRepository.isPinEnabledFlow.first())
        assertFalse(pinRepository.isBiometricEnabledFlow.first())
    }

    @Test
    fun unavailableBiometricIsExposedInSettingsState() = runTest {
        val useCase = createUseCase(
            FakePinRepository(pinEnabled = true),
            BiometricAvailability.NoHardware
        )

        val settings = useCase.observeSettings().first()

        assertEquals(BiometricAvailability.NoHardware, settings.availability)
        assertFalse(settings.canEnableBiometric)
    }

    @Test
    fun enablingUnavailableBiometricIsRejected() = runTest {
        val pinRepository = FakePinRepository(pinEnabled = true)
        val useCase = createUseCase(pinRepository, BiometricAvailability.NoHardware)

        val result = useCase.setBiometricEnabled(true)

        assertTrue(result is SetBiometricEnabledResult.Unavailable)
        assertFalse(pinRepository.biometricEnabled.value)
    }

    private fun createUseCase(
        pinRepository: FakePinRepository,
        availability: BiometricAvailability
    ): BiometricSettingsUseCase {
        return BiometricSettingsUseCase(
            pinRepository = pinRepository,
            biometricRepository = FakeBiometricRepository(availability)
        )
    }

    private class FakePinRepository(
        pinEnabled: Boolean,
        biometricEnabled: Boolean = false
    ) : IPinRepository {
        val pinEnabled = MutableStateFlow(pinEnabled)
        val biometricEnabled = MutableStateFlow(biometricEnabled)

        override val isPinEnabledFlow: Flow<Boolean> = this.pinEnabled
        override val isBiometricEnabledFlow: Flow<Boolean> = this.biometricEnabled

        override suspend fun setPinEnabled(enabled: Boolean) {
            pinEnabled.value = enabled
            if (!enabled) {
                biometricEnabled.value = false
            }
        }

        override suspend fun setBiometricEnabled(enabled: Boolean) {
            biometricEnabled.value = enabled
        }

        override suspend fun savePin(pin: String) = Unit

        override suspend fun isPinValid(input: String): Boolean = false
    }

    private class FakeBiometricRepository(
        private val availability: BiometricAvailability
    ) : IBiometricRepository {
        override suspend fun authenticate(
            activity: FragmentActivity,
            title: String?,
            subtitle: String?,
            description: String?
        ): BiometricResult = BiometricResult.Canceled

        override fun checkAvailability(): BiometricAvailability = availability

        override fun isAvailable(): Boolean = availability.isAvailable
    }
}
