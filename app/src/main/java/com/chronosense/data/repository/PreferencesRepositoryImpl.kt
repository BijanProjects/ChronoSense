package com.chronosense.data.repository

import com.chronosense.data.preferences.UserPreferencesStore
import com.chronosense.domain.model.UserPreferences
import com.chronosense.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

/**
 * DataStore-backed implementation of [PreferencesRepository].
 */
class PreferencesRepositoryImpl(
    private val store: UserPreferencesStore
) : PreferencesRepository {

    override fun observePreferences(): Flow<UserPreferences> = store.preferences

    override suspend fun update(transform: (UserPreferences) -> UserPreferences) {
        store.update(transform)
    }
}
