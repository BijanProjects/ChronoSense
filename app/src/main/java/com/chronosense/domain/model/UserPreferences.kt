package com.chronosense.domain.model

import java.time.Duration
import java.time.LocalTime

/**
 * User-configurable schedule & notification preferences.
 *
 * All defaults match a "typical" user: awake 7 AM–11 PM, 2-hour check-ins.
 */
data class UserPreferences(
    val wakeTime: LocalTime = LocalTime.of(7, 0),
    val sleepTime: LocalTime = LocalTime.of(23, 0),
    val intervalMinutes: Int = 120,
    val notificationsEnabled: Boolean = true,
    val dynamicColors: Boolean = false
) {
    val wakingMinutes: Long get() = Duration.between(wakeTime, sleepTime).toMinutes()

    /** Total number of check-in slots in a day. */
    val totalSlots: Int
        get() {
            if (intervalMinutes <= 0) return 0
            val full = (wakingMinutes / intervalMinutes).toInt()
            val hasPartial = wakingMinutes % intervalMinutes > 0
            return full + if (hasPartial) 1 else 0
        }

    companion object {
        val INTERVAL_OPTIONS = listOf(30, 60, 90, 120, 180, 240)
    }
}
