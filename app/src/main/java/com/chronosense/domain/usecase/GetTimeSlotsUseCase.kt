package com.chronosense.domain.usecase

import com.chronosense.core.algorithm.IntervalEngine
import com.chronosense.domain.model.TimeSlot
import com.chronosense.domain.repository.JournalRepository
import com.chronosense.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

/**
 * Produces the ordered list of [TimeSlot]s for a given date.
 *
 * Combines the user's wake/sleep/interval preferences with stored entries,
 * delegating the heavy-lifting to [IntervalEngine.generateSlots].
 *
 * Complexity: O(S + E·log E) per emission, where S = slots, E = entries.
 */
class GetTimeSlotsUseCase(
    private val journalRepo: JournalRepository,
    private val prefsRepo: PreferencesRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<TimeSlot>> =
        combine(
            journalRepo.observeEntriesForDate(date),
            prefsRepo.observePreferences()
        ) { entries, prefs ->
            IntervalEngine.generateSlots(
                wakeTime = prefs.wakeTime,
                sleepTime = prefs.sleepTime,
                intervalMinutes = prefs.intervalMinutes,
                entries = entries
            )
        }
}
