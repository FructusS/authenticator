package com.example.itplaneta.utils


import android.net.Uri
import com.example.itplaneta.data.database.Account
import com.example.itplaneta.otp.OtpDigest
import com.example.itplaneta.otp.OtpType
import javax.inject.Singleton

@Singleton
class UriOtpParser {

    fun uriOtpParser(uri: String): Account? {

        val uri = Uri.parse(uri)
        val schema = uri.scheme
        if (schema != "otpauth") {
            return null
        }
        val otpType = when (uri.path) {
            "totp" -> OtpType.Totp

            "hotp" -> OtpType.Hotp

            else -> return null
        }

        val label = try {
            uri.pathSegments[0]
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val secret = uri.getQueryParameter("secret") ?: null
        val issuer = uri.getQueryParameter("issuer") ?: null

        val algorithm = when (val algotype = uri.getQueryParameter("algorithm")) {
            null -> OtpDigest.Sha1
            else -> OtpDigest.Sha1
        }
        val digits = uri.getQueryParameter("digits") ?: "6"


        val paramPeriod = uri.getQueryParameter("period") ?: "30"
        val period = try {
            if (otpType == OtpType.Hotp) null else paramPeriod.toInt()
        } catch (e: java.lang.Exception) {
            return throw e
        }

        val paramCounter = uri.getQueryParameter("counter")
        if (otpType == OtpType.Hotp && paramCounter == null) {
            return null
        }
        val counter = try {
            paramCounter?.toInt()
        } catch (e: NumberFormatException) {
            return throw e
        }


        return Account(
            id = 0,
            label = label.toString(),
            tokenType = otpType,
            counter = counter ?: 0,
            secret = secret.toString(),
            period = period ?: 30,
            digits = digits.toInt(),
            algorithm = algorithm,
            issuer = issuer
        )
    }

}