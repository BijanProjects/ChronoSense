package com.chronosense.data.local

import androidx.room.*
import com.chronosense.data.model.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for journal_entries table.
 *
 * All temporal parameters are ISO-8601 strings — no TypeConverters needed.
 * Indexed columns: `date`, `(date, startTime)` UNIQUE.
 */
@Dao
interface JournalDao {

    @Query("SELECT * FROM journal_entries WHERE date = :date ORDER BY startTime ASC")
    fun observeByDate(date: String): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE date BETWEEN :start AND :end ORDER BY date ASC, startTime ASC")
    fun observeByDateRange(start: String, end: String): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getById(id: Long): JournalEntryEntity?

    @Query("SELECT * FROM journal_entries WHERE date = :date AND startTime = :startTime LIMIT 1")
    suspend fun getBySlot(date: String, startTime: String): JournalEntryEntity?

    @Upsert
    suspend fun upsert(entity: JournalEntryEntity): Long

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM journal_entries WHERE date BETWEEN :start AND :end")
    fun observeCountForRange(start: String, end: String): Flow<Int>
}
