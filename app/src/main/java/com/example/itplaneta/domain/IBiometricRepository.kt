package com.example.itplaneta.domain

import androidx.fragment.app.FragmentActivity
import com.example.itplaneta.core.biometric.BiometricAvailability
import com.example.itplaneta.core.biometric.BiometricResult

interface IBiometricRepository {
    suspend fun authenticate(
        activity: FragmentActivity,
        title: String? = null,
        subtitle: String? = null,
        description: String? = null
    ): BiometricResult

    fun checkAvailability(): BiometricAvailability
    fun isAvailable(): Boolean
}