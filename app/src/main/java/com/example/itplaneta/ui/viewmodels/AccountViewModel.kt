package com.example.itplaneta.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.itplaneta.data.database.Account
import com.example.itplaneta.data.database.AccountRepository
import com.example.itplaneta.otp.OtpDigest
import com.example.itplaneta.otp.OtpType
import com.example.itplaneta.utils.Base32
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val accountRepository: AccountRepository) : ViewModel() {


    private val base32 = Base32()
    val otpTypeList = listOf(OtpType.Totp,OtpType.Hotp)
    private var otpType by mutableStateOf(otpTypeList[0])

    fun updateOtpType(otpType: OtpType) {
        this.otpType = otpType
    }
    fun getCurrentOtpType()  = otpType

    var label by mutableStateOf("")
    private set

    var errorLabel by mutableStateOf(false)
        private set
    var errorLabelText by mutableStateOf("")
        private set
    fun updateLabel(newLabel : String){
        this.label = newLabel
    }

    var issuer by mutableStateOf("")
        private set

    fun updateIssuer(newIssuer : String){
        this.issuer = newIssuer
    }

    var secret by mutableStateOf("")
        private set

    var errorSecret by mutableStateOf(false)
        private set

    var errorSecretText by mutableStateOf("")
        private set
    fun updateSecret(newSecret : String){
        this.secret = newSecret
    }


    fun addAccount() : Boolean{
        resetErrors()

        if (label.isEmpty()){
            errorLabel = true
            errorLabelText = "Название аккаунта не может быть пустым"
            return false
        }

        if (secret.isEmpty()){
            errorSecret = true
            errorSecretText = "Секретный ключ не может быть пустым"
            return false
        }
        try {
            base32.decodeBase32(secret.uppercase())
        }catch (ex : Exception){
            errorSecret = true
            errorSecretText = "Секретный ключ должен содержать только ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
            return false
        }


        accountRepository.addAccount(Account(
            0,
            label = label,
            issuer = issuer,
            tokenType = otpType,
            algorithm = OtpDigest.Sha1,
            secret = secret,
            digits = 6,
            counter = 0,
            period = 30)
        )
        return true
    }

    private fun resetErrors() {
        errorLabel = false
        errorSecret = false
    }

    fun updateAccountField(accountId: Int) {
        val account = getAccountById(accountId)
        updateIssuer(account.issuer.toString())
        updateLabel(account.label)
        updateSecret(account.secret)
        updateOtpType(account.tokenType)
    }


    fun updateAccount(id: Int){
        accountRepository.updateAccount(Account(
            id.toLong(),
            label = label,
            issuer = issuer,
            tokenType = otpType,
            algorithm = OtpDigest.Sha1,
            secret = secret,
            digits = 6,
            counter = 0,
            period = 30))
    }

    private fun getAccountById(id : Int) : Account =
        accountRepository.getAccountById(id)



}
