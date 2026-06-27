package com.example.itplaneta.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.itplaneta.domain.IAppSettingsRepository
import com.example.itplaneta.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : IAppSettingsRepository {

    private companion object {
        val THEME_KEY = stringPreferencesKey("theme_key")
    }

    override val themeFlow: Flow<AppTheme> = dataStore.data.map { preferences ->
        val name = preferences[THEME_KEY] ?: AppTheme.Auto.name
        AppTheme.fromName(name)
    }

    override suspend fun saveTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}
