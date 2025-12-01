package com.example.itplaneta.core.otp.parser


import android.net.Uri
import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.core.otp.models.OtpType
import com.example.itplaneta.core.utils.Result
import com.example.itplaneta.data.backup.BackupMessage
import com.example.itplaneta.domain.validation.AccountConstraints
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

class OtpUriParseException(message: String) : Exception(message)

typealias OtpParsingError<E> = Result<Account, E>

@Singleton
class UriOtpParser @Inject constructor(private val base32: Base32) {
    fun parse(otpUri: String): OtpParsingError<String> {
        return try {
            val uri = otpUri.toUri()

            if (uri.scheme != "otpauth") {
                return Result.Error(
                    OtpUriParseException("Invalid URI scheme: ${uri.scheme}"),
                    "Invalid QR code format"
                )
            }

            val typeString = uri.host ?: return Result.Error(
                OtpUriParseException("Missing type"), "Invalid QR code format"
            )

            val otpType = when (typeString.lowercase()) {
                "totp" -> OtpType.Totp
                "hotp" -> OtpType.Hotp
                else -> return Result.Error(
                    OtpUriParseException("Invalid type: $typeString"), "Unsupported OTP type"
                )
            }

            if (uri.pathSegments.isEmpty() || uri.pathSegments[0].isBlank()) {
                return Result.Error(
                    OtpUriParseException("Invalid path"), "Invalid QR code format"
                )
            }

            val rawLabel = uri.pathSegments[0]
            val (issuerFromPath, labelFromPath) = rawLabel.split(":", limit = 2).let { parts ->
                when (parts.size) {
                    2 -> parts[0] to parts[1]
                    else -> "" to parts[0]
                }
            }

            val secretRaw = uri.getQueryParameter("secret") ?: return Result.Error(
                OtpUriParseException("Missing secret"), "Missing secret in QR code"
            )

            // Валидация Base32: чистим пробелы, верхний регистр — как в AccountValidator
            val cleanSecret = secretRaw.replace(" ", "")
            if (cleanSecret.length < AccountConstraints.MIN_SECRET_LENGTH) {
                return Result.Error(
                    OtpUriParseException("Secret too short"), "Secret key is too short"
                )
            }

            val secret = try {
                base32.decodeBase32(cleanSecret.uppercase())
                // сохраняем исходную строку (как и раньше), главное — что декодируется
                cleanSecret
            } catch (e: Exception) {
                return Result.Error(
                    OtpUriParseException("Invalid Base32 secret"),
                    "Secret key is not a valid Base32 string"
                )
            }

            val issuerParam = uri.getQueryParameter("issuer")
            val issuer = when {
                !issuerParam.isNullOrBlank() -> issuerParam
                issuerFromPath.isNotBlank() -> issuerFromPath
                else -> ""
            }

            val algorithmString = uri.getQueryParameter("algorithm") ?: "SHA1"
            val algorithm = when (algorithmString.uppercase()) {
                "SHA1" -> OtpAlgorithm.Sha1
                "SHA256" -> OtpAlgorithm.Sha256
                "SHA512" -> OtpAlgorithm.Sha512
                else -> return Result.Error(
                    OtpUriParseException("Invalid algorithm: $algorithmString"),
                    "Unsupported algorithm"
                )
            }

            val digits = (uri.getQueryParameter("digits")
                ?: AccountConstraints.DEFAULT_DIGITS.toString()).toIntOrNull()
                ?: return Result.Error(
                    OtpUriParseException("Invalid digits"), "Invalid digits value"
                )

            if (digits !in AccountConstraints.MIN_DIGITS..AccountConstraints.MAX_DIGITS) {
                return Result.Error(
                    OtpUriParseException("Digits out of range: $digits"),
                    "Digits must be between ${AccountConstraints.MIN_DIGITS} and ${AccountConstraints.MAX_DIGITS}"
                )
            }

            val period = (uri.getQueryParameter("period")
                ?: AccountConstraints.DEFAULT_PERIOD.toString()).toIntOrNull()
                ?: return Result.Error(
                    OtpUriParseException("Invalid period"), "Invalid period value"
                )

            if (period !in AccountConstraints.MIN_PERIOD..AccountConstraints.MAX_PERIOD) {
                return Result.Error(
                    OtpUriParseException("Period out of range: $period"),
                    "Period must be between ${AccountConstraints.MIN_PERIOD} and ${AccountConstraints.MAX_PERIOD}"
                )
            }

            val counter =
                (uri.getQueryParameter("counter") ?: "0").toLongOrNull() ?: return Result.Error(
                    OtpUriParseException("Invalid counter"), "Invalid counter value"
                )

            val label = labelFromPath.ifBlank { issuer.ifBlank { "Account" } }

            Result.Success(
                Account(
                    id = 0,
                    issuer = issuer,
                    label = label,
                    tokenType = otpType,
                    algorithm = algorithm,
                    secret = secret,
                    digits = digits,
                    counter = counter,
                    period = period
                )
            )
        } catch (e: Exception) {
            Result.Error(e, "Failed to parse QR code")
        }
    }
}