package com.ifs21035.lostfounds.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ifs21035.lostfounds.data.local.entity.LostFoundEntity

@Database(entities = [LostFoundEntity::class], version = 1, exportSchema = false)
abstract class LostFoundDatabase : RoomDatabase() {
    abstract fun lostFoundDao(): ILostFoundDao
    companion object {
        private const val Database_NAME = "LostFound.db"
        @Volatile
        private var INSTANCE: LostFoundDatabase? = null
        @JvmStatic
        fun getInstance(context: Context): LostFoundDatabase {
            if (INSTANCE == null) {
                synchronized(LostFoundDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        LostFoundDatabase::class.java,
                        Database_NAME
                    ).build()
                }
            }
            return INSTANCE as LostFoundDatabase
        }
    }
}
