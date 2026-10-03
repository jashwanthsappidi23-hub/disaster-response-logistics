package com.example.daaproject.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.daaproject.data.dao.AppDao
import com.example.daaproject.data.entity.*

@Database(
    entities = [Scenario::class, Node::class, Edge::class, Resource::class, Demand::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "disaster_logistics_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
