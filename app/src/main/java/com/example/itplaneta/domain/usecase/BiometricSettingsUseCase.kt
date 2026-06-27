package com.example.itplaneta.domain.usecase

import com.example.itplaneta.core.biometric.BiometricAvailability
import com.example.itplaneta.domain.IBiometricRepository
import com.example.itplaneta.domain.IBiometricSettingsRepository
import com.example.itplaneta.domain.IPinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class BiometricSettings(
    val isPinEnabled: Boolean,
    val isBiometricEnabled: Boolean,
    val availability: BiometricAvailability
) {
    val canEnableBiometric: Boolean
        get() = isPinEnabled && availability.isAvailable
}

sealed class SetBiometricEnabledResult {
    object Success : SetBiometricEnabledResult()
    object PinRequired : SetBiometricEnabledResult()
    data class Unavailable(val availability: BiometricAvailability) : SetBiometricEnabledResult()
}

class BiometricSettingsUseCase @Inject constructor(
    private val pinRepository: IPinRepository,
    private val biometricSettingsRepository: IBiometricSettingsRepository,
    private val biometricRepository: IBiometricRepository
) {
    fun observeSettings(): Flow<BiometricSettings> {
        val availability = biometricRepository.checkAvailability()
        return combine(
            pinRepository.isPinEnabledFlow,
            biometricSettingsRepository.isBiometricEnabledFlow
        ) { isPinEnabled, isBiometricEnabled ->
            BiometricSettings(
                isPinEnabled = isPinEnabled,
                isBiometricEnabled = isBiometricEnabled && isPinEnabled,
                availability = availability
            )
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean): SetBiometricEnabledResult {
        if (!enabled) {
            biometricSettingsRepository.setBiometricEnabled(false)
            return SetBiometricEnabledResult.Success
        }

        return when (val validation = validateCanEnableBiometric()) {
            SetBiometricEnabledResult.Success -> {
                biometricSettingsRepository.setBiometricEnabled(true)
                SetBiometricEnabledResult.Success
            }

            SetBiometricEnabledResult.PinRequired -> validation
            is SetBiometricEnabledResult.Unavailable -> validation
        }
    }

    suspend fun validateCanEnableBiometric(): SetBiometricEnabledResult {
        if (!pinRepository.isPinEnabledFlow.first()) {
            return SetBiometricEnabledResult.PinRequired
        }

        val availability = biometricRepository.checkAvailability()
        if (!availability.isAvailable) {
            return SetBiometricEnabledResult.Unavailable(availability)
        }

        return SetBiometricEnabledResult.Success
    }
}
