package com.example.itplaneta.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.itplaneta.core.utils.PinHashUtils
import com.example.itplaneta.core.utils.PinHashUtils.fromBase64
import com.example.itplaneta.core.utils.PinHashUtils.toBase64
import com.example.itplaneta.ui.theme.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
        private val THEME_KEY = stringPreferencesKey("theme_key")
        private val PIN_ENABLED_KEY = booleanPreferencesKey("pin_enabled")
        private val PIN_HASH_KEY = stringPreferencesKey("pin_hash")
        private val PIN_SALT_KEY = stringPreferencesKey("pin_salt")
    }


    val getTheme: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        val name = preferences[THEME_KEY] ?: AppTheme.Auto.name
        AppTheme.fromName(name)
    }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    val isPinEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PIN_ENABLED_KEY] ?: false
    }

    suspend fun setPinEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PIN_ENABLED_KEY] = enabled
        }
    }

    suspend fun savePin(pin: String) {
        val salt = PinHashUtils.generateSalt()
        val hash = PinHashUtils.hashPin(pin, salt)

        context.dataStore.edit { prefs ->
            prefs[PIN_SALT_KEY] = salt.toBase64()
            prefs[PIN_HASH_KEY] = hash.toBase64()
        }
    }

    suspend fun isPinValid(input: String): Boolean {
        val prefs = context.dataStore.data.first()

        val saltBase64 = prefs[PIN_SALT_KEY] ?: return false
        val hashBase64 = prefs[PIN_HASH_KEY] ?: return false

        val salt = saltBase64.fromBase64()
        val expectedHash = hashBase64.fromBase64()
        val actualHash = PinHashUtils.hashPin(input, salt)

        return MessageDigest.isEqual(expectedHash, actualHash)
    }
}

