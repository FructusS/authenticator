package com.example.itplaneta.domain

import kotlinx.coroutines.flow.Flow

interface IBiometricSettingsRepository {
    val isBiometricEnabledFlow: Flow<Boolean>

    suspend fun setBiometricEnabled(enabled: Boolean)
}
