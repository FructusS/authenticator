package com.example.itplaneta.domain

import com.example.itplaneta.data.sources.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun addAccount(newAccount: Account)
    suspend fun updateAccount(account: Account)
    fun getAccounts(): Flow<List<Account>>

    suspend fun deleteAccount(account: Account)

    fun getAccountById(id: Int) : Account
    suspend fun incrementHotpCounter(account: Account)


    fun getAllAccounts() : List<Account>

}