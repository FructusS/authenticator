package com.example.itplaneta.core.otp

import com.example.itplaneta.core.otp.models.OtpGenerator
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.data.sources.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

/**
 * Константы для OTP генерации
 */
object OtpConstants {
    const val UPDATE_INTERVAL_MS = 1000L
    const val ERROR_CODE = "------"
}

/**
 * Результат генерации OTP кода
 */
sealed class OtpResult {
    data class Success(val code: String) : OtpResult()
    data class Error(val message: String) : OtpResult()
}

class OtpCodeManager @Inject constructor(
    private val otpGenerator: OtpGenerator
) {
    private val _codes = MutableStateFlow<Map<Int, OtpResult>>(emptyMap())
    val codes: StateFlow<Map<Int, String>> = _codes.map { map ->
            map.mapValues { (_, result) ->
                when (result) {
                    is OtpResult.Success -> result.code
                    is OtpResult.Error -> OtpConstants.ERROR_CODE
                }
            }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    private val _timerProgresses = MutableStateFlow<Map<Int, Float>>(emptyMap())
    val timerProgresses: StateFlow<Map<Int, Float>> = _timerProgresses.asStateFlow()
    private val _timerValues = MutableStateFlow<Map<Int, Long>>(emptyMap())
    val timerValues: StateFlow<Map<Int, Long>> = _timerValues.asStateFlow()
    private var managerScope: CoroutineScope? = null

    fun start(accountsFlow: Flow<List<Account>>) {
        stop()

        managerScope = CoroutineScope(Dispatchers.Default + SupervisorJob()).apply {
            launch {
                accountsFlow.catch { e ->
                        Timber.e(e, "OtpCodeManager: error in accountsFlow")
                        emit(emptyList())
                    }.collectLatest { accounts ->
                        updateCodesLoop(accounts)
                    }
            }
        }
    }

    private suspend fun updateCodesLoop(accounts: List<Account>) {
        while (currentCoroutineContext().isActive) {
            val startTime = System.currentTimeMillis()

            updateCodes(accounts)

            // Вычисляем точную задержку до следующей секунды
            val elapsed = System.currentTimeMillis() - startTime
            val delayTime = (OtpConstants.UPDATE_INTERVAL_MS - elapsed).coerceAtLeast(0)

            delay(delayTime)
        }
    }

    private fun updateCodes(accounts: List<Account>) {
        val nowSeconds = System.currentTimeMillis() / 1000L
        val codesMap = mutableMapOf<Int, OtpResult>()
        val progressMap = mutableMapOf<Int, Float>()
        val valuesMap = mutableMapOf<Int, Long>()

        for (account in accounts) {
            try {
                val keyBytes = otpGenerator.transformToBytes(account.secret)

                try {
                    when (account.tokenType) {
                        OtpType.Hotp -> {
                            codesMap[account.id] = generateHotp(account, keyBytes)
                        }

                        OtpType.Totp -> {
                            codesMap[account.id] = generateTotp(account, keyBytes, nowSeconds)
                            val diff = nowSeconds % account.period
                            progressMap[account.id] = 1f - (diff / account.period.toFloat())
                            valuesMap[account.id] = account.period - diff
                        }
                    }
                } finally {
                    // Очищаем ключи из памяти
                    keyBytes.fill(0)
                }
            } catch (e: Exception) {
                Timber.e(e, "OtpCodeManager: error processing account ${account.label}")
                codesMap[account.id] = OtpResult.Error("Generation failed")
            }
        }

        _codes.value = codesMap
        _timerProgresses.value = progressMap
        _timerValues.value = valuesMap
    }


    fun stop() {
        managerScope?.cancel()
        managerScope = null

        // Очищаем state
        _codes.value = emptyMap()
        _timerProgresses.value = emptyMap()
        _timerValues.value = emptyMap()
    }

    private fun generateHotp(account: Account, keyBytes: ByteArray): OtpResult {
        return try {
            val code = otpGenerator.generateHotp(
                secret = keyBytes,
                counter = account.counter,
                digits = account.digits,
                digest = account.algorithm
            )
            OtpResult.Success(code)
        } catch (e: Exception) {
            Timber.e(e, "OtpCodeManager: error generating HOTP for ${account.label}")
            OtpResult.Error("HOTP generation failed: ${e.message}")
        }
    }

    private fun generateTotp(account: Account, keyBytes: ByteArray, seconds: Long): OtpResult {
        return try {
            val code = otpGenerator.generateTotp(
                secret = keyBytes,
                interval = account.period.toLong(),
                digits = account.digits,
                seconds = seconds,
                digest = account.algorithm
            )
            OtpResult.Success(code)
        } catch (e: Exception) {
            Timber.e(e, "OtpCodeManager: error generating TOTP for ${account.label}")
            OtpResult.Error("TOTP generation failed: ${e.message}")
        }
    }
}