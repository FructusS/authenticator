package com.example.itplaneta.data.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class AccountRepository @Inject constructor(private val accountDao: AccountDao) {
    val accountList: Flow<List<Account>> = accountDao.getAll()

    fun addAccount(newAccount : Account) = runBlocking{
        this.launch(Dispatchers.IO)  {
            accountDao.addAccount(newAccount)
        }
    }
    fun updateAccount(account : Account) = runBlocking{
        this.launch(Dispatchers.IO) {
            accountDao.updateAccount(account)
        }
    }
    fun getAccounts() : Flow<List<Account>> {
        return  accountList
    }

    fun deleteAccount(account: Account) = runBlocking{
        this.launch(Dispatchers.IO) {
            accountDao.deleteAccount(account)
        }
    }

    fun getAccountBySecret(secret: String): Account?
          =  accountDao.getAccountBySecret(secret)

    fun getAccountById(id: Int) : Account
    =  accountDao.getAccountById(id)

    fun incrementHotpCounter(id: Int) {
        val account = getAccountById(id)
        updateAccount( account.copy(counter = account.counter + 1))
    }

}