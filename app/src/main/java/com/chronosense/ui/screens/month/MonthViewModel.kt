package com.chronosense.ui.screens.month

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chronosense.domain.model.MonthInsight
import com.chronosense.domain.usecase.GetMonthInsightsUseCase
import kotlinx.coroutines.flow.*
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class MonthViewModel(
    private val getMonthInsightsUseCase: GetMonthInsightsUseCase
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    val formattedMonth: StateFlow<String> = _selectedMonth.map { month ->
        month.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val insight: StateFlow<MonthInsight?> = _selectedMonth
        .flatMapLatest { month -> getMonthInsightsUseCase(month) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun goToPreviousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
    }

    fun goToNextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
    }

    class Factory(
        private val getMonthInsightsUseCase: GetMonthInsightsUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MonthViewModel(getMonthInsightsUseCase) as T
        }
    }
}
