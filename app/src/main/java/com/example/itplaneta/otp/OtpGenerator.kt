package com.example.itplaneta.otp

import com.example.itplaneta.utils.Base32
import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.floor
import kotlin.math.pow

@Singleton
class OtpGenerator @Inject constructor() {

    private val base32 = Base32()

    fun generateHotp(
        secret: ByteArray,
        counter: Long,
        digits: Int,
        digest: OtpAlgorithm
    ): String {
        val hash = Mac.getInstance(digest.algorithmName).let { mac ->
            val byteCounter = ByteBuffer.allocate(8)
                .putLong(counter)
                .array()

            mac.init(SecretKeySpec(secret, "RAW"))
            mac.doFinal(byteCounter)
        }

        val offset = hash[hash.size - 1].toInt() and 0xF

        val code = ((hash[offset].toInt() and 0x7f) shl 24) or
            ((hash[offset + 1].toInt() and 0xff) shl 16) or
            ((hash[offset + 2].toInt() and 0xff) shl 8) or
            ((hash[offset + 3].toInt() and 0xff))

        val paddedCode = (code % 10.0.pow(digits.toDouble())).toInt()

        return StringBuilder(paddedCode.toString()).apply {
            while (length < digits) {
                insert(0, "0")
            }
        }.toString()
    }

     fun generateTotp(
        secret: ByteArray,
        interval: Long,
        seconds: Long,
        digits: Int,
        digest: OtpAlgorithm
    ): String {
        val counter = floor((seconds / interval).toDouble()).toLong()
        return generateHotp(secret, counter, digits, digest)
    }

    private val OtpAlgorithm.algorithmName: String
        get() {
            return when (this) {
                OtpAlgorithm.Sha1 -> "HmacSHA1"
                OtpAlgorithm.Sha256 -> "HmacSHA256"
                OtpAlgorithm.Sha512 -> "HmacSHA512"
            }
        }
    fun transformToBytes(key: String): ByteArray {
        val trimmed = key.trim().replace("-", "").replace(" ", "")
        return base32.decodeBase32(trimmed)
    }

}