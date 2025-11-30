package com.example.itplaneta.core.otp.parser

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Base32 @Inject constructor() {
    companion object {
        private const val BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        private const val BITS_PER_CHAR = 5
        private const val BITS_PER_BYTE = 8
    }

    fun decodeBase32(input: String): ByteArray {
        val secret = input.replace(" ", "").uppercase()

        // Single validation
        validateBase32(secret)

        return convertBase32ToBytes(secret)
    }

    private fun validateBase32(input: String) {
        require(input.isNotEmpty()) { "Base32 string cannot be empty" }
        require(input.all { it in BASE32_CHARS }) {
            "String contains invalid Base32 characters"
        }
    }

    private fun convertBase32ToBytes(secret: String): ByteArray {
        val bytes = ByteArray(secret.length * BITS_PER_CHAR / BITS_PER_BYTE)
        var byteIndex = 0
        var bitsRemaining = BITS_PER_BYTE
        var currentByte = 0

        for (char in secret) {
            val charValue = BASE32_CHARS.indexOf(char)

            if (bitsRemaining > BITS_PER_CHAR) {
                currentByte = (currentByte shl BITS_PER_CHAR) or charValue
                bitsRemaining -= BITS_PER_CHAR
            } else {
                currentByte = (currentByte shl bitsRemaining) or (charValue ushr (BITS_PER_CHAR - bitsRemaining))
                bytes[byteIndex++] = currentByte.toByte()
                currentByte = charValue
                bitsRemaining += 3
            }
        }

        return bytes
    }
}
