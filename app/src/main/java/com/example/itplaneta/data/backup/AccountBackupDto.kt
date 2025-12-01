package com.example.itplaneta.data.backup

import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.data.sources.Account
import kotlinx.serialization.Serializable

@Serializable
data class AccountBackupDto(
    val algorithm: String,
    val counter: Long,
    val digits: Int,
    val id: Int,
    val issuer: String? = null,
    val label: String,
    val period: Int,
    val secret: String,
    val tokenType: String
)

fun Account.toBackupDto(): AccountBackupDto = AccountBackupDto(
    algorithm = when (algorithm) {
        OtpAlgorithm.Sha1 -> "Sha1"
        OtpAlgorithm.Sha256 -> "Sha256"
        OtpAlgorithm.Sha512 -> "Sha512"
    },
    counter = counter,
    digits = digits,
    id = id,
    issuer = issuer,
    label = label,
    period = period,
    secret = secret,
    tokenType = when (tokenType) {
        OtpType.Totp -> "Totp"
        OtpType.Hotp -> "Hotp"
    }
)

fun AccountBackupDto.toAccount(): Account = Account(
    id = id, issuer = issuer, label = label, tokenType = when (tokenType.lowercase()) {
        "totp" -> OtpType.Totp
        "hotp" -> OtpType.Hotp
        else -> OtpType.Totp
    }, algorithm = when (algorithm.uppercase()) {
        "SHA1", "SHA_1", "SHA-1" -> OtpAlgorithm.Sha1
        "SHA256", "SHA_256", "SHA-256" -> OtpAlgorithm.Sha256
        "SHA512", "SHA_512", "SHA-512" -> OtpAlgorithm.Sha512
        else -> OtpAlgorithm.Sha1
    }, secret = secret, digits = digits, counter = counter, period = period
)