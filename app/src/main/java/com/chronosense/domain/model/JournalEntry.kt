package com.chronosense.domain.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/**
 * Domain model for a single journal entry—pure Kotlin, no framework annotations.
 *
 * When migrating to KMP, swap [LocalDate]/[LocalTime] for kotlinx-datetime equivalents.
 */
data class JournalEntry(
    val id: Long = 0,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val description: String = "",
    val mood: Mood? = null,
    val tags: List<ActivityTag> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    /** Whether this entry has any user-provided content at all. */
    val hasContent: Boolean
        get() = description.isNotBlank() || mood != null || tags.isNotEmpty()

    /** Slot duration in whole minutes. */
    val durationMinutes: Int
        get() = Duration.between(startTime, endTime).toMinutes().toInt()
}
