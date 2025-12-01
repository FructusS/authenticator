package com.example.itplaneta.domain

import com.example.itplaneta.core.utils.Result
import android.net.Uri
import com.example.itplaneta.data.backup.BackupMessage
import com.example.itplaneta.data.backup.BackupResult

interface IAccountBackupManager {
    suspend fun backupToUri(uri: Uri): BackupResult<BackupMessage>
    suspend fun restoreFromUri(uri: Uri): BackupResult<BackupMessage>
}