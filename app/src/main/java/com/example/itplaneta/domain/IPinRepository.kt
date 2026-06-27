package com.example.itplaneta.domain

import kotlinx.coroutines.flow.Flow

interface IPinRepository {
    val isPinEnabledFlow: Flow<Boolean>
    val isBiometricEnabledFlow: Flow<Boolean>

    suspend fun setPinEnabled(enabled: Boolean)
    suspend fun savePin(pin: String)
    suspend fun isPinValid(input: String): Boolean
}
