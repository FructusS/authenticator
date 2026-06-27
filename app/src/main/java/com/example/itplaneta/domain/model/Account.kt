package com.example.itplaneta.domain.model

import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.core.otp.models.OtpType

data class Account(
    val id: Int,
    val issuer: String?,
    val label: String,
    val tokenType: OtpType,
    val algorithm: OtpAlgorithm,
    val secret: String,
    val digits: Int,
    val counter: Long,
    val period: Int
)
