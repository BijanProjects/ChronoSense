package com.chronosense

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.chronosense.core.di.AppModule

/**
 * Application entry point — owns the [AppModule] DI container.
 *
 * Startup is kept lean: only the notification channel is created eagerly.
 * All other dependencies are lazy-initialised on first access.
 */
class ChronoSenseApp : Application() {

    /** Lazy DI container — first access triggers construction. */
    val module: AppModule by lazy { AppModule(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "ChronoSense Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders to log your time intervals"
            enableVibration(true)
        }
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "chronosense_reminders"
        lateinit var instance: ChronoSenseApp
            private set
    }
}
