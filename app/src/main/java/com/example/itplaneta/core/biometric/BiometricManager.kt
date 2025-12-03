package com.example.itplaneta.core.biometric

import android.content.Context
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager as AndroidBiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.itplaneta.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Проверяет, доступна ли биометрическая аутентификация на устройстве
     */
    fun isBiometricAvailable(): BiometricAvailability {
        val biometricManager = AndroidBiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            AndroidBiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.Available

            AndroidBiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NoHardware

            AndroidBiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.HardwareUnavailable

            AndroidBiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NotEnrolled

            AndroidBiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricAvailability.SecurityUpdateRequired

            AndroidBiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> BiometricAvailability.Unsupported

            AndroidBiometricManager.BIOMETRIC_STATUS_UNKNOWN -> BiometricAvailability.Unknown

            else -> BiometricAvailability.Unknown
        }
    }

    /**
     * Создает BiometricPrompt для аутентификации
     */
    fun createPrompt(
        activity: FragmentActivity, callback: BiometricAuthCallback
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)

        val authCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                callback.onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)

                when (errorCode) {
                    BiometricPrompt.ERROR_CANCELED, BiometricPrompt.ERROR_USER_CANCELED, BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        callback.onCanceled()
                    }

                    BiometricPrompt.ERROR_LOCKOUT, BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                        callback.onLockout(errString.toString())
                    }

                    else -> {
                        callback.onError(errorCode, errString.toString())
                    }
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                callback.onFailed()
            }
        }

        return BiometricPrompt(activity, executor, authCallback)
    }

    /**
     * Создает PromptInfo с стандартными настройками
     */
    fun createPromptInfo(
        title: String? = null,
        subtitle: String? = null,
        description: String? = null,
        negativeButtonText: String? = null
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(title ?: context.getString(R.string.biometric_prompt_title))
            .setSubtitle(subtitle ?: context.getString(R.string.biometric_prompt_subtitle)).apply {
                description?.let { setDescription(it) }
            }.setNegativeButtonText(
                negativeButtonText ?: context.getString(R.string.cancel)
            ).setAllowedAuthenticators(BIOMETRIC_STRONG).build()
    }

    /**
     * Запускает биометрическую аутентификацию
     */
    fun authenticate(
        activity: FragmentActivity,
        callback: BiometricAuthCallback,
        promptInfo: BiometricPrompt.PromptInfo? = null
    ) {
        val prompt = createPrompt(activity, callback)
        val info = promptInfo ?: createPromptInfo()
        prompt.authenticate(info)
    }
}

/**
 * Состояние доступности биометрии
 */
sealed class BiometricAvailability {
    object Available : BiometricAvailability()
    object NoHardware : BiometricAvailability()
    object HardwareUnavailable : BiometricAvailability()
    object NotEnrolled : BiometricAvailability()
    object SecurityUpdateRequired : BiometricAvailability()
    object Unsupported : BiometricAvailability()
    object Unknown : BiometricAvailability()

    val isAvailable: Boolean
        get() = this is Available
}

/**
 * Callback для результатов биометрической аутентификации
 */
interface BiometricAuthCallback {
    fun onSuccess()
    fun onFailed()
    fun onError(errorCode: Int, errorMessage: String)
    fun onCanceled()
    fun onLockout(message: String)
}