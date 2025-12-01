package com.example.itplaneta.ui.screens.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
        private val THEME_KEY = intPreferencesKey("theme_key")

    }


    val getTheme: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        AppTheme.fromOrdinal(preferences[THEME_KEY] ?: 2)
    }

    suspend fun saveTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.ordinal
        }
    }
}

enum class AppTheme {
    Light,
    Dark,
    Auto;
    companion object{
        fun fromOrdinal(ordinal : Int) : AppTheme {
          return  AppTheme.entries[ordinal]
        }

    }
}
