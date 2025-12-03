package com.example.itplaneta.core.biometric

import androidx.fragment.app.FragmentActivity
import com.example.itplaneta.domain.IBiometricRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Repository для работы с биометрической аутентификацией
 */
@Singleton
class BiometricRepository @Inject constructor(
    private val biometricManager: BiometricManager
) : IBiometricRepository {
    /**
     * Suspend функция для биометрической аутентификации
     */
    override suspend fun authenticate(
        activity: FragmentActivity,
        title: String?,
        subtitle: String?,
        description: String?
    ): BiometricResult = suspendCancellableCoroutine { continuation ->

        val callback = object : BiometricAuthCallback {
            override fun onSuccess() {
                if (continuation.isActive) {
                    continuation.resume(BiometricResult.Success)
                }
            }

            override fun onFailed() {
                if (continuation.isActive) {
                    continuation.resume(BiometricResult.Failed)
                }
            }

            override fun onError(errorCode: Int, errorMessage: String) {
                if (continuation.isActive) {
                    continuation.resume(BiometricResult.Error(errorCode, errorMessage))
                }
            }

            override fun onCanceled() {
                if (continuation.isActive) {
                    continuation.resume(BiometricResult.Canceled)
                }
            }

            override fun onLockout(message: String) {
                if (continuation.isActive) {
                    continuation.resume(BiometricResult.Lockout(message))
                }
            }
        }

        val promptInfo = biometricManager.createPromptInfo(
            title = title,
            subtitle = subtitle,
            description = description
        )

        biometricManager.authenticate(activity, callback, promptInfo)
    }

    /**
     * Проверяет доступность биометрии на устройстве
     */
    override fun checkAvailability(): BiometricAvailability {
        return biometricManager.isBiometricAvailable()
    }

    /**
     * Проверяет, доступна ли биометрия
     */
    override fun isAvailable(): Boolean {
        return checkAvailability().isAvailable
    }
}

/**
 * Результат биометрической аутентификации
 */
sealed class BiometricResult {
    object Success : BiometricResult()
    object Failed : BiometricResult()
    object Canceled : BiometricResult()
    data class Error(val code: Int, val message: String) : BiometricResult()
    data class Lockout(val message: String) : BiometricResult()

    val isSuccess: Boolean get() = this is Success
    val isCanceled: Boolean get() = this is Canceled
}