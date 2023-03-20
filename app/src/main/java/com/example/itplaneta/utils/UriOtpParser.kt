package com.example.itplaneta.utils


import android.net.Uri
import android.util.Log
import com.example.itplaneta.data.database.Account
import com.example.itplaneta.otp.OtpDigest
import com.example.itplaneta.otp.OtpType
import java.net.URLDecoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UriOtpParser @Inject constructor() {
    fun uriOtpParser(uri: String): Account? {
        val otpstring = uri.replace("%40","@").replace("%3A",":")

        val uri = Uri.parse(otpstring)

        val schema = uri.scheme
        if (schema != "otpauth") {
            return null
        }
        val otpType = when (uri.path) {
            "totp" -> OtpType.Totp

            "hotp" -> OtpType.Hotp

            else -> {OtpType.Totp}
        }
        val issuer = uri.getQueryParameter("issuer") ?: ""

        val label = try {
            Log.i("123",      uri.pathSegments[0])
            uri.pathSegments[0].substring(issuer.count() + 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val secret = uri.getQueryParameter("secret") ?: ""

        val algorithm = when (val algotype = uri.getQueryParameter("algorithm")) {
            null -> OtpDigest.Sha1
            else -> OtpDigest.Sha1
        }
        val digits = uri.getQueryParameter("digits") ?: "6"


        val paramPeriod = uri.getQueryParameter("period") ?: "30"
        val period = if (otpType == OtpType.Hotp) null else paramPeriod.toInt()


        val paramCounter = uri.getQueryParameter("counter")
        if (otpType == OtpType.Hotp && paramCounter == null) {
            return null
        }
        val counter = paramCounter?.toInt()

        return Account(
            id = 0,
            label = label.toString(),
            tokenType = otpType,
            counter = counter ?: 0,
            secret = secret,
            period = period ?: 30,
            digits = digits.toInt(),
            algorithm = algorithm,
            issuer = issuer
        )
    }
}