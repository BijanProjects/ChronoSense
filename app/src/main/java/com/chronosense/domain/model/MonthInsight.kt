package com.chronosense.domain.model

import java.time.YearMonth

/**
 * Monthly analytics derived from all journal entries within a [YearMonth].
 *
 * All aggregation happens in [aggregate] via a single O(n) pass over entries,
 * using mutable frequency maps to avoid repeated iteration.
 */
data class MonthInsight(
    val yearMonth: YearMonth,
    val totalEntries: Int,
    val activeDays: Int,
    val tagFrequency: Map<ActivityTag, Int>,
    val moodFrequency: Map<Mood, Int>,
    val daysWithEntries: Set<Int>,
    val averageCompletionRate: Float
) {
    val topActivity: ActivityTag? get() = tagFrequency.maxByOrNull { it.value }?.key
    val dominantMood: Mood? get() = moodFrequency.maxByOrNull { it.value }?.key

    companion object {
        /**
         * Single-pass O(n) aggregation of monthly insights.
         *
         * @param yearMonth     target month
         * @param entries       all journal entries within the month
         * @param slotsPerDay   number of time slots per day (for completion calculation)
         */
        fun aggregate(
            yearMonth: YearMonth,
            entries: List<JournalEntry>,
            slotsPerDay: Int
        ): MonthInsight {
            val tagFreq = HashMap<ActivityTag, Int>(ActivityTag.entries.size)
            val moodFreq = HashMap<Mood, Int>(Mood.entries.size)
            val daySet = HashSet<Int>()

            for (entry in entries) {
                daySet.add(entry.date.dayOfMonth)

                entry.mood?.let { m ->
                    moodFreq[m] = (moodFreq[m] ?: 0) + 1
                }
                for (tag in entry.tags) {
                    tagFreq[tag] = (tagFreq[tag] ?: 0) + 1
                }
            }

            val totalSlots = daySet.size * slotsPerDay.coerceAtLeast(1)
            val avgCompletion = if (totalSlots > 0) entries.size.toFloat() / totalSlots else 0f

            return MonthInsight(
                yearMonth = yearMonth,
                totalEntries = entries.size,
                activeDays = daySet.size,
                tagFrequency = tagFreq.entries
                    .sortedByDescending { it.value }
                    .associate { it.key to it.value },
                moodFrequency = moodFreq.entries
                    .sortedByDescending { it.value }
                    .associate { it.key to it.value },
                daysWithEntries = daySet,
                averageCompletionRate = avgCompletion
            )
        }
    }
}
