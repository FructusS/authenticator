package com.example.itplaneta.core.otp.models

/**
 * Enum for OTP algorithm types
 * Supports HMAC with SHA-1, SHA-256, and SHA-512
 */
enum class OtpAlgorithm(val algorithmName: String) {
    Sha1("HmacSHA1"),
    Sha256("HmacSHA256"),
    Sha512("HmacSHA512");

    companion object {
        fun fromString(value: String): OtpAlgorithm {
            return when (value.uppercase()) {
                "SHA1" -> Sha1
                "SHA256" -> Sha256
                "SHA512" -> Sha512
                else -> Sha1  // Default
            }
        }
    }
}
