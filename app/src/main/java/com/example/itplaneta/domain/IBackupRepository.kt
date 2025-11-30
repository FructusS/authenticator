package com.example.itplaneta.domain

import com.example.itplaneta.data.sources.Account

interface IBackupRepository {
    fun serializeAccounts(accounts: List<Account>): String
    fun deserializeAccounts(jsonString: String): List<Account>
    fun encryptBackup(data: String): String
    fun decryptBackup(encryptedData: String): String
}