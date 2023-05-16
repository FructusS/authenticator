package com.example.itplaneta.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAccount(account: Account)

    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): List<Account>

    @Query("SELECT * FROM accounts")
    fun getAll(): Flow<List<Account>>

    @Update
    suspend fun updateAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)
    @Query("SELECT * FROM accounts WHERE secret = :secret")
    fun getAccountBySecret(secret: String) : Account?

    @Query("SELECT * FROM accounts WHERE id = :id")
     fun getAccountById(id: Int): Account


}