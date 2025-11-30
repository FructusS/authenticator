package com.example.itplaneta.data.repository

import com.example.itplaneta.core.utils.Result
import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.data.sources.database.AccountDao
import com.example.itplaneta.domain.IAccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val accountDao: AccountDao
) : IAccountRepository {

    override suspend fun addAccount(newAccount: Account): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            accountDao.addAccount(newAccount)
            Timber.d("Account added: ${newAccount.label}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error adding account")
            Result.Error(e, "Failed to add account: ${e.message}")
        }
    }

    override suspend fun updateAccount(account: Account): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            accountDao.updateAccount(account)
            Timber.d("Account updated: ${account.label}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error updating account")
            Result.Error(e, "Failed to update account: ${e.message}")
        }
    }

    override fun getAccounts(): Flow<List<Account>> = accountDao.getAllAccountsFlow()

    override suspend fun deleteAccount(account: Account): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            accountDao.deleteAccount(account)
            Timber.d("Account deleted: ${account.label}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting account")
            Result.Error(e, "Failed to delete account: ${e.message}")
        }
    }

    override suspend fun getAccountById(id: Int): Result<Account> = withContext(Dispatchers.IO) {
        return@withContext try {
            val account = accountDao.getAccountById(id)
            if (account != null) {
                Result.Success(account)
            } else {
                Result.Error(Exception("Account not found"), "Account with id $id not found")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting account by id")
            Result.Error(e, "Failed to fetch account: ${e.message}")
        }
    }

    override suspend fun incrementHotpCounter(account: Account): Result<Unit> {
        return updateAccount(account.copy(counter = account.counter + 1))
    }
}