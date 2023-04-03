package com.example.itplaneta.utils

import androidx.compose.ui.res.stringResource
import com.example.itplaneta.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Base32 @Inject constructor() {

    fun decodeBase32(input : String): ByteArray {

        val secret = input.replace(" ","").uppercase()

        val base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
       // Проверяем, что строка содержит только символы из алфавита Base32

        for (char in secret) {
            if (char !in base32Chars) {
                throw IllegalArgumentException("String is not base32 char")
            }
        }

        // Проверяем, что длина строки кратна 8
        if (secret.length % 8 != 0) {
            throw IllegalArgumentException("String is not divisible by 8")
        }



        val bytes = ByteArray(secret.length * 5 / 8)
        var byteIndex = 0
        var bitsRemaining = 8
        var currentByte = 0

        for (char in secret) {
            val charValue = base32Chars.indexOf(char)
            if (charValue == -1) {
                throw IllegalArgumentException("Invalid Base32 character: $char")
            }
            if (bitsRemaining > 5) {
                currentByte = (currentByte shl 5) or charValue
                bitsRemaining -= 5
            } else {
                currentByte = (currentByte shl bitsRemaining) or (charValue ushr (5 - bitsRemaining))
                bytes[byteIndex++] = currentByte.toByte()
                currentByte = charValue
                bitsRemaining += 3
            }
        }

        // Если строка закончилась на неполный байт, то это ошибка
        if (bitsRemaining < 8 && currentByte shl bitsRemaining != 0) {
            throw IllegalArgumentException("Invalid Base32 string length")
        }

        return bytes
    }


}