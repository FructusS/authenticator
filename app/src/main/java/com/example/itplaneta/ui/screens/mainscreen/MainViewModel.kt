package com.example.itplaneta.ui.screens.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.core.otp.OtpCodeManager
import com.example.itplaneta.core.otp.models.OtpGenerator
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.core.utils.Result
import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.domain.IAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountRepository: IAccountRepository,
    private val otpCodeManager: OtpCodeManager
) : ViewModel() {

    private val secretBytes = Collections.synchronizedMap(mutableMapOf<String, ByteArray>())

    val accounts = accountRepository.getAccounts()

    val codes = otpCodeManager.codes
    val timerProgresses = otpCodeManager.timerProgresses
    val timerValues = otpCodeManager.timerValues

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()


    init {
        initializeAccounts()
    }

    /**
     * Initialize accounts and generate initial OTP codes
     */
    private fun initializeAccounts() {
        viewModelScope.launch {
            // Запускаем otpCodeManager и параллельно обновляем secretBytes при изменении списка аккаунтов
            accounts.collectLatest { list ->
                // Обновляем secretBytes map (удаляем старые, добавляем новые)
                secretBytes.clear()
//                for (account in list) {
//                    try {
//                        val bytes = otpGenerator.transformToBytes(account.secret)
//                        secretBytes[account.secret] = bytes
//                    } catch (e: Exception) {
//                        Timber.e(e, "MainViewModel: failed to transform secret for ${account.label}")
//                        _error.value = "Failed to init account: ${account.label}"
//                    }
//                }
                // стартуем/перезапускаем менеджер, передаём flow аккаунтов и snapshot secretBytes map
                // NOTE: передаём immutable view (корректно для чтения)
                otpCodeManager.start(viewModelScope, accounts, secretBytes.toMap())
            }
        }
    }


    /**
     * Delete account with error handling
     */
    suspend fun deleteAccount(account: Account) {
        viewModelScope.launch {
            when (val result = accountRepository.deleteAccount(account)) {
                is Result.Success -> {
                    Timber.d("Account deleted: ${account.label}")
                    _error.value = null
                }
                is Result.Error -> {
                    Timber.e("Error deleting account: ${result.message}")
                    _error.value = result.message ?: "Failed to delete account"
                }
                is Result.Loading -> {
                    _error.value = "Deleting..."
                }
            }
        }
    }

    fun incrementHotpCounter(account: Account) {
        viewModelScope.launch {
            when (val result = accountRepository.incrementHotpCounter(account)) {
                is Result.Success -> {
                    Timber.d("HOTP counter incremented for: ${account.label}")
                    _error.value = null
                }
                is Result.Error -> {
                    Timber.e("Error incrementing counter: ${result.message}")
                    _error.value = result.message ?: "Failed to increment counter"
                }
                is Result.Loading -> {
                    _error.value = "Updating..."
                }
            }
        }
    }

    override fun onCleared() {
        otpCodeManager.stop()
        secretBytes.clear()
        Timber.d("MainViewModel cleared")
        super.onCleared()
    }
}
