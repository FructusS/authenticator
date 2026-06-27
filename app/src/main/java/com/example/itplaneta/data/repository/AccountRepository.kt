package com.example.itplaneta.data.repository

import com.example.itplaneta.core.utils.Result
import com.example.itplaneta.data.mapper.toDomain
import com.example.itplaneta.data.mapper.toEntity
import com.example.itplaneta.data.sources.database.AccountDao
import com.example.itplaneta.domain.IAccountRepository
import com.example.itplaneta.domain.model.Account
import com.example.itplaneta.domain.model.AccountResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


class AccountRepository @Inject constructor(
    private val accountDao: AccountDao
) : IAccountRepository {

    override suspend fun addAccount(newAccount: Account): AccountResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            accountDao.addAccount(newAccount.toEntity())
            Timber.d("Account added: ${newAccount.label}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error adding account")
            Result.Error(e, "Failed to add account: ${e.message}")
        }
    }

    override suspend fun updateAccount(account: Account): AccountResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            accountDao.updateAccount(account.toEntity())
            Timber.d("Account updated: ${account.label}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error updating account")
            Result.Error(e, "Failed to update account: ${e.message}")
        }
    }

    override fun getAccounts(): Flow<List<Account>> =
        accountDao.getAllAccountsFlow().map { accounts -> accounts.map { it.toDomain() } }

    override suspend fun deleteAccount(account: Account): AccountResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            accountDao.deleteAccount(account.toEntity())
            Timber.d("Account deleted: ${account.label}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting account")
            Result.Error(e, "Failed to delete account: ${e.message}")
        }
    }

    override suspend fun getAccountById(id: Int): AccountResult<Account> = withContext(Dispatchers.IO) {
        return@withContext try {
            val account = accountDao.getAccountById(id)
            if (account != null) {
                Result.Success(account.toDomain())
            } else {
                Result.Error(Exception("Account not found"), "Account with id $id not found")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting account by id")
            Result.Error(e, "Failed to fetch account: ${e.message}")
        }
    }

    override suspend fun incrementHotpCounter(account: Account): AccountResult<Unit> {
        return updateAccount(account.copy(counter = account.counter + 1))
    }
}
