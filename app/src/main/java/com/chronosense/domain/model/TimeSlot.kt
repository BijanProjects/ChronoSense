package com.chronosense.domain.model

import java.time.Duration
import java.time.LocalTime

/**
 * A time slot within a day's schedule. May or may not contain a journal [entry].
 */
data class TimeSlot(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val entry: JournalEntry? = null
) {
    val isFilled: Boolean get() = entry != null
    val durationMinutes: Int get() = Duration.between(startTime, endTime).toMinutes().toInt()
}
