package com.chronosense.core.algorithm

import com.chronosense.domain.model.JournalEntry
import com.chronosense.domain.model.TimeSlot
import java.time.Duration
import java.time.LocalTime

/**
 * High-performance interval engine for time slot generation and entry matching.
 *
 * ### Complexity
 * - [generateSlots]: **O(S + E·log E)** where S = number of slots, E = number of entries
 * - [findActiveSlotIndex]: **O(1)** arithmetic lookup
 * - [nextNotificationTime]: **O(1)** arithmetic lookup
 *
 * ### Algorithm (generateSlots)
 * 1. Generate slot boundaries from wake → sleep in O(S).
 * 2. Sort entries by startTime — O(E·log E); typically already sorted from DB.
 * 3. Two-pointer merge: advance through slots and entries simultaneously in O(S + E).
 *
 * This avoids the naïve O(S × E) nested-loop approach that degrades at scale
 * (e.g., 16 slots × 16 entries = 256 comparisons → 32 with two-pointer merge).
 *
 * Completely stateless and deterministic. No Android dependencies—KMP-ready.
 */
object IntervalEngine {

    /**
     * Generates the full day's time slots and merges in existing entries.
     *
     * @param wakeTime        start of the user's day
     * @param sleepTime       end of the user's day (must be after [wakeTime])
     * @param intervalMinutes minutes per slot (> 0)
     * @param entries         existing journal entries (any order)
     * @return ordered list of [TimeSlot]s spanning wake → sleep
     */
    fun generateSlots(
        wakeTime: LocalTime,
        sleepTime: LocalTime,
        intervalMinutes: Int,
        entries: List<JournalEntry>
    ): List<TimeSlot> {
        if (intervalMinutes <= 0 || !wakeTime.isBefore(sleepTime)) return emptyList()

        val totalMinutes = Duration.between(wakeTime, sleepTime).toMinutes()
        val capacity = (totalMinutes / intervalMinutes).toInt() +
                if (totalMinutes % intervalMinutes > 0L) 1 else 0

        // Pre-sort entries for O(S+E) merge — skip if already trivially sorted
        val sorted = when {
            entries.size <= 1 -> entries
            else -> entries.sortedBy { it.startTime }
        }

        val slots = ArrayList<TimeSlot>(capacity)
        var current = wakeTime
        var ei = 0  // entry index

        while (current.isBefore(sleepTime)) {
            val slotEnd = minOf(current.plusMinutes(intervalMinutes.toLong()), sleepTime)

            // Two-pointer: skip entries before current slot, match on equality
            var matched: JournalEntry? = null
            while (ei < sorted.size) {
                val e = sorted[ei]
                when {
                    e.startTime == current -> { matched = e; ei++; break }
                    e.startTime.isBefore(current) -> ei++           // stale entry, skip
                    else -> break                                    // entry is ahead, stop
                }
            }

            slots.add(TimeSlot(current, slotEnd, matched))
            current = slotEnd
        }

        return slots
    }

    /**
     * O(1) lookup of the currently-active slot index.
     *
     * Returns -1 if [currentTime] is before [wakeTime].
     */
    fun findActiveSlotIndex(
        wakeTime: LocalTime,
        intervalMinutes: Int,
        currentTime: LocalTime
    ): Int {
        if (currentTime.isBefore(wakeTime) || intervalMinutes <= 0) return -1
        return (Duration.between(wakeTime, currentTime).toMinutes() / intervalMinutes).toInt()
    }

    /**
     * O(1) computation of the next notification trigger time.
     *
     * Returns `null` if all intervals have already passed for today.
     */
    fun nextNotificationTime(
        wakeTime: LocalTime,
        sleepTime: LocalTime,
        intervalMinutes: Int,
        currentTime: LocalTime
    ): LocalTime? {
        if (!currentTime.isBefore(sleepTime) || intervalMinutes <= 0) return null

        val effective = if (currentTime.isBefore(wakeTime)) wakeTime else currentTime
        val elapsed = Duration.between(wakeTime, effective).toMinutes()
        val nextSlotIdx = (elapsed / intervalMinutes).toInt() + 1
        val next = wakeTime.plusMinutes((nextSlotIdx.toLong() * intervalMinutes))

        return if (!next.isAfter(sleepTime)) next else null
    }

    /** Null-safe minimum of two [LocalTime]s. */
    private fun minOf(a: LocalTime, b: LocalTime): LocalTime = if (a.isBefore(b)) a else b
}
