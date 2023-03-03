package com.example.itplaneta.data

import com.example.itplaneta.otp.OtpDigest
import com.example.itplaneta.otp.OtpType

data class AccountUiState(
    val issuer: String?,
    val label: String,
    val tokenType: OtpType,
    val algorithm: OtpDigest,
    val secret: String,
    val digits: Int,
    val period: Int
)
