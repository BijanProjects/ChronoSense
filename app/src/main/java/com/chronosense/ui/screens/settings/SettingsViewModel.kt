package com.chronosense.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chronosense.domain.model.UserPreferences
import com.chronosense.domain.repository.PreferencesRepository
import com.chronosense.notification.NotificationScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesRepository
        .observePreferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    fun setWakeTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesRepository.update { it.copy(wakeTime = LocalTime.of(hour, minute)) }
            rescheduleNotifications()
        }
    }

    fun setSleepTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesRepository.update { it.copy(sleepTime = LocalTime.of(hour, minute)) }
            rescheduleNotifications()
        }
    }

    fun setIntervalMinutes(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.update { it.copy(intervalMinutes = minutes) }
            rescheduleNotifications()
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.update { it.copy(notificationsEnabled = enabled) }
            if (enabled) rescheduleNotifications() else notificationScheduler.cancelAll()
        }
    }

    private suspend fun rescheduleNotifications() {
        val prefs = preferences.value
        if (prefs.notificationsEnabled) {
            notificationScheduler.scheduleForToday(prefs.wakeTime, prefs.sleepTime, prefs.intervalMinutes)
        }
    }

    class Factory(
        private val preferencesRepository: PreferencesRepository,
        private val notificationScheduler: NotificationScheduler
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(preferencesRepository, notificationScheduler) as T
        }
    }
}
