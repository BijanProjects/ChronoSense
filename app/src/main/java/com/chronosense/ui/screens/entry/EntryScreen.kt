package com.chronosense.ui.screens.entry

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chronosense.ChronoSenseApp
import com.chronosense.ui.components.MoodSelector
import com.chronosense.ui.components.TagSelector
import com.chronosense.ui.design.tokens.Radius
import com.chronosense.ui.design.tokens.Spacing
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val timeFmt = DateTimeFormatter.ofPattern("h:mm a")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryScreen(
    date: String,
    startTime: String,
    endTime: String,
    onNavigateBack: () -> Unit,
    viewModel: EntryViewModel = viewModel(
        factory = EntryViewModel.Factory(
            repository = ChronoSenseApp.instance.module.journalRepository,
            saveEntryUseCase = ChronoSenseApp.instance.module.saveEntryUseCase,
            date = runCatching { LocalDate.parse(date) }.getOrElse { LocalDate.now() },
            startTime = runCatching { LocalTime.parse(startTime) }.getOrElse { LocalTime.now() },
            endTime = runCatching { LocalTime.parse(endTime) }.getOrElse { LocalTime.now().plusHours(1) }
        )
    )
) {
    val description by viewModel.description.collectAsStateWithLifecycle()
    val mood by viewModel.mood.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val parsedStart = LocalTime.parse(startTime)
    val parsedEnd = LocalTime.parse(endTime)
    val timeRange = "${parsedStart.format(timeFmt)} \u2014 ${parsedEnd.format(timeFmt)}"

    LaunchedEffect(isSaved) {
        if (isSaved) onNavigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(text = timeRange, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = LocalDate.parse(date).toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            actions = {
                IconButton(onClick = { viewModel.deleteEntry() }) {
                    Icon(Icons.Default.Delete, "Delete entry", tint = MaterialTheme.colorScheme.error)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.xl)
            ) {
                Spacer(modifier = Modifier.height(Spacing.sm))

                Text(
                    text = "What happened during this time?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                OutlinedTextField(
                    value = description,
                    onValueChange = { viewModel.updateDescription(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp),
                    placeholder = {
                        Text(
                            text = "Describe how you spent this time and how you felt...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    shape = RoundedCornerShape(Radius.lg),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(Spacing.xxl))

                MoodSelector(
                    selectedMood = mood,
                    onMoodSelected = { viewModel.updateMood(it) }
                )

                Spacer(modifier = Modifier.height(Spacing.xxl))

                TagSelector(
                    selectedTags = tags,
                    onTagToggled = { viewModel.toggleTag(it) }
                )

                Spacer(modifier = Modifier.height(Spacing.xxxl))
            }

            // Save button pinned to bottom
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                Button(
                    onClick = { viewModel.saveEntry() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.xl, vertical = Spacing.lg)
                        .navigationBarsPadding()
                        .height(54.dp),
                    shape = RoundedCornerShape(Radius.lg),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Save Entry",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
