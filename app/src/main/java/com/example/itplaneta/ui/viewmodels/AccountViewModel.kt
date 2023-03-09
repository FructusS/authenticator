package com.example.itplaneta.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.itplaneta.data.database.Account
import com.example.itplaneta.data.database.AccountRepository
import com.example.itplaneta.otp.OtpDigest
import com.example.itplaneta.otp.OtpType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val accountRepository: AccountRepository) : ViewModel() {

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

        accountRepository.addAccount(Account(
            0,
            label = label,
            issuer = issuer,
            tokenType = OtpType.Totp,
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



    fun updateAccount(account: Account){
        accountRepository.updateAccount(account)
    }
    fun getAccountBySecret(secret : String) : Account? =
        accountRepository.getAccountBySecret(secret)
}
