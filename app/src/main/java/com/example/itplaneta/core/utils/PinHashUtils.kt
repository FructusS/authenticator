package com.example.itplaneta.core.utils

import com.google.crypto.tink.subtle.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object PinHashUtils {
    private const val HASH_ALGO = "SHA-256"
    private const val SALT_BYTES = 16

    fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(salt)
        return salt
    }

    fun hashPin(pin: String, salt: ByteArray): ByteArray {
        val md = MessageDigest.getInstance(HASH_ALGO)
        md.update(salt)
        md.update(pin.toByteArray(Charsets.UTF_8))
        return md.digest()
    }

    fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)

    fun String.fromBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)
}