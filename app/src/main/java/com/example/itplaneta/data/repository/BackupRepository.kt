package com.example.itplaneta.data.repository

import android.content.Context
import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.domain.IBackupRepository
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.subtle.Base64
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepository @Inject constructor(
    private val aead: Aead,
    private val json: Json = Json
) : IBackupRepository {

    companion object {
        private const val ASSOCIATED_DATA = "backup_data"
        private const val TINK_PREF_FILE = "tink_keyset_prefs"
        private const val TINK_PREF_KEY = "tink_keyset"
        private const val MASTER_KEY_ALIAS = "backup_master_key"
        private const val MASTER_KEY_URI = "android-keystore://$MASTER_KEY_ALIAS"
    }

    fun provideAead(@ApplicationContext context: Context): Aead {
        val manager = AndroidKeysetManager.Builder()
            .withSharedPref(context, TINK_PREF_KEY, TINK_PREF_FILE)
            .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()


        return manager.keysetHandle.getPrimitive(Aead::class.java)
    }

    override fun serializeAccounts(accounts: List<Account>): String = json.encodeToString(accounts)

    override fun deserializeAccounts(jsonString: String): List<Account> = json.decodeFromString(jsonString)

    override fun encryptBackup(data: String): String {
        try {
            Timber.d("encryptBackup: starting (size = ${'$'}{data.length} bytes)")

            val plaintext = data.toByteArray(StandardCharsets.UTF_8)
            val associatedData = ASSOCIATED_DATA.toByteArray(StandardCharsets.UTF_8)
            val ciphertext = aead.encrypt(plaintext, associatedData)
            val encoded = Base64.encodeToString(ciphertext, Base64.NO_WRAP)

            Timber.d("encryptBackup: success (encoded size = ${'$'}{encoded.length} bytes)")
            return encoded
        } catch (e: Exception) {
            Timber.e(e, "encryptBackup failed")
            throw Exception("Encryption failed: ${'$'}{e.message}", e)
        }
    }

    override fun decryptBackup(encryptedData: String): String {
        try {
            Timber.d("decryptBackup: starting (encoded size = ${'$'}{encryptedData.length} bytes)")

            val ciphertext = Base64.decode(encryptedData, Base64.NO_WRAP)
            val associatedData = ASSOCIATED_DATA.toByteArray(StandardCharsets.UTF_8)
            val plaintext = aead.decrypt(ciphertext, associatedData)
            val result = String(plaintext, StandardCharsets.UTF_8)

            Timber.d("decryptBackup: success (decrypted size = ${'$'}{result.length} bytes)")
            return result
        } catch (e: Exception) {
            Timber.e(e, "decryptBackup failed")
            throw Exception("Decryption failed: ${'$'}{e.message}", e)
        }
    }
}