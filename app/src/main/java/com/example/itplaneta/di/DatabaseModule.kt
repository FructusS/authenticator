package com.example.itplaneta.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.itplaneta.data.database.AccountDao
import com.example.itplaneta.data.database.AccountRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module

@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun ProvideAccountDao(accountRoomDatabase: AccountRoomDatabase): AccountDao {
        return accountRoomDatabase.accountDao()
    }
    @Provides
    @Singleton
    fun ProvideAccountRoomDatabase(@ApplicationContext appContext: Context): AccountRoomDatabase {
        return Room.databaseBuilder(
            appContext,
            AccountRoomDatabase::class. java ,
            "AccountDatabase"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("drop table accountEntities RENAME TO accounts")
    }
}