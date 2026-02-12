package com.chronosense.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for journal entries—maps 1:1 to the `journal_entries` SQLite table.
 *
 * All temporal fields are stored as ISO-8601 strings for maximum portability
 * and zero TypeConverter overhead. Domain-layer conversion is handled by [EntityMapper].
 *
 * ### Indices
 * - `date` — fast day-view queries
 * - `(date, startTime)` UNIQUE — prevents duplicate slots per day
 */
@Entity(
    tableName = "journal_entries",
    indices = [
        Index(value = ["date"]),
        Index(value = ["date", "startTime"], unique = true)
    ]
)
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,            // ISO-8601 LocalDate
    val startTime: String,       // ISO-8601 LocalTime
    val endTime: String,         // ISO-8601 LocalTime
    val description: String = "",
    val mood: String = "",       // Mood.name or empty
    val tags: String = "",       // comma-separated ActivityTag labels
    val createdAt: Long = System.currentTimeMillis()
)
