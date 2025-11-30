package com.example.itplaneta.data.sources.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.itplaneta.data.sources.Account

@Database(entities = [Account::class], version = 3, exportSchema = true)
abstract class AccountDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var Instance: AccountDatabase? = null

        fun getDatabase(context: Context): AccountDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AccountDatabase::class.java, "AccountDatabase"
                ).build()

                Instance = instance
                instance
            }
        }
    }
}