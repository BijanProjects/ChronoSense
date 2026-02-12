package com.chronosense.domain.usecase

import com.chronosense.domain.model.MonthInsight
import com.chronosense.domain.repository.JournalRepository
import com.chronosense.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth

/**
 * Produces a [MonthInsight] for the given [YearMonth].
 *
 * Aggregation is O(n) single-pass, executed on each emission from either
 * the entries flow or the preferences flow.
 */
class GetMonthInsightsUseCase(
    private val journalRepo: JournalRepository,
    private val prefsRepo: PreferencesRepository
) {
    operator fun invoke(yearMonth: YearMonth): Flow<MonthInsight> {
        val start = yearMonth.atDay(1)
        val end = yearMonth.atEndOfMonth()

        return combine(
            journalRepo.observeEntriesForDateRange(start, end),
            prefsRepo.observePreferences()
        ) { entries, prefs ->
            MonthInsight.aggregate(
                yearMonth = yearMonth,
                entries = entries,
                slotsPerDay = prefs.totalSlots
            )
        }
    }
}
