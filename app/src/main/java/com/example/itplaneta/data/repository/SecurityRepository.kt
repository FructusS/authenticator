package com.example.itplaneta.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.itplaneta.core.utils.PinHashUtils
import com.example.itplaneta.core.utils.PinHashUtils.fromBase64
import com.example.itplaneta.core.utils.PinHashUtils.toBase64
import com.example.itplaneta.domain.IBiometricSettingsRepository
import com.example.itplaneta.domain.IPinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : IPinRepository, IBiometricSettingsRepository {

    private companion object {
        val PIN_ENABLED_KEY = booleanPreferencesKey("pin_enabled")
        val PIN_HASH_KEY = stringPreferencesKey("pin_hash")
        val PIN_SALT_KEY = stringPreferencesKey("pin_salt")
        val BIOMETRIC_ENABLED_KEY = booleanPreferencesKey("biometric_enabled")
    }

    override val isPinEnabledFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[PIN_ENABLED_KEY] ?: false
    }

    override val isBiometricEnabledFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[BIOMETRIC_ENABLED_KEY] ?: false
    }

    override suspend fun setPinEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PIN_ENABLED_KEY] = enabled
            if (!enabled) {
                prefs.remove(PIN_HASH_KEY)
                prefs.remove(PIN_SALT_KEY)
                prefs[BIOMETRIC_ENABLED_KEY] = false
            }
        }
    }

    override suspend fun savePin(pin: String) {
        val salt = PinHashUtils.generateSalt()
        val hash = PinHashUtils.hashPin(pin, salt)

        dataStore.edit { prefs ->
            prefs[PIN_SALT_KEY] = salt.toBase64()
            prefs[PIN_HASH_KEY] = hash.toBase64()
        }
    }

    override suspend fun isPinValid(input: String): Boolean {
        val prefs = dataStore.data.first()

        val saltBase64 = prefs[PIN_SALT_KEY] ?: return false
        val hashBase64 = prefs[PIN_HASH_KEY] ?: return false

        val salt = saltBase64.fromBase64()
        val expectedHash = hashBase64.fromBase64()
        val actualHash = PinHashUtils.hashPin(input, salt)

        return MessageDigest.isEqual(expectedHash, actualHash)
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            val isPinEnabled = prefs[PIN_ENABLED_KEY] ?: false
            prefs[BIOMETRIC_ENABLED_KEY] = enabled && isPinEnabled
        }
    }
}
