package com.chronosense.ui.screens.day

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chronosense.core.algorithm.IntervalEngine
import com.chronosense.domain.model.DaySummary
import com.chronosense.domain.model.TimeSlot
import com.chronosense.domain.usecase.GetTimeSlotsUseCase
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class DayUiState(
    val formattedDate: String = "Today",
    val timeSlots: List<TimeSlot> = emptyList(),
    val completionRate: Float = 0f,
    val filledCount: Int = 0,
    val totalCount: Int = 0,
    val activeSlotIndex: Int = -1,
    val isToday: Boolean = true
)

class DayViewModel(
    private val getTimeSlotsUseCase: GetTimeSlotsUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val uiState: StateFlow<DayUiState> = _selectedDate
        .flatMapLatest { date ->
            getTimeSlotsUseCase(date).map { slots ->
                val summary = DaySummary.from(date, slots)
                val isToday = date == LocalDate.now()
                DayUiState(
                    formattedDate = formatDate(date),
                    timeSlots = slots,
                    completionRate = summary.completionRate,
                    filledCount = summary.filledSlots,
                    totalCount = summary.totalSlots,
                    activeSlotIndex = if (isToday && slots.isNotEmpty()) {
                        IntervalEngine.findActiveSlotIndex(
                            slots.first().startTime,
                            slots.first().durationMinutes,
                            LocalTime.now()
                        )
                    } else -1,
                    isToday = isToday
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DayUiState())

    fun goToPreviousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    fun goToNextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
    }

    fun goToToday() {
        _selectedDate.value = LocalDate.now()
    }

    private fun formatDate(date: LocalDate): String {
        val today = LocalDate.now()
        return when (date) {
            today -> "Today"
            today.minusDays(1) -> "Yesterday"
            today.plusDays(1) -> "Tomorrow"
            else -> date.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
        }
    }

    class Factory(
        private val getTimeSlotsUseCase: GetTimeSlotsUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DayViewModel(getTimeSlotsUseCase) as T
        }
    }
}
