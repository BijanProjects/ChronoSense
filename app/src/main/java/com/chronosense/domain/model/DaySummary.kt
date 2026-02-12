package com.chronosense.domain.model

import java.time.LocalDate

/**
 * Aggregated summary for a single day—derived from [TimeSlot] list.
 */
data class DaySummary(
    val date: LocalDate,
    val totalSlots: Int,
    val filledSlots: Int,
    val dominantMood: Mood?,
    val topTags: List<ActivityTag>,
    val completionRate: Float
) {
    companion object {
        /**
         * Single-pass O(n) aggregation from a list of time slots.
         * Uses [groupingBy] + [eachCount] for frequency extraction.
         */
        fun from(date: LocalDate, slots: List<TimeSlot>): DaySummary {
            val filled = slots.filter { it.isFilled }

            val moodFreq = filled
                .mapNotNull { it.entry?.mood }
                .groupingBy { it }
                .eachCount()

            val tagFreq = filled
                .flatMap { it.entry?.tags ?: emptyList() }
                .groupingBy { it }
                .eachCount()

            return DaySummary(
                date = date,
                totalSlots = slots.size,
                filledSlots = filled.size,
                dominantMood = moodFreq.maxByOrNull { it.value }?.key,
                topTags = tagFreq.entries
                    .sortedByDescending { it.value }
                    .take(3)
                    .map { it.key },
                completionRate = if (slots.isEmpty()) 0f
                else filled.size.toFloat() / slots.size
            )
        }
    }
}
