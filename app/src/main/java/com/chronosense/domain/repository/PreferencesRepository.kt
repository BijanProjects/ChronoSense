package com.chronosense.domain.repository

import com.chronosense.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Contract for user preferences storage.
 *
 * Observations emit immediately with the current value, then again on any change.
 * Mutations are atomic via the [update] transform pattern.
 */
interface PreferencesRepository {
    fun observePreferences(): Flow<UserPreferences>
    suspend fun update(transform: (UserPreferences) -> UserPreferences)
}
