package com.chronosense.core.di

import android.content.Context
import com.chronosense.core.analytics.AnalyticsTracker
import com.chronosense.core.analytics.NoOpTracker
import com.chronosense.data.cache.DayCache
import com.chronosense.data.local.AppDatabase
import com.chronosense.data.preferences.UserPreferencesStore
import com.chronosense.data.repository.JournalRepositoryImpl
import com.chronosense.data.repository.PreferencesRepositoryImpl
import com.chronosense.domain.repository.JournalRepository
import com.chronosense.domain.repository.PreferencesRepository
import com.chronosense.domain.usecase.GetMonthInsightsUseCase
import com.chronosense.domain.usecase.GetTimeSlotsUseCase
import com.chronosense.domain.usecase.SaveEntryUseCase
import com.chronosense.notification.NotificationScheduler

/**
 * Manual dependency injection container.
 *
 * Organised with **lazy** delegates so nothing is initialised until first access,
 * keeping Application.onCreate() fast (~20 ms target).
 *
 * ### KMP Migration Path
 * Replace with Koin modules (`module { singleOf(::JournalRepositoryImpl) bind JournalRepository::class }`)
 * or kotlin-inject. The call-sites stay identical thanks to interface-based wiring.
 */
class AppModule(private val context: Context) {

    // ── Data ──────────────────────────────────────────────
    val database: AppDatabase by lazy { AppDatabase.getInstance(context) }
    val preferencesStore: UserPreferencesStore by lazy { UserPreferencesStore(context) }
    val dayCache: DayCache by lazy { DayCache(maxSize = 14) }

    // ── Repositories ─────────────────────────────────────
    val journalRepository: JournalRepository by lazy {
        JournalRepositoryImpl(database.journalDao(), dayCache)
    }
    val preferencesRepository: PreferencesRepository by lazy {
        PreferencesRepositoryImpl(preferencesStore)
    }

    // ── Use Cases ────────────────────────────────────────
    val getTimeSlotsUseCase: GetTimeSlotsUseCase by lazy {
        GetTimeSlotsUseCase(journalRepository, preferencesRepository)
    }
    val saveEntryUseCase: SaveEntryUseCase by lazy {
        SaveEntryUseCase(journalRepository)
    }
    val getMonthInsightsUseCase: GetMonthInsightsUseCase by lazy {
        GetMonthInsightsUseCase(journalRepository, preferencesRepository)
    }

    // ── Platform Services ────────────────────────────────
    val notificationScheduler: NotificationScheduler by lazy {
        NotificationScheduler(context)
    }
    val analytics: AnalyticsTracker by lazy {
        NoOpTracker()  // Swap for FirebaseTracker / AmplitudeTracker in release builds
    }
}
