package com.chronosense.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chronosense.data.model.JournalEntryEntity

/**
 * Room database — single source of truth for journal data.
 *
 * v2: Migrated to JournalEntryEntity with string-based temporal columns
 * and composite indices for O(log n) date+time lookups.
 *
 * No TypeConverters required — all fields are primitives or Strings.
 */
@Database(
    entities = [JournalEntryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun journalDao(): JournalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chronosense_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
