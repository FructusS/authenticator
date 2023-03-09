package com.example.itplaneta

import java.net.URI


class OTPAuth(uri: String) {
    val issuer: String
    val accountName: String
    val secret: String
    val algorithm: String?
    val digits: Int?
    val period: Int?

    init {
        val parsedUri = URI.create(uri)
        if (parsedUri.scheme != "otpauth") {
            throw IllegalArgumentException("Invalid OTPAuth URI")
        }
        val parts = parsedUri.path.split(":", limit = 2)
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid account name in OTPAuth URI")
        }
        issuer = parsedUri.query?.let { query ->
            val params = query.split("&")
            params.associate {
                val pair = it.split("=", limit = 2)
                pair[0] to pair.getOrElse(1) { "" }
            }["issuer"]
        } ?: ""
        accountName = parts[1]
        secret = parsedUri.getQueryParameter("secret").toString()
        algorithm = parsedUri.getQueryParameter("algorithm")
        digits = parsedUri.getQueryParameter("digits")?.toIntOrNull()
        period = parsedUri.getQueryParameter("period")?.toIntOrNull()
    }

    private fun URI.getQueryParameter(key: String): String? {
        return query?.let { query ->
            val params = query.split("&")
            params.associate {
                val pair = it.split("=", limit = 2)
                pair[0] to pair.getOrElse(1) { "" }
            }[key]
        }
    }

    companion object {
        fun fromUri(uri: String): OTPAuth {
            return OTPAuth(uri)
        }
    }
}

fun main(){
    val first = "otpauth://totp/VK:fipuctus?secret=OPLXU64XHBZV563L&issuer=VK"
    val second = "otpauth://totp/Google%3Aafanasev10056%40gmail.com?secret=mpc5k7azgvbrtsmrcnjwkkrmwvi2xtdo&issuer=Google"
    fun decode(uri : String){
        val uri = URI.create(uri)

        if (uri.scheme != "otpauth"){

        }
        println(uri.host)
        println(uri.path)
    }
    decode(first)
 //   decode(first)
}