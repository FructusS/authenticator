package com.example.itplaneta.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.itplaneta.ui.theme.AppTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class AppSettingsRepositoryTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun defaultThemeIsAuto() = runTest {
        val repository = createRepository()

        assertEquals(AppTheme.Auto, repository.themeFlow.first())
    }

    @Test
    fun savedThemeIsExposedInThemeFlow() = runTest {
        val repository = createRepository()

        repository.saveTheme(AppTheme.Dark)

        assertEquals(AppTheme.Dark, repository.themeFlow.first())
    }

    private fun TestScope.createRepository(): AppSettingsRepository {
        val settingsFile = temporaryFolder.newFolder().resolve("settings.preferences_pb")
        val dataStore = PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = { settingsFile }
        )
        return AppSettingsRepository(dataStore)
    }
}
