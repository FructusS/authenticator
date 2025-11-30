package com.example.itplaneta.core.otp

import com.example.itplaneta.core.otp.models.OtpGenerator
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.data.sources.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

class OtpCodeManager @Inject constructor(
    private val otpGenerator: OtpGenerator
) {

    private val _codes = MutableStateFlow<Map<Int, String>>(emptyMap())
    val codes: StateFlow<Map<Int, String>> = _codes.asStateFlow()

    private val _timerProgresses = MutableStateFlow<Map<Int, Float>>(emptyMap())
    val timerProgresses: StateFlow<Map<Int, Float>> = _timerProgresses.asStateFlow()

    private val _timerValues = MutableStateFlow<Map<Int, Long>>(emptyMap())
    val timerValues: StateFlow<Map<Int, Long>> = _timerValues.asStateFlow()

    private var workerJob: Job? = null

    /**
     * Запустить менеджер: передаём scope (обычно viewModelScope) и Flow аккаунтов.
     * Если уже запущено — оно будет отменено и перезапущено.
     */
    fun start(
        scope: CoroutineScope,
        accountsFlow: Flow<List<Account>>,
        secretBytesMap: Map<String, ByteArray>
    ) {
        stop()

        workerJob = scope.launch(Dispatchers.Default + SupervisorJob()) {
            try {
                accountsFlow.collectLatest { accounts ->
                    // Когда accounts меняются — начинаем внутренний цикл обновления каждую секунду.
                    // collectLatest гарантирует отмену предыдущего цикла при новом emissions.
                    while (isActive) {
                        try {
                            val nowSeconds = System.currentTimeMillis() / 1000L
                            val codesMap = _codes.value.toMutableMap()
                            val progressMap = mutableMapOf<Int, Float>()
                            val valuesMap = mutableMapOf<Int, Long>()

                            for (account in accounts) {
                                try {
                                    when (account.tokenType) {
                                        OtpType.Hotp -> {
                                            val bytes = secretBytesMap[account.secret]
                                            if (bytes != null) {
                                                codesMap[account.id] = generateHotp(account, bytes)
                                            }
                                        }

                                        OtpType.Totp -> {
                                            val bytes = secretBytesMap[account.secret]
                                            if (bytes != null) {
                                                codesMap[account.id] =
                                                    generateTotp(account, bytes, nowSeconds)
                                                val diff = nowSeconds % account.period
                                                progressMap[account.id] =
                                                    1f - (diff / account.period.toFloat())
                                                valuesMap[account.id] = account.period - diff
                                            }
                                        }
                                    }
                                } catch (e: CancellationException) {
                                    throw e
                                } catch (e: Exception) {
                                    Timber.e(
                                        e,
                                        "OtpCodeManager: error generating code for ${account.label}"
                                    )
                                }
                            }

                            _codes.value = codesMap
                            _timerProgresses.value = progressMap
                            _timerValues.value = valuesMap

                            delay(1000L)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            Timber.e(e, "OtpCodeManager: internal timer error")
                            // при ошибке — подождём и попытаемся снова
                            delay(1000L)
                        }
                    }
                }
            } catch (e: CancellationException) {
                // нормальное завершение
                Timber.d("OtpCodeManager: cancelled")
            } catch (e: Exception) {
                Timber.e(e, "OtpCodeManager: unexpected error")
            }
        }
    }

    fun stop() {
        workerJob?.cancel()
        workerJob = null
    }

    private fun generateHotp(account: Account, keyBytes: ByteArray): String {
        return try {
            otpGenerator.generateHotp(
                secret = keyBytes,
                counter = account.counter.toLong(),
                digits = account.digits,
                digest = account.algorithm
            )
        } catch (e: Exception) {
            Timber.e(e, "OtpCodeManager: error generating HOTP")
            "ERROR"
        }
    }

    private fun generateTotp(account: Account, keyBytes: ByteArray, seconds: Long): String {
        return try {
            otpGenerator.generateTotp(
                secret = keyBytes,
                interval = account.period.toLong(),
                digits = account.digits,
                seconds = seconds,
                digest = account.algorithm
            )
        } catch (e: Exception) {
            Timber.e(e, "OtpCodeManager: error generating TOTP")
            "ERROR"
        }
    }
}