package com.chronosense.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.chronosense.ChronoSenseApp
import com.chronosense.MainActivity
import com.chronosense.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val startStr = intent.getStringExtra(NotificationScheduler.EXTRA_START_TIME) ?: return
        val endStr = intent.getStringExtra(NotificationScheduler.EXTRA_END_TIME) ?: return
        val dateStr = intent.getStringExtra(NotificationScheduler.EXTRA_DATE) ?: return

        val fmt = DateTimeFormatter.ofPattern("h:mm a")
        val start = LocalTime.parse(startStr)
        val end = LocalTime.parse(endStr)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(NotificationScheduler.EXTRA_DATE, dateStr)
            putExtra(NotificationScheduler.EXTRA_START_TIME, startStr)
            putExtra(NotificationScheduler.EXTRA_END_TIME, endStr)
        }

        val pi = PendingIntent.getActivity(
            context, 0, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ChronoSenseApp.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Time to reflect \u2728")
            .setContentText("How was ${start.format(fmt)} \u2014 ${end.format(fmt)}?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(3000 + start.toSecondOfDay(), notification)
        } catch (_: SecurityException) { /* permission not granted */ }
    }
}
