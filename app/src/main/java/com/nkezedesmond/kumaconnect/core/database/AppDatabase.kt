package com.nkezedesmond.kumaconnect.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nkezedesmond.kumaconnect.core.database.dao.DeviceDao
import com.nkezedesmond.kumaconnect.core.database.dao.MessageDao
import com.nkezedesmond.kumaconnect.core.database.entities.Device
import com.nkezedesmond.kumaconnect.core.database.entities.Message

@Database(entities = [Message::class, Device::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun messageDao(): MessageDao
    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kuma_connect_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
