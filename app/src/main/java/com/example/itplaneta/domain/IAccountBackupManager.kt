package com.example.itplaneta.domain

import android.net.Uri
import com.example.itplaneta.domain.model.BackupMessage
import com.example.itplaneta.domain.model.BackupResult

interface IAccountBackupManager {
    suspend fun backupToUri(uri: Uri): BackupResult<BackupMessage>
    suspend fun restoreFromUri(uri: Uri): BackupResult<BackupMessage>
}
