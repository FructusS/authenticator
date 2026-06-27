package com.example.itplaneta.data.repository

import com.example.itplaneta.data.SettingsManager
import com.example.itplaneta.domain.IPinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinRepository @Inject constructor(
    private val settingsManager: SettingsManager
) : IPinRepository {
    override val isPinEnabledFlow: Flow<Boolean> = settingsManager.isPinEnabledFlow
    override val isBiometricEnabledFlow: Flow<Boolean> = settingsManager.isBiometricEnabledFlow

    override suspend fun setPinEnabled(enabled: Boolean) {
        settingsManager.setPinEnabled(enabled)
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        settingsManager.setBiometricEnabled(enabled)
    }

    override suspend fun savePin(pin: String) {
        settingsManager.savePin(pin)
    }

    override suspend fun isPinValid(input: String): Boolean {
        return settingsManager.isPinValid(input)
    }
}
