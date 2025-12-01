package com.example.itplaneta.data.backup

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import com.example.itplaneta.R
import com.example.itplaneta.domain.IAccountBackupManager
import com.example.itplaneta.domain.IAccountRepository
import com.example.itplaneta.domain.IBackupRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import com.example.itplaneta.core.utils.Result
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader

data class BackupMessage(
    @param:StringRes val resId: Int,
    val arg: Int? = null
)

typealias BackupResult<T> = Result<T, BackupMessage>

@Singleton
class AccountBackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val accountRepository: IAccountRepository,
    private val backupRepository: IBackupRepository
) : IAccountBackupManager {

    override suspend fun backupToUri(uri: Uri): BackupResult<BackupMessage> =
        withContext(Dispatchers.IO) {
            try {
                val accounts = accountRepository.getAccounts().first()

                val json = backupRepository.serializeAccounts(accounts)
                val encrypted = backupRepository.encryptBackup(json)

                context.contentResolver.openFileDescriptor(uri, "w")?.use { fd ->
                    FileOutputStream(fd.fileDescriptor).use { outputStream ->
                        outputStream.write(encrypted.toByteArray())
                    }
                } ?: throw IOException("Cannot open file descriptor")

                Timber.d("Backup saved: ${accounts.size} accounts")
                Result.Success(
                    BackupMessage(
                        resId = R.string.backup_saved_successfully,
                        arg = accounts.size
                    )
                )
            } catch (e: FileNotFoundException) {
                Timber.e(e, "File not found for backup")
                Result.Error(
                    e,
                    BackupMessage(resId = R.string.backup_error_file_not_found)
                )
            } catch (e: IOException) {
                Timber.e(e, "Error saving backup")
                Result.Error(
                    e,
                    BackupMessage(resId = R.string.backup_error_io_save)
                )
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error during backup")
                Result.Error(
                    e,
                    BackupMessage(resId = R.string.backup_error_unexpected)
                )
            }
        }

    override suspend fun restoreFromUri(uri: Uri): BackupResult<BackupMessage> =
        withContext(Dispatchers.IO) {
            try {
                val encrypted = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        reader.readText()
                    }
                } ?: throw IOException("Cannot open file")

                val json = backupRepository.decryptBackup(encrypted)
                val accounts = backupRepository.deserializeAccounts(json)

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

                Timber.d("Backup restored: ${accounts.size} accounts")
                Result.Success(
                    BackupMessage(
                        resId = R.string.backup_restored_successfully,
                        arg = accounts.size
                    )
                )
            } catch (e: FileNotFoundException) {
                Timber.e(e, "File not found for restore")
                Result.Error(
                    e,
                    BackupMessage(resId = R.string.backup_error_file_not_found)
                )
            } catch (e: IOException) {
                Timber.e(e, "Error reading backup file")
                Result.Error(
                    e,
                    BackupMessage(resId = R.string.backup_error_io_read)
                )
            } catch (e: IllegalArgumentException) {
                Timber.e(e, "Backup file is empty or invalid")
                Result.Error(
                    e,
                    BackupMessage(resId = R.string.backup_error_empty)
                )
            } catch (e: Exception) {
                Timber.e(e, "Error restoring backup")
                Result.Error(
                    e,
                    BackupMessage(resId = R.string.backup_error_unexpected)
                )
            }
        }
}