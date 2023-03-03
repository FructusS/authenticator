package com.example.itplaneta.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.itplaneta.data.database.Account
import com.example.itplaneta.data.database.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val accountRepository: AccountRepository) : ViewModel() {

    fun addAccount(account: Account){
        accountRepository.addAccount(account)
    }
    fun updateAccount(account: Account){
        accountRepository.updateAccount(account)
    }
    fun getAccountBySecret(secret : String) : Account? =
        accountRepository.getAccountBySecret(secret)


}
