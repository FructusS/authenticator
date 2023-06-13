package com.example.itplaneta.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.R
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.core.otp.parser.Base32
import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.domain.AccountRepository
import com.example.itplaneta.domain.RawAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val base32 = Base32()

    private val _state = MutableStateFlow(AccountUiState())
    val state = _state.asStateFlow()


    private val account
        get() = _state.value.account

    fun updateUiState(account: RawAccount) {
        _state.value = _state.value.copy(account = account)
    }

    private fun validateInput(): Boolean {
        _state.value = _state.value.copy(errorType = ErrorType.Nothing)
        if (account.label.isBlank()) {
            _state.value = _state.value.copy(errorType = ErrorType.LabelError, errorText = R.string.fill_the_label)
            return false
        }

        if (account.secret.isBlank() || account.secret.replace(" ","").length < 16){
            _state.value = _state.value.copy(errorType = ErrorType.SecretError, errorText = R.string.secret_key_to_short)
            return false
        }
        if (account.tokenType == OtpType.Hotp){
            if(account.counter.toLongOrNull() == null){
                _state.value = _state.value.copy(errorType = ErrorType.CounterError, errorText = R.string.fill_the_counter)
                return false
            }
        }

        else{
            if (account.period.toIntOrNull() == null) {
                _state.value = _state.value.copy(errorType = ErrorType.PeriodError, errorText = R.string.incorrect_period)
                return false
            }
            if (account.period.toInt() < 1 && account.period.toInt() > Int.MAX_VALUE / 1000){
                _state.value = _state.value.copy(errorType = ErrorType.PeriodError, errorText = R.string.incorrect_period)
                return false
            }
        }

        if (account.digits.toIntOrNull() == null) {
            _state.value = _state.value.copy(errorType = ErrorType.DigitsError, errorText = R.string.incorrect_digits)
            return false
        }
        if (account.digits.toInt() < 6 || account.digits.toInt() > 10) {
            _state.value = _state.value.copy(errorType = ErrorType.DigitsError, errorText = R.string.incorrect_digits)
            return false
        }
        try {
            base32.decodeBase32(account.secret.uppercase())
        } catch (ex: Exception) {
            _state.value = _state.value.copy(errorType = ErrorType.SecretError, errorText = R.string.secret_can_only_containt_base32chars)
            return false
        }
        return true
    }


    suspend fun addAccount(navigateBack: () -> Unit) {

        if (validateInput()) {
            accountRepository.addAccount(
                account.toAccount()
            )
            navigateBack()
        }
    }


    suspend fun updateAccount(navigateBack: () -> Unit) {
        if (validateInput()) {
            accountRepository.updateAccount(
                account.toAccount()
            )
            navigateBack()
        }
    }

    fun getAccountById(id: Int): RawAccount = accountRepository.getAccountById(id).toRawAccount()

    private fun Account.toRawAccount(): RawAccount = RawAccount(
        id = id,
        digits = digits.toString(),
        counter = counter.toString(),
        secret = secret,
        label = label,
        issuer = issuer,
        period = period.toString(),
        algorithm = algorithm,
        tokenType = tokenType,
    )

    private fun RawAccount.toAccount(): Account = Account(
        id = id,
        digits = digits.toInt(),
        counter = counter.toLong(),
        secret = secret,
        label = label,
        issuer = issuer,
        period = period.toInt(),
        algorithm = algorithm,
        tokenType = tokenType
    )

    fun updateUiStateByAccountId(accountId: Int) {
        viewModelScope.launch {
            updateUiState(accountRepository.getAccountById(accountId).toRawAccount())
        }
    }

}
