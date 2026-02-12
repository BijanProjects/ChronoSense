package com.chronosense.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chronosense.ChronoSenseApp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

/**
 * Re-schedules interval alarms after device reboot.
 *
 * Uses [goAsync] to extend the BroadcastReceiver window
 * while the coroutine reads preferences from DataStore.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val module = (context.applicationContext as ChronoSenseApp).module
        val pending = goAsync()

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val prefs = module.preferencesRepository.observePreferences().first()
                if (prefs.notificationsEnabled) {
                    module.notificationScheduler.scheduleForToday(
                        prefs.wakeTime, prefs.sleepTime, prefs.intervalMinutes
                    )
                }
            } finally {
                pending.finish()
            }
        }
    }
}
