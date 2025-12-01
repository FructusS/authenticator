package com.example.itplaneta.domain

import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.data.sources.Account

data class AccountInputDto(
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

fun Account.toAccountInputDto(): AccountInputDto = AccountInputDto(
    id = id,
    digits = digits.toString(),
    counter = counter.toString(),
    secret = secret,
    label = label,
    issuer = issuer,
    period = period.toString(),
    algorithm = algorithm,
    tokenType = tokenType
)

fun AccountInputDto.toAccount(): Account = Account(
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