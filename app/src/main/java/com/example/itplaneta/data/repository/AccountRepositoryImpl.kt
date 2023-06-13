package com.example.itplaneta.data.repository

import com.example.itplaneta.data.sources.Account
import com.example.itplaneta.data.sources.database.AccountDao
import com.example.itplaneta.domain.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AccountRepositoryImpl @Inject constructor(private val accountDao: AccountDao) :
    AccountRepository {


    private val accountList: Flow<List<Account>> = accountDao.getAllAccountsFlow()

    //private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override suspend fun addAccount(newAccount: Account) {
        accountDao.addAccount(newAccount)
    }

    override suspend fun updateAccount(account: Account) {
        return accountDao.updateAccount(account = account)
    }

    override fun getAccounts(): Flow<List<Account>> {
        return accountList
    }

    override suspend fun deleteAccount(account: Account) {
        return accountDao.deleteAccount(account)
    }

    override fun getAccountById(id: Int): Account {
        return accountDao.getAccountById(id)
    }

    override suspend fun incrementHotpCounter(account: Account) {
        updateAccount(account.copy(counter = account.counter + 1))
    }

    override fun getAllAccounts() =

        accountDao.getAllAccounts()


    //    override suspend fun updateAccount(account : Account) = runBlocking{
//        this.launch(Dispatchers.IO) {
//            accountDao.updateAccount(account)
//        }
//    }
//    override fun getAccounts() : Flow<List<Account>> {
//        return  accountList
//    }
//
////    override suspend fun deleteAccount(account: Account) = runBlocking{
////        this.launch(Dispatchers.IO) {
////            accountDao.deleteAccount(account)
////        }
////    }
//
//    override suspend fun getAccountById(id: Int) : Account
//            =  accountDao.getAccountById(id)
//
//    suspend fun incrementHotpCounter(id: Int) {
//        val account = getAccountById(id)
//        updateAccount( account.copy(counter = account.counter + 1))
//    }
//
//
//    fun getAllAccounts() = accountDao.getAllAccounts()

}