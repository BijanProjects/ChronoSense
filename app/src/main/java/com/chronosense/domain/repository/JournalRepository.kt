package com.chronosense.domain.repository

import com.chronosense.domain.model.JournalEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

/**
 * Contract for journal entry persistence.
 *
 * All query methods return [Flow]s that emit fresh data on every DB change
 * (powered by Room under the hood). Command methods are suspend functions.
 *
 * Cross-platform: this interface has zero Android imports—only domain types + coroutines.
 */
interface JournalRepository {
    fun observeEntriesForDate(date: LocalDate): Flow<List<JournalEntry>>
    fun observeEntriesForDateRange(start: LocalDate, end: LocalDate): Flow<List<JournalEntry>>
    suspend fun getEntryById(id: Long): JournalEntry?
    suspend fun getEntryBySlot(date: LocalDate, startTime: LocalTime): JournalEntry?
    suspend fun upsertEntry(entry: JournalEntry): Long
    suspend fun deleteEntry(id: Long)
    fun observeEntryCountForRange(start: LocalDate, end: LocalDate): Flow<Int>
}
