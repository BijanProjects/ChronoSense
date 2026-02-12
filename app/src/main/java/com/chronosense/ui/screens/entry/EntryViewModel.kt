package com.chronosense.ui.screens.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chronosense.domain.model.ActivityTag
import com.chronosense.domain.model.Mood
import com.chronosense.domain.repository.JournalRepository
import com.chronosense.domain.usecase.SaveEntryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class EntryViewModel(
    private val repository: JournalRepository,
    private val saveEntryUseCase: SaveEntryUseCase,
    private val date: LocalDate,
    private val startTime: LocalTime,
    private val endTime: LocalTime
) : ViewModel() {

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _mood = MutableStateFlow<Mood?>(null)
    val mood: StateFlow<Mood?> = _mood.asStateFlow()

    private val _tags = MutableStateFlow<List<ActivityTag>>(emptyList())
    val tags: StateFlow<List<ActivityTag>> = _tags.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var existingEntryId: Long = 0

    init {
        loadExistingEntry()
    }

    private fun loadExistingEntry() {
        viewModelScope.launch {
            val existing = repository.getEntryBySlot(date, startTime)
            if (existing != null) {
                existingEntryId = existing.id
                _description.value = existing.description
                _mood.value = existing.mood
                _tags.value = existing.tags
            }
            _isLoading.value = false
        }
    }

    fun updateDescription(text: String) {
        _description.value = text
    }

    fun updateMood(mood: Mood?) {
        _mood.value = mood
    }

    fun toggleTag(tag: ActivityTag) {
        _tags.value = if (tag in _tags.value) {
            _tags.value - tag
        } else {
            _tags.value + tag
        }
    }

    fun saveEntry() {
        viewModelScope.launch {
            saveEntryUseCase(
                id = existingEntryId,
                date = date,
                startTime = startTime,
                endTime = endTime,
                description = _description.value,
                mood = _mood.value,
                tags = _tags.value
            )
            _isSaved.value = true
        }
    }

    fun deleteEntry() {
        if (existingEntryId != 0L) {
            viewModelScope.launch {
                repository.deleteEntry(existingEntryId)
                _isSaved.value = true
            }
        }
    }

    class Factory(
        private val repository: JournalRepository,
        private val saveEntryUseCase: SaveEntryUseCase,
        private val date: LocalDate,
        private val startTime: LocalTime,
        private val endTime: LocalTime
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EntryViewModel(repository, saveEntryUseCase, date, startTime, endTime) as T
        }
    }
}
