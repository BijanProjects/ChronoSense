package com.chronosense.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chronosense.ChronoSenseApp
import com.chronosense.domain.model.UserPreferences
import com.chronosense.ui.design.tokens.Radius
import com.chronosense.ui.design.tokens.Spacing
import java.time.format.DateTimeFormatter

private val timeFmt = DateTimeFormatter.ofPattern("h:mm a")

private fun formatIntervalLabel(minutes: Int): String = when {
    minutes < 60 -> "${minutes}m"
    minutes % 60 == 0 -> "${minutes / 60}h"
    else -> "${minutes / 60}h ${minutes % 60}m"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(
            ChronoSenseApp.instance.module.preferencesRepository,
            ChronoSenseApp.instance.module.notificationScheduler
        )
    )
) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()

    var showWakeTimePicker by remember { mutableStateOf(false) }
    var showSleepTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(28.dp))

        // \u2500\u2500 Schedule Section \u2500\u2500
        Text(
            text = "SCHEDULE",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        // Wake Time
        SettingsCard(
            icon = Icons.Default.WbSunny,
            title = "Wake Time",
            subtitle = prefs.wakeTime.format(timeFmt),
            onClick = { showWakeTimePicker = true }
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Sleep Time
        SettingsCard(
            icon = Icons.Default.Bedtime,
            title = "Sleep Time",
            subtitle = prefs.sleepTime.format(timeFmt),
            onClick = { showSleepTimePicker = true }
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Interval selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Radius.lg),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Text(
                        text = "Check-in Interval",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    UserPreferences.INTERVAL_OPTIONS.forEach { minutes ->
                        val isSelected = prefs.intervalMinutes == minutes
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setIntervalMinutes(minutes) },
                            label = {
                                Text(
                                    text = formatIntervalLabel(minutes),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // \u2500\u2500 Notifications Section \u2500\u2500
        Text(
            text = "NOTIFICATIONS",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Radius.lg),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.md))
                    Column {
                        Text(
                            text = "Interval Reminders",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Get notified at each interval",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = prefs.notificationsEnabled,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // \u2500\u2500 About Section \u2500\u2500
        Text(
            text = "ABOUT",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Radius.lg),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                Text(
                    text = "ChronoSense",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Version 1.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = "Understand your time. Reflect on your hours. Live intentionally.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xxxl))
    }

    // \u2500\u2500 Time Picker Dialogs \u2500\u2500
    if (showWakeTimePicker) {
        TimePickerDialog(
            initialHour = prefs.wakeTime.hour,
            initialMinute = prefs.wakeTime.minute,
            title = "Set Wake Time",
            onConfirm = { hour, minute ->
                viewModel.setWakeTime(hour, minute)
                showWakeTimePicker = false
            },
            onDismiss = { showWakeTimePicker = false }
        )
    }

    if (showSleepTimePicker) {
        TimePickerDialog(
            initialHour = prefs.sleepTime.hour,
            initialMinute = prefs.sleepTime.minute,
            title = "Set Sleep Time",
            onConfirm = { hour, minute ->
                viewModel.setSleepTime(hour, minute)
                showSleepTimePicker = false
            },
            onDismiss = { showSleepTimePicker = false }
        )
    }
}

@Composable
fun SettingsCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.md))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    title: String,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
