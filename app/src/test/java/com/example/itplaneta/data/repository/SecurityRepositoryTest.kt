package com.example.itplaneta.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class SecurityRepositoryTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun enablingBiometricWithoutEnabledPinStoresDisabledFlag() = runTest {
        val repository = createRepository()

        repository.setBiometricEnabled(true)

        assertFalse(repository.isBiometricEnabledFlow.first())
    }

    @Test
    fun savedPinValidatesOnlyMatchingInput() = runTest {
        val repository = createRepository()

        repository.savePin("123456")

        assertTrue(repository.isPinValid("123456"))
        assertFalse(repository.isPinValid("000000"))
    }

    private fun TestScope.createRepository(): SecurityRepository {
        val settingsFile = temporaryFolder.newFolder().resolve("settings.preferences_pb")
        val dataStore = PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = { settingsFile }
        )
        return SecurityRepository(dataStore)
    }
}
