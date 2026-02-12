package com.chronosense.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.chronosense.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("chrono_prefs")

/**
 * DataStore-backed persistence for [UserPreferences].
 *
 * All keys are primitive types (int, boolean) for efficient proto serialisation.
 * The [update] method uses read-modify-write semantics with automatic conflict resolution.
 */
class UserPreferencesStore(private val context: Context) {

    private object K {
        val WAKE_H = intPreferencesKey("wake_hour")
        val WAKE_M = intPreferencesKey("wake_minute")
        val SLEEP_H = intPreferencesKey("sleep_hour")
        val SLEEP_M = intPreferencesKey("sleep_minute")
        val INTERVAL = intPreferencesKey("interval_minutes")
        val NOTIF = booleanPreferencesKey("notifications_enabled")
        val DYNAMIC = booleanPreferencesKey("dynamic_colors")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { p ->
        UserPreferences(
            wakeTime = LocalTime.of(p[K.WAKE_H] ?: 7, p[K.WAKE_M] ?: 0),
            sleepTime = LocalTime.of(p[K.SLEEP_H] ?: 23, p[K.SLEEP_M] ?: 0),
            intervalMinutes = p[K.INTERVAL] ?: 120,
            notificationsEnabled = p[K.NOTIF] ?: true,
            dynamicColors = p[K.DYNAMIC] ?: false
        )
    }

    suspend fun update(transform: (UserPreferences) -> UserPreferences) {
        context.dataStore.edit { p ->
            val current = UserPreferences(
                wakeTime = LocalTime.of(p[K.WAKE_H] ?: 7, p[K.WAKE_M] ?: 0),
                sleepTime = LocalTime.of(p[K.SLEEP_H] ?: 23, p[K.SLEEP_M] ?: 0),
                intervalMinutes = p[K.INTERVAL] ?: 120,
                notificationsEnabled = p[K.NOTIF] ?: true,
                dynamicColors = p[K.DYNAMIC] ?: false
            )
            val updated = transform(current)
            p[K.WAKE_H] = updated.wakeTime.hour
            p[K.WAKE_M] = updated.wakeTime.minute
            p[K.SLEEP_H] = updated.sleepTime.hour
            p[K.SLEEP_M] = updated.sleepTime.minute
            p[K.INTERVAL] = updated.intervalMinutes
            p[K.NOTIF] = updated.notificationsEnabled
            p[K.DYNAMIC] = updated.dynamicColors
        }
    }
}
