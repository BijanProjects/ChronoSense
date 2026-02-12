package com.chronosense.data.cache

import com.chronosense.domain.model.JournalEntry
import java.time.LocalDate

/**
 * Bounded LRU cache for recently accessed day entries.
 *
 * Keeps the last [maxSize] days in memory to avoid redundant Room queries
 * during rapid day-swiping. Thread-safe via `@Synchronized`.
 *
 * ### Eviction Policy
 * Least-recently-accessed days are evicted first when capacity is reached.
 *
 * ### Invalidation
 * - [invalidate] on single-day write (insert/update/delete).
 * - [invalidateAll] on preference changes that alter the slot grid.
 */
class DayCache(private val maxSize: Int = 14) {

    private data class CacheEntry(
        val entries: List<JournalEntry>,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val store = LinkedHashMap<LocalDate, CacheEntry>(maxSize + 1, 0.75f, true)

    @Synchronized
    fun get(date: LocalDate): List<JournalEntry>? = store[date]?.entries

    @Synchronized
    fun put(date: LocalDate, entries: List<JournalEntry>) {
        store[date] = CacheEntry(entries)
        if (store.size > maxSize) {
            store.remove(store.entries.first().key)
        }
    }

    @Synchronized
    fun invalidate(date: LocalDate) { store.remove(date) }

    @Synchronized
    fun invalidateAll() { store.clear() }
}
