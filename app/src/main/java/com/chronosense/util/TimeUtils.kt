package com.chronosense.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimeUtils {

    private val timeFormatter12 = DateTimeFormatter.ofPattern("h:mm a")

    fun formatTime(time: LocalTime): String = time.format(timeFormatter12)

    fun formatTimeRange(start: LocalTime, end: LocalTime): String {
        return "${formatTime(start)} — ${formatTime(end)}"
    }

    fun generateTimeSlots(
        wakeTime: LocalTime,
        sleepTime: LocalTime,
        intervalMinutes: Int
    ): List<Pair<LocalTime, LocalTime>> {
        val slots = mutableListOf<Pair<LocalTime, LocalTime>>()
        var current = wakeTime

        while (current.plusMinutes(intervalMinutes.toLong()) <= sleepTime) {
            val end = current.plusMinutes(intervalMinutes.toLong())
            slots.add(current to end)
            current = end
        }

        if (current < sleepTime) {
            slots.add(current to sleepTime)
        }

        return slots
    }

    fun formatIntervalLabel(minutes: Int): String {
        return when {
            minutes < 60 -> "${minutes}min"
            minutes % 60 == 0 -> "${minutes / 60}h"
            else -> "${minutes / 60}h ${minutes % 60}min"
        }
    }
}
