package com.chronosense.util

object Constants {
    const val NOTIFICATION_CHANNEL_ID = "chronosense_reminders"
    const val NOTIFICATION_ID = 1001
    const val ALARM_REQUEST_CODE = 2001

    const val EXTRA_DATE = "extra_date"
    const val EXTRA_START_TIME = "extra_start_time"
    const val EXTRA_END_TIME = "extra_end_time"

    val DEFAULT_TAGS = listOf(
        "Work", "Exercise", "Social", "Creative", "Rest",
        "Learning", "Commute", "Meals", "Entertainment", "Self-care"
    )

    val MOODS = listOf(
        "🔥" to "Energetic",
        "😌" to "Calm",
        "🎯" to "Focused",
        "😴" to "Tired",
        "😰" to "Stressed",
        "😊" to "Happy",
        "😐" to "Neutral"
    )

    val INTERVAL_OPTIONS = listOf(30, 60, 90, 120, 180, 240)
}
