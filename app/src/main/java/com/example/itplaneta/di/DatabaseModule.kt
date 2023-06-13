package com.example.itplaneta.di

import android.content.Context
import com.example.itplaneta.data.sources.database.AccountDao
import com.example.itplaneta.data.repository.AccountRepositoryImpl
import com.example.itplaneta.data.sources.database.AccountDatabase
import com.example.itplaneta.domain.AccountRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module

@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AccountDatabase {
        return AccountDatabase.getDatabase(context)
    }

    @Provides
    fun provideAccountDao(accountDatabase: AccountDatabase): AccountDao {
        return accountDatabase.accountDao()
    }

}


@Module

@InstallIn(SingletonComponent::class)
interface RepositoryModule{
    @Binds

    fun provideAccountRepository(
        impl: AccountRepositoryImpl
    ): AccountRepository

}