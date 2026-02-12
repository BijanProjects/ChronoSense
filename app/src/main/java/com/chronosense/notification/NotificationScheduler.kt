package com.chronosense.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.chronosense.core.algorithm.IntervalEngine
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/**
 * Schedules exact alarms for each remaining time-slot boundary today.
 *
 * Uses [IntervalEngine.generateSlots] for consistent slot-boundary calculation,
 * ensuring notifications fire at exactly the same times the UI displays.
 */
class NotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        private const val BASE_RC = 2000
        private const val MAX_ALARMS = 24
        const val EXTRA_DATE = "extra_date"
        const val EXTRA_START_TIME = "extra_start_time"
        const val EXTRA_END_TIME = "extra_end_time"
    }

    fun scheduleForToday(wakeTime: LocalTime, sleepTime: LocalTime, intervalMinutes: Int) {
        cancelAll()

        val now = LocalTime.now()
        val today = LocalDate.now()
        val slots = IntervalEngine.generateSlots(wakeTime, sleepTime, intervalMinutes, emptyList())

        var scheduled = 0
        for (slot in slots) {
            if (slot.endTime.isAfter(now)) {
                val triggerMs = today.atTime(slot.endTime)
                    .atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()

                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra(EXTRA_DATE, today.toString())
                    putExtra(EXTRA_START_TIME, slot.startTime.toString())
                    putExtra(EXTRA_END_TIME, slot.endTime.toString())
                }

                val pi = PendingIntent.getBroadcast(
                    context, BASE_RC + scheduled, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi)
                scheduled++
            }
        }
    }

    fun cancelAll() {
        for (i in 0 until MAX_ALARMS) {
            val intent = Intent(context, AlarmReceiver::class.java)
            PendingIntent.getBroadcast(
                context, BASE_RC + i, intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )?.let { alarmManager.cancel(it) }
        }
    }
}
