package com.example.itplaneta.ui.screens.account.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.itplaneta.R
import com.example.itplaneta.domain.validation.AccountFieldError
import com.example.itplaneta.ui.components.BaseTextField

/**
TOTP Period input field
Specifies interval in seconds (typically 30)
Used only when OTP type is TOTP (time-based)
 */

@Composable
fun OtpPeriod(
    period: String,
    onValueChange: (String) -> Unit,
    error: AccountFieldError? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        BaseTextField(
            value = period,
            onValueChange = onValueChange,
            label = stringResource(id = R.string.period_otp_code),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            error = error
        )
    }
}