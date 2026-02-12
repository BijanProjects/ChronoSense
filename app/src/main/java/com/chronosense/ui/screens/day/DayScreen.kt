package com.chronosense.ui.screens.day

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chronosense.ChronoSenseApp
import com.chronosense.ui.components.TimeSlotCard
import com.chronosense.ui.design.tokens.Spacing
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DayScreen(
    onTimeSlotClick: (LocalDate, LocalTime, LocalTime) -> Unit,
    viewModel: DayViewModel = viewModel(
        factory = DayViewModel.Factory(ChronoSenseApp.instance.module.getTimeSlotsUseCase)
    )
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Auto-scroll to active slot when viewing today
    LaunchedEffect(uiState.activeSlotIndex) {
        if (uiState.activeSlotIndex > 0 && uiState.isToday) {
            listState.animateScrollToItem(uiState.activeSlotIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.xl)
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = "ChronoSense",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Date navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.goToPreviousDay() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous day",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = uiState.formattedDate,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${(uiState.completionRate * 100).toInt()}% logged",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { viewModel.goToNextDay() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next day",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        LinearProgressIndicator(
            progress = { uiState.completionRate },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        if (uiState.timeSlots.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "⏰", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = "No time slots for this day",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Set up your waking hours in Settings",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = Spacing.lg),
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = uiState.timeSlots,
                    key = { it.startTime.toString() }
                ) { slot ->
                    TimeSlotCard(
                        timeSlot = slot,
                        onClick = {
                            onTimeSlotClick(selectedDate, slot.startTime, slot.endTime)
                        },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}
