package com.example.itplaneta.ui.screens.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.domain.AccountRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.*
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsManager: SettingsManager,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _themeState = MutableStateFlow(settingsManager.getTheme)
    val themeState = _themeState.asStateFlow().value
    fun saveTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsManager.saveTheme(theme)
        }
    }

    private val accountList = accountRepository.getAccounts()
    fun saveBackupToExternal(uri: Uri) {

        viewModelScope.launch {
            accountList.collect{list ->
                try {
                    try {
                        context.contentResolver.openFileDescriptor(uri, "w")?.use {
                            FileOutputStream(it.fileDescriptor).use { it ->
                                it.write(
                                    GsonBuilder().create().toJson(list)
                                        .toByteArray()
                                )
                            }
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                } catch (_: IOException) {

                }
            }
        }

    }

    fun restoreBackupFromExternal(uri: Uri) {

        val stringBuilder = StringBuilder()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        val listType = object : TypeToken<List<Account>>() {}.type
        val accounts = Gson().fromJson<List<Account>>(stringBuilder.toString(),listType)

        if (accounts.isNotEmpty()){
            accounts.forEach {
                    account ->
                viewModelScope.launch {
                    accountRepository.addAccount(account)

                }
            }
        }
    }

    private fun getAllAccounts() = accountRepository.getAllAccounts()




}