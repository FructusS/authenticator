package com.example.itplaneta.ui.screens.qrscanner

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.R
import com.example.itplaneta.core.otp.parser.UriOtpParser
import com.example.itplaneta.domain.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class QrScannerViewModel @Inject constructor(@ApplicationContext val context : Context,
                                             private val accountRepository: AccountRepository,
                                             private val uriOtpParser: UriOtpParser
) : ViewModel() {
    var hasReadCode = mutableStateOf(false)

    fun parse(uri : String){
        try {
            val account = uriOtpParser.uriOtpParser(uri)
            if (account != null) {
                viewModelScope.launch {
                    accountRepository.addAccount(account)
                }
                hasReadCode.value = true
            } else{
                Toast.makeText(context, context.getText(R.string.fail_scan_qr_code),Toast.LENGTH_SHORT).show()
            }
        }catch (ex : Exception){
            Toast.makeText(context, context.getText(R.string.fail_scan_qr_code),Toast.LENGTH_SHORT).show()
        }

    }
}