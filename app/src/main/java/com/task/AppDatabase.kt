package com.task

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.task.roomDB.Converters
import com.task.roomDB.DefaultGroupDao

@Database(entities = [LocationListData::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): DefaultGroupDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context,
                        AppDatabase::class.java, "task.db")
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

    }

}