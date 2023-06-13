package com.example.itplaneta.core.otp.parser


import android.net.Uri
import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.core.otp.models.OtpAlgorithm
import com.example.itplaneta.core.otp.models.OtpType
import javax.inject.Inject
import javax.inject.Singleton

class OtpUriParseException(message: String) : Exception(message)

@Singleton
class UriOtpParser @Inject constructor() {
    fun uriOtpParser(otpuri: String): Account? {

        val uri = Uri.parse(otpuri.replace("%40","@").replace("%3A",":"))
        if (uri.scheme != "otpauth") {
            throw OtpUriParseException("Invalid URI scheme")
        }
        if (uri.authority == null) {
            throw OtpUriParseException("Missing authority")
        }
        val pathParts = uri.pathSegments
        if (pathParts.size != 1 || pathParts[0].isEmpty()) {
            throw OtpUriParseException("Invalid path")
        }
        val otpTypeString = uri.host
        if (otpTypeString != "totp" && otpTypeString != "hotp") {
            throw OtpUriParseException("Invalid type")
        }
        val otpType = when (otpTypeString) {
            "totp" -> OtpType.Totp
            "hotp" -> OtpType.Hotp
            else -> {
                throw OtpUriParseException("Invalid type")
            }
        }
        val secret = uri.getQueryParameter("secret") ?: throw OtpUriParseException("Missing secret")
        val issuer = uri.getQueryParameter("issuer") ?: ""

        val label = try {
            uri.pathSegments[0]
        } catch (e: Exception) {
            throw OtpUriParseException("Invalid label")
        }
        val algorithmString = uri.getQueryParameter("algorithm") ?: "SHA1"
        val algorithm = when (algorithmString.uppercase()) {
            "SHA1" -> OtpAlgorithm.Sha1
            "SHA256" -> OtpAlgorithm.Sha256
            "SHA512" -> OtpAlgorithm.Sha512
            else -> {
                throw OtpUriParseException("Invalid algorithm")
            }
        }
        val digitsString = uri.getQueryParameter("digits") ?: "6"
        val digits = digitsString.toInt()
        val periodString = uri.getQueryParameter("period") ?: "30"
        val period = periodString.toInt()
        val counterString = uri.getQueryParameter("counter") ?: "0"
        val counter = try {
            counterString.toLong()
        } catch (e: NumberFormatException) {
            return null
        }

        return Account(
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
    }
}