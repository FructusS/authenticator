package com.example.itplaneta.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [Account::class], version = 1)
abstract class AccountRoomDatabase : RoomDatabase() {
    abstract  fun accountDao() : AccountDao
}
