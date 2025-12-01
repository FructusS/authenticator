package com.example.itplaneta.domain


import com.example.itplaneta.core.utils.Result
import com.example.itplaneta.data.repository.AccountResult
import com.example.itplaneta.data.sources.Account
import kotlinx.coroutines.flow.Flow


/**
 * Repository interface for account operations
 */
interface IAccountRepository {
    /**
     * Add a new account to the database
     */
    suspend fun addAccount(newAccount: Account): AccountResult<Unit>

    /**
     * Update an existing account
     */
    suspend fun updateAccount(account: Account): AccountResult<Unit>

    /**
     * Get all accounts as a Flow for reactive updates
     */
    fun getAccounts(): Flow<List<Account>>

    /**
     * Delete an account
     */
    suspend fun deleteAccount(account: Account): AccountResult<Unit>

    /**
     * Get a single account by ID
     */
    suspend fun getAccountById(id: Int): AccountResult<Account>

    /**
     * Increment HOTP counter for an account
     */
    suspend fun incrementHotpCounter(account: Account): AccountResult<Unit>
}