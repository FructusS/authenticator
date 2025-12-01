package com.example.itplaneta.data.backup

import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.domain.IBackupRepository
import com.google.crypto.tink.Aead
import com.google.crypto.tink.subtle.Base64
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.map

@Singleton
class BackupRepository @Inject constructor(
    private val aead: Aead
) : IBackupRepository {

    companion object {
        private const val ASSOCIATED_DATA = "backup_data"
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        prettyPrint = false
        isLenient = true
        ignoreUnknownKeys = true      // новые поля в будущем не сломают старые бэкапы
        coerceInputValues = true      // дефолты для enum'ов / полей, если значение не совпало
        decodeEnumsCaseInsensitive = true // "totp" / "TOTP" тоже прочитаются
    }

    override fun serializeAccounts(accounts: List<Account>): String =
        json.encodeToString(accounts.map { it.toBackupDto() })

    override fun deserializeAccounts(jsonString: String): List<Account> =
        json.decodeFromString<List<AccountBackupDto>>(jsonString).map { it.toAccount() }

    override fun encryptBackup(data: String): String {
        try {
            Timber.d("encryptBackup: starting (size = ${data.length} bytes)")

            val plaintext = data.toByteArray(StandardCharsets.UTF_8)
            val associatedData = ASSOCIATED_DATA.toByteArray(StandardCharsets.UTF_8)
            val ciphertext = aead.encrypt(plaintext, associatedData)
            val encoded = Base64.encodeToString(ciphertext, Base64.NO_WRAP)

            Timber.d("encryptBackup: success (encoded size = ${encoded.length} bytes)")
            return encoded
        } catch (e: Exception) {
            Timber.e(e, "encryptBackup failed")
            throw Exception("Encryption failed: ${e.message}", e)
        }
    }

    override fun decryptBackup(encryptedData: String): String {
        try {
            Timber.d("decryptBackup: starting (encoded size = ${encryptedData.length} bytes)")

            val ciphertext = Base64.decode(encryptedData, Base64.NO_WRAP)
            val associatedData = ASSOCIATED_DATA.toByteArray(StandardCharsets.UTF_8)
            val plaintext = aead.decrypt(ciphertext, associatedData)
            val result = String(plaintext, StandardCharsets.UTF_8)

            Timber.d("decryptBackup: success (decrypted size = ${result.length} bytes)")
            return result
        } catch (e: Exception) {
            Timber.e(e, "decryptBackup failed")
            throw Exception("Decryption failed: ${e.message}", e)
        }
    }
}