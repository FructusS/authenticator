package com.example.itplaneta.ui.screens.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.itplaneta.data.database.Account
import com.example.itplaneta.data.database.AccountRepository
import com.example.itplaneta.otp.OtpAlgorithm
import com.example.itplaneta.otp.OtpType
import com.example.itplaneta.utils.Base32
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val accountRepository: AccountRepository) :
    ViewModel() {


    private val base32 = Base32()

    //region Otp algorithm

    val otpAlgorithmLists = listOf(OtpAlgorithm.Sha1, OtpAlgorithm.Sha256, OtpAlgorithm.Sha512)
    var otpAlgorithm by mutableStateOf(otpAlgorithmLists[0])
        private set

    fun updateOtpAlgorithm(otpAlgorithm: OtpAlgorithm) {
        this.otpAlgorithm = otpAlgorithm
    }

    //endregion

    //region OtpType
    val otpTypeList = listOf(OtpType.Totp, OtpType.Hotp)
    var otpType by mutableStateOf(otpTypeList[0])
        private set

    fun updateOtpType(otpType: OtpType) {
        this.otpType = otpType
    }


    //endregion

    //region label
    var label by mutableStateOf("")
        private set

    var errorLabel by mutableStateOf(false)
        private set
    var errorLabelText by mutableStateOf("")
        private set

    fun updateLabel(newLabel: String) {
        this.label = newLabel
    }
    //endregion

    //region issuer
    var issuer by mutableStateOf("")
        private set

    fun updateIssuer(newIssuer: String) {
        this.issuer = newIssuer
    }
    //endregion

    //region secret
    var secret by mutableStateOf("")
        private set

    var errorSecret by mutableStateOf(false)
        private set

    var errorSecretText by mutableStateOf("")
        private set

    fun updateSecret(newSecret: String) {
        this.secret = newSecret
    }

    //endregion secret

    //region digits
    var digits by mutableStateOf("6")
        private set
    var errorDigits by mutableStateOf(false)
        private set

    fun updateDigits(oldDigits: String) {
        this.digits = oldDigits
    }
    //endregion

    //region period

    var period by mutableStateOf("30")
    private set
    var errorPeriod by mutableStateOf(false)
        private set

    fun updatePeriod(oldPeriod: String) {
        this.period = oldPeriod
    }



    //endregion

    //region counter

    var counter by mutableStateOf("0")
        private set
    var errorCounter by mutableStateOf(false)
        private set

    fun updateCounter(oldCounter: String) {
        this.counter = oldCounter
    }
    //endregion

    fun addAccount(): Boolean {
        resetErrors()

        if (label.isEmpty()) {
            errorLabel = true
            errorLabelText = "Название аккаунта не может быть пустым"
            return false
        }

        if (secret.isEmpty()) {
            errorSecret = true
            errorSecretText = "Секретный ключ не может быть пустым"
            return false
        }
        if (secret.length < 8) {
            errorSecret = true
            errorSecretText = "Секретный ключ короткий"
            return false
        }
        if (digits.isEmpty()){
            errorDigits = true
            return false
        }
        if (digits.toInt() < 6 || digits.toInt() > 10){
            errorDigits = true
            return false
        }
        try {
            base32.decodeBase32(secret.uppercase())
        } catch (ex: Exception) {
            errorSecret = true
            errorSecretText =
                "Секретный ключ должен содержать только ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
            return false
        }


        accountRepository.addAccount(
            Account(
                0,
                label = label,
                issuer = issuer,
                tokenType = otpType,
                algorithm = otpAlgorithm,
                secret = secret,
                digits = digits.toInt(),
                counter = counter.toLong(),
                period = period.toInt()
            )
        )
        return true
    }

    private fun resetErrors() {
        errorLabel = false
        errorSecret = false
        errorDigits = false
        errorPeriod = false
        errorCounter = false

    }

    fun updateAccountField(accountId: Int) {
        val account = getAccountById(accountId)

        account.issuer?.let { updateIssuer(it) }

        updateLabel(account.label)
        updateSecret(account.secret)
        updateOtpType(account.tokenType)
        updateOtpAlgorithm(account.algorithm)
        updateDigits(account.digits.toString())
        updateCounter(account.counter.toString())
        updatePeriod(account.period.toString())
    }


    fun updateAccount(id: Int) {
        accountRepository.updateAccount(
            Account(
                id,
                label = label,
                issuer = issuer,
                tokenType = otpType,
                algorithm = otpAlgorithm,
                secret = secret,
                digits = digits.toInt(),
                counter = counter.toLong(),
                period = period.toInt()
            )
        )
    }

    private fun getAccountById(id: Int): Account =
        accountRepository.getAccountById(id)
}
