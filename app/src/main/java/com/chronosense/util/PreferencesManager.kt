package com.chronosense.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chronosense_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val WAKE_HOUR = intPreferencesKey("wake_hour")
        val WAKE_MINUTE = intPreferencesKey("wake_minute")
        val SLEEP_HOUR = intPreferencesKey("sleep_hour")
        val SLEEP_MINUTE = intPreferencesKey("sleep_minute")
        val INTERVAL_MINUTES = intPreferencesKey("interval_minutes")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val wakeTime: Flow<LocalTime> = context.dataStore.data.map { prefs ->
        LocalTime.of(
            prefs[WAKE_HOUR] ?: 7,
            prefs[WAKE_MINUTE] ?: 0
        )
    }

    val sleepTime: Flow<LocalTime> = context.dataStore.data.map { prefs ->
        LocalTime.of(
            prefs[SLEEP_HOUR] ?: 23,
            prefs[SLEEP_MINUTE] ?: 0
        )
    }

    val intervalMinutes: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[INTERVAL_MINUTES] ?: 120
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED] ?: true
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE] ?: false
    }

    suspend fun setWakeTime(hour: Int, minute: Int) {
        context.dataStore.edit { prefs ->
            prefs[WAKE_HOUR] = hour
            prefs[WAKE_MINUTE] = minute
        }
    }

    suspend fun setSleepTime(hour: Int, minute: Int) {
        context.dataStore.edit { prefs ->
            prefs[SLEEP_HOUR] = hour
            prefs[SLEEP_MINUTE] = minute
        }
    }

    suspend fun setIntervalMinutes(minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[INTERVAL_MINUTES] = minutes
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE] = enabled
        }
    }
}
