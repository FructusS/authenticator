package com.example.itplaneta.ui.screens.qrscanner

import androidx.lifecycle.ViewModel
import com.example.itplaneta.data.database.AccountRepository
import com.example.itplaneta.utils.UriOtpParser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class QrScannerViewModel @Inject constructor(private val accountRepository: AccountRepository,
                                             private val uriOtpParser: UriOtpParser) : ViewModel() {
    fun parse(uri : String){
        val account = uriOtpParser.uriOtpParser(uri)
        if (account != null) {
            accountRepository.addAccount(account)
        }
    }
}