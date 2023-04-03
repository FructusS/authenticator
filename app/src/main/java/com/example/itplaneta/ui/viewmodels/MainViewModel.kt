package com.example.itplaneta.ui.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.data.database.Account

import com.example.itplaneta.data.database.AccountRepository
import com.example.itplaneta.otp.OtpGenerator
import com.example.itplaneta.otp.OtpType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val otpGenerator: OtpGenerator
    ): ViewModel() {

    private val secretBytes = mutableMapOf<String, ByteArray>()
    val accounts = accountRepository.getAccounts()
    val codes = mutableStateMapOf<Long, String>()
    val timerProgresses = mutableStateMapOf<Long, Float>()
    val timerValues = mutableStateMapOf<Long, Long>()

    init {
        viewModelScope.launch {
            accounts.collect { list ->
                secretBytes.clear()
                list.forEach {
                    secretBytes[it.secret] = otpGenerator.transformToBytes(it.secret)
                    if (it.tokenType == OtpType.Hotp){
                        generateHotp(it)
                    }
                    if (it.tokenType == OtpType.Totp){
                        generateTotp(it)

                    }
                }
            }
        }
    }

    private fun generateHotp(account: Account) {
        val keyByte = secretBytes[account.secret]
        if (keyByte != null) {
            codes[account.id] = otpGenerator.generateHotp(
                secret = keyByte,
                counter = account.counter.toLong(),
                digits = account.digits,
                digest = account.algorithm
            )
        }
    }

    private val totpTimer = fixedRateTimer(name = "totp-timer", daemon = false, period = 1000L) {
        viewModelScope.launch(Dispatchers.Main) {
            accounts.collect { list -> list.forEach {
                if (it.tokenType == OtpType.Totp)
                 generateTotp(it)
                }
            }
        }
    }


    fun deleteAccount(account: Account){
        accountRepository.deleteAccount(account)
    }

    private fun generateTotp(account: Account) {
        val seconds = System.currentTimeMillis() / 1000
        val keyByte = secretBytes[account.secret]
        if (keyByte != null) {
            codes[account.id] = otpGenerator.generateTotp(
                secret = keyByte,
                interval = account.period.toLong(),
                digits = account.digits,
                seconds = seconds,
                digest = account.algorithm
            )
        }
        val diff = seconds % account.period
        timerProgresses[account.id] = 1f - (diff / account.period.toFloat())
        timerValues[account.id] = account.period - diff
    }

    override fun onCleared() {
        totpTimer.cancel()
    }

}