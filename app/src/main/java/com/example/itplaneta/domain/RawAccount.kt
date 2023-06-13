package com.example.itplaneta.domain

import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.core.otp.models.OtpType

data class RawAccount(
    val id: Int = 0,
    val issuer: String? = "",
    val label: String = "",
    val tokenType: OtpType = OtpType.Totp,
    val algorithm: OtpAlgorithm = OtpAlgorithm.Sha1,
    val secret: String = "",
    val digits: String = "6",
    val counter: String = "0",
    val period: String = "30"

)
