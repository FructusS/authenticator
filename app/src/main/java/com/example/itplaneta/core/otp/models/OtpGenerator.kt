package com.example.itplaneta.core.otp.models

import com.example.itplaneta.core.otp.parser.Base32
import timber.log.Timber
import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.floor
import kotlin.math.pow

@Singleton
class OtpGenerator @Inject constructor(private val base32 : Base32) {

    /**
    Generate HMAC-based OTP (counter-based)
    RFC 4226 compliant
     */

    fun generateHotp(
        secret: ByteArray,
        counter: Long,
        digits: Int,
        digest: OtpAlgorithm
    ): String = try {
        val hash = Mac.getInstance(digest.algorithmName).let { mac ->
            val byteCounter = ByteBuffer.allocate(8).putLong(counter).array()
            mac.init(SecretKeySpec(secret, "RAW"))
            mac.doFinal(byteCounter)
        }

        val offset = hash[hash.size - 1].toInt() and 0xF
        val code = ((hash[offset].toInt() and 0x7f) shl 24) or
                ((hash[offset + 1].toInt() and 0xff) shl 16) or
                ((hash[offset + 2].toInt() and 0xff) shl 8) or
                ((hash[offset + 3].toInt() and 0xff))

        val paddedCode = (code % 10.0.pow(digits.toDouble())).toInt()

        StringBuilder(paddedCode.toString()).apply {
            while (length < digits) {
                insert(0, "0")
            }
        }.toString()
    } catch (e: Exception) {
        Timber.e(e, "Error generating HOTP")
        throw IllegalStateException("Failed to generate HOTP code", e)
    }

    /**
    Generate Time-based OTP (time-based)
    RFC 6238 compliant
    Default period: 30 seconds
     */
    fun generateTotp(
        secret: ByteArray,
        interval: Long,
        seconds: Long,
        digits: Int,
        digest: OtpAlgorithm
    ): String = try {
        val counter = floor((seconds / interval).toDouble()).toLong()
        generateHotp(secret, counter, digits, digest)
    } catch (e: Exception) {
        Timber.e(e, "Error generating TOTP")
        throw IllegalStateException("Failed to generate TOTP code", e)
    }

    /**
    Transform Base32-encoded secret to byte array
    Handles spaces and dashes in secret
     */
    fun transformToBytes(key: String): ByteArray = try {
        val trimmed = key.trim().replace("-", "").replace(" ", "").uppercase()
        require(trimmed.isNotEmpty()) { "Secret cannot be empty" }
        base32.decodeBase32(trimmed)
    } catch (e: Exception) {
        Timber.e(e, "Error decoding Base32 secret")
        throw IllegalArgumentException("Invalid Base32 secret", e)
    }
}