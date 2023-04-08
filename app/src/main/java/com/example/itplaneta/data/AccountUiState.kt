package com.example.itplaneta.data

import com.example.itplaneta.otp.OtpAlgorithm
import com.example.itplaneta.otp.OtpType

data class AccountUiState(
    val issuer: String?,
    val label: String,
    val tokenType: OtpType,
    val algorithm: OtpAlgorithm,
    val secret: String,
    val digits: Int,
    val period: Int
)
