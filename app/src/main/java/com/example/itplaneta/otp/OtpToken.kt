package com.example.itplaneta.otp

import androidx.room.PrimaryKey

data class OtpToken(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val issuer: String?,
    val label: String,
    val tokenType: OtpType,
    val algorithm: OtpDigest,
    val secret: String,
    val digits: Int,
    val counter: Long,
    val period: Int
)
