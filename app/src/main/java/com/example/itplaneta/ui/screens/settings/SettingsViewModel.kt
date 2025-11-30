package com.example.itplaneta.ui.screens.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.core.utils.Result
import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.domain.IAccountRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.*
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager,
    private val accountRepository: IAccountRepository
) : ViewModel() {

    private val _themeState = MutableStateFlow(settingsManager.getTheme)
    val themeState = _themeState.asStateFlow()

    private val _backupState = MutableStateFlow<Result<String>>(Result.Loading)
    val backupState = _backupState.asStateFlow()

    private val accountList = accountRepository.getAccounts()

    fun saveTheme(theme: AppTheme) {
        viewModelScope.launch {
            try {
                settingsManager.saveTheme(theme)
                Timber.d("Theme saved: $theme")
            } catch (e: Exception) {
                Timber.e(e, "Error saving theme")
            }
        }
    }

    fun saveBackupToExternal(uri: Uri) {
        viewModelScope.launch {
            _backupState.value = Result.Loading
            try {
                accountList.collect { list ->
                    context.contentResolver.openFileDescriptor(uri, "w")?.use { fd ->
                        FileOutputStream(fd.fileDescriptor).use { outputStream ->
                            val json = GsonBuilder().setPrettyPrinting().create().toJson(list)
                            outputStream.write(json.toByteArray())
                            _backupState.value = Result.Success("Backup saved successfully")
                            Timber.d("Backup saved: ${list.size} accounts")
                        }
                    }
                }
            } catch (e: FileNotFoundException) {
                Timber.e(e, "File not found for backup")
                _backupState.value = Result.Error(e, "File not found")
            } catch (e: IOException) {
                Timber.e(e, "Error saving backup")
                _backupState.value = Result.Error(e, "Failed to save backup")
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error during backup")
                _backupState.value = Result.Error(e, "Unexpected error: ${e.message}")
            }
        }
    }

    fun restoreBackupFromExternal(uri: Uri) {
        viewModelScope.launch {
            _backupState.value = Result.Loading
            try {
                val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        reader.readText()
                    }
                } ?: throw IOException("Cannot open file")

                val listType = object : TypeToken<List<Account>>() {}.type
                val accounts = Gson().fromJson<List<Account>>(json, listType)

                if (accounts.isEmpty()) {
                    throw IllegalArgumentException("Backup file is empty")
                }

                accounts.forEach { account ->
                    try {
                        accountRepository.addAccount(account)
                    } catch (e: Exception) {
                        Timber.e(e, "Error importing account: ${account.label}")
                    }
                }

                _backupState.value = Result.Success("Restored ${accounts.size} accounts")
                Timber.d("Backup restored: ${accounts.size} accounts")
            } catch (e: IOException) {
                Timber.e(e, "Error reading backup file")
                _backupState.value = Result.Error(e, "Failed to read backup file")
            } catch (e: Exception) {
                Timber.e(e, "Error restoring backup")
                _backupState.value = Result.Error(e, "Failed to restore backup: ${e.message}")
            }
        }
    }
}
