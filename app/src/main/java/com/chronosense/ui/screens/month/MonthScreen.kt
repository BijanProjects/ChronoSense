package com.chronosense.ui.screens.month

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chronosense.ChronoSenseApp
import com.chronosense.domain.model.ActivityTag
import com.chronosense.domain.model.Mood
import com.chronosense.ui.design.tokens.Radius
import com.chronosense.ui.design.tokens.Spacing
import com.chronosense.ui.theme.getMoodColor
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthScreen(
    onDayClick: (LocalDate) -> Unit,
    viewModel: MonthViewModel = viewModel(
        factory = MonthViewModel.Factory(ChronoSenseApp.instance.module.getMonthInsightsUseCase)
    )
) {
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val formattedMonth by viewModel.formattedMonth.collectAsStateWithLifecycle()
    val insight by viewModel.insight.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.xl)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = Spacing.lg)
    ) {
        item {
            Spacer(modifier = Modifier.height(Spacing.lg))

            Text(
                text = "Monthly Overview",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Month navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.goToPreviousMonth() }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Previous month")
                }
                Text(
                    text = formattedMonth,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { viewModel.goToNextMonth() }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next month")
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Calendar grid
            CalendarGrid(
                yearMonth = selectedMonth,
                daysWithEntries = insight?.daysWithEntries ?: emptySet(),
                onDayClick = { day ->
                    onDayClick(LocalDate.of(selectedMonth.year, selectedMonth.month, day))
                }
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            // Summary stats card
            if (insight != null) {
                val data = insight!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Radius.lg),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.xl),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(value = "${data.totalEntries}", label = "Entries")
                        StatItem(value = "${data.activeDays}", label = "Active Days")
                        StatItem(
                            value = data.topActivity?.label ?: "\u2014",
                            label = "Top Activity"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.xxl))
            }
        }

        // Mood distribution section
        if (insight != null && insight!!.moodFrequency.isNotEmpty()) {
            item {
                Text(
                    text = "Mood Distribution",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    val totalMoods = insight!!.moodFrequency.values.sum()
                    insight!!.moodFrequency.forEach { (mood, count) ->
                        val fraction = count.toFloat() / totalMoods
                        Card(
                            modifier = Modifier.weight(fraction.coerceAtLeast(0.1f)),
                            shape = RoundedCornerShape(Radius.md),
                            colors = CardDefaults.cardColors(
                                containerColor = getMoodColor(mood).copy(alpha = 0.15f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(Spacing.md),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = mood.emoji, style = MaterialTheme.typography.titleLarge)
                                Text(
                                    text = "$count",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xxl))
            }
        }

        // Top activities section
        if (insight != null && insight!!.tagFrequency.isNotEmpty()) {
            item {
                Text(
                    text = "Most Repeated Activities",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            val tagEntries = insight!!.tagFrequency.entries.take(8).toList()
            val maxCount = tagEntries.maxOfOrNull { it.value } ?: 1

            items(tagEntries) { (tag, count) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${tag.icon} ${tag.label}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(120.dp)
                    )

                    Spacer(modifier = Modifier.width(Spacing.md))

                    Box(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp)
                                .clip(RoundedCornerShape(Radius.sm))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(count.toFloat() / maxCount)
                                .height(28.dp)
                                .clip(RoundedCornerShape(Radius.sm))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                        )
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = Spacing.sm)
                        )
                    }
                }
            }
        }

        // Empty state
        if (insight == null || (insight!!.tagFrequency.isEmpty() && insight!!.totalEntries == 0)) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.huge),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "\uD83D\uDCCA", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = "No entries this month yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Start logging your time to see insights here",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    daysWithEntries: Set<Int>,
    onDayClick: (Int) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value // Monday = 1
    val today = LocalDate.now()

    Column {
        // Day-of-week headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Calendar days
        var dayCounter = 1
        val offset = firstDayOfWeek - 1

        for (week in 0..5) {
            if (dayCounter > daysInMonth) break

            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0..6) {
                    val cellIndex = week * 7 + dayOfWeek

                    if (cellIndex < offset || dayCounter > daysInMonth) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    } else {
                        val day = dayCounter
                        val hasEntry = day in daysWithEntries
                        val isToday = yearMonth.year == today.year &&
                                yearMonth.monthValue == today.monthValue &&
                                day == today.dayOfMonth

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isToday -> MaterialTheme.colorScheme.primary
                                        hasEntry -> MaterialTheme.colorScheme.primaryContainer
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                )
                                .clickable { onDayClick(day) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$day",
                                style = MaterialTheme.typography.bodySmall,
                                color = when {
                                    isToday -> MaterialTheme.colorScheme.onPrimary
                                    hasEntry -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
