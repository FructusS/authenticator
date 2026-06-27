package com.example.itplaneta.domain.usecase

import androidx.fragment.app.FragmentActivity
import com.example.itplaneta.core.biometric.BiometricAvailability
import com.example.itplaneta.core.biometric.BiometricResult
import com.example.itplaneta.domain.IBiometricRepository
import com.example.itplaneta.domain.IBiometricSettingsRepository
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
        val biometricSettingsRepository = FakeBiometricSettingsRepository()
        val useCase = createUseCase(
            pinRepository,
            biometricSettingsRepository,
            BiometricAvailability.Available
        )

        val result = useCase.setBiometricEnabled(true)

        assertEquals(SetBiometricEnabledResult.Success, result)
        assertTrue(biometricSettingsRepository.biometricEnabled.value)
    }

    @Test
    fun validatingBiometricEnableDoesNotStoreEnabledFlagBeforeAuthentication() = runTest {
        val pinRepository = FakePinRepository(pinEnabled = true)
        val biometricSettingsRepository = FakeBiometricSettingsRepository()
        val useCase = createUseCase(
            pinRepository,
            biometricSettingsRepository,
            BiometricAvailability.Available
        )

        val result = useCase.validateCanEnableBiometric()

        assertEquals(SetBiometricEnabledResult.Success, result)
        assertFalse(biometricSettingsRepository.biometricEnabled.value)
    }

    @Test
    fun enablingBiometricWithoutPinIsRejected() = runTest {
        val pinRepository = FakePinRepository(pinEnabled = false)
        val biometricSettingsRepository = FakeBiometricSettingsRepository()
        val useCase = createUseCase(
            pinRepository,
            biometricSettingsRepository,
            BiometricAvailability.Available
        )

        val result = useCase.setBiometricEnabled(true)

        assertEquals(SetBiometricEnabledResult.PinRequired, result)
        assertFalse(biometricSettingsRepository.biometricEnabled.value)
    }

    @Test
    fun disablingBiometricStoresDisabledFlag() = runTest {
        val pinRepository = FakePinRepository(pinEnabled = true)
        val biometricSettingsRepository = FakeBiometricSettingsRepository(biometricEnabled = true)
        val useCase = createUseCase(
            pinRepository,
            biometricSettingsRepository,
            BiometricAvailability.Available
        )

        val result = useCase.setBiometricEnabled(false)

        assertEquals(SetBiometricEnabledResult.Success, result)
        assertFalse(biometricSettingsRepository.biometricEnabled.value)
    }

    @Test
    fun disabledPinMasksStoredBiometricEnabledState() = runTest {
        val useCase = createUseCase(
            FakePinRepository(pinEnabled = false),
            FakeBiometricSettingsRepository(biometricEnabled = true),
            BiometricAvailability.Available
        )

        val settings = useCase.observeSettings().first()

        assertFalse(settings.isPinEnabled)
        assertFalse(settings.isBiometricEnabled)
    }

    @Test
    fun unavailableBiometricIsExposedInSettingsState() = runTest {
        val useCase = createUseCase(
            FakePinRepository(pinEnabled = true),
            FakeBiometricSettingsRepository(),
            BiometricAvailability.NoHardware
        )

        val settings = useCase.observeSettings().first()

        assertEquals(BiometricAvailability.NoHardware, settings.availability)
        assertFalse(settings.canEnableBiometric)
    }

    @Test
    fun enablingUnavailableBiometricIsRejected() = runTest {
        val pinRepository = FakePinRepository(pinEnabled = true)
        val biometricSettingsRepository = FakeBiometricSettingsRepository()
        val useCase = createUseCase(
            pinRepository,
            biometricSettingsRepository,
            BiometricAvailability.NoHardware
        )

        val result = useCase.setBiometricEnabled(true)

        assertTrue(result is SetBiometricEnabledResult.Unavailable)
        assertFalse(biometricSettingsRepository.biometricEnabled.value)
    }

    private fun createUseCase(
        pinRepository: FakePinRepository,
        biometricSettingsRepository: FakeBiometricSettingsRepository,
        availability: BiometricAvailability
    ): BiometricSettingsUseCase {
        return BiometricSettingsUseCase(
            pinRepository = pinRepository,
            biometricSettingsRepository = biometricSettingsRepository,
            biometricRepository = FakeBiometricRepository(availability)
        )
    }

    private class FakePinRepository(
        pinEnabled: Boolean
    ) : IPinRepository {
        val pinEnabled = MutableStateFlow(pinEnabled)

        override val isPinEnabledFlow: Flow<Boolean> = this.pinEnabled

        override suspend fun setPinEnabled(enabled: Boolean) {
            pinEnabled.value = enabled
        }

        override suspend fun savePin(pin: String) = Unit

        override suspend fun isPinValid(input: String): Boolean = false
    }

    private class FakeBiometricSettingsRepository(
        biometricEnabled: Boolean = false
    ) : IBiometricSettingsRepository {
        val biometricEnabled = MutableStateFlow(biometricEnabled)

        override val isBiometricEnabledFlow: Flow<Boolean> = this.biometricEnabled

        override suspend fun setBiometricEnabled(enabled: Boolean) {
            biometricEnabled.value = enabled
        }
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
