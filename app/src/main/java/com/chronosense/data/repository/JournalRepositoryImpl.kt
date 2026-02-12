package com.chronosense.data.repository

import com.chronosense.data.cache.DayCache
import com.chronosense.data.local.JournalDao
import com.chronosense.data.mapper.EntityMapper
import com.chronosense.domain.model.JournalEntry
import com.chronosense.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Room-backed implementation of [JournalRepository].
 *
 * ### Caching Strategy
 * - On every `observeEntriesForDate` emission, the result is pushed into [DayCache].
 * - On every write (upsert/delete), the affected day is invalidated from cache.
 * - This ensures rapid day-swiping never hits the database for recently viewed days.
 */
class JournalRepositoryImpl(
    private val dao: JournalDao,
    private val cache: DayCache
) : JournalRepository {

    private val dateFmt = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFmt = DateTimeFormatter.ISO_LOCAL_TIME

    override fun observeEntriesForDate(date: LocalDate): Flow<List<JournalEntry>> =
        dao.observeByDate(date.format(dateFmt)).map { entities ->
            EntityMapper.toDomainList(entities).also { cache.put(date, it) }
        }

    override fun observeEntriesForDateRange(
        start: LocalDate, end: LocalDate
    ): Flow<List<JournalEntry>> =
        dao.observeByDateRange(start.format(dateFmt), end.format(dateFmt))
            .map { EntityMapper.toDomainList(it) }

    override suspend fun getEntryById(id: Long): JournalEntry? =
        dao.getById(id)?.let { EntityMapper.toDomain(it) }

    override suspend fun getEntryBySlot(date: LocalDate, startTime: LocalTime): JournalEntry? =
        dao.getBySlot(date.format(dateFmt), startTime.format(timeFmt))
            ?.let { EntityMapper.toDomain(it) }

    override suspend fun upsertEntry(entry: JournalEntry): Long {
        cache.invalidate(entry.date)
        return dao.upsert(EntityMapper.toEntity(entry))
    }

    override suspend fun deleteEntry(id: Long) {
        dao.getById(id)?.let {
            cache.invalidate(LocalDate.parse(it.date, dateFmt))
        }
        dao.deleteById(id)
    }

    override fun observeEntryCountForRange(start: LocalDate, end: LocalDate): Flow<Int> =
        dao.observeCountForRange(start.format(dateFmt), end.format(dateFmt))
}
