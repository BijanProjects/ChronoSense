package com.chronosense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chronosense.domain.model.TimeSlot
import com.chronosense.ui.design.tokens.Radius
import com.chronosense.ui.design.tokens.Spacing
import com.chronosense.ui.theme.getMoodColor
import java.time.format.DateTimeFormatter

private val timeFmt = DateTimeFormatter.ofPattern("h:mm a")

@Composable
fun TimeSlotCard(
    timeSlot: TimeSlot,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasEntry = timeSlot.isFilled
    val moodColor = if (hasEntry) {
        getMoodColor(timeSlot.entry?.mood)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }
    val timeLabel = "${timeSlot.startTime.format(timeFmt)} \u2014 ${timeSlot.endTime.format(timeFmt)}"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics { contentDescription = if (hasEntry) "Entry: $timeLabel" else "Empty slot: $timeLabel" },
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(
            containerColor = if (hasEntry) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (hasEntry) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mood accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(if (hasEntry) 90.dp else 72.dp)
                    .clip(RoundedCornerShape(topStart = Radius.lg, bottomStart = Radius.lg))
                    .background(moodColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(Spacing.lg)
            ) {
                Text(
                    text = timeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(Spacing.xs))

                if (hasEntry) {
                    val entry = timeSlot.entry!!
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        entry.mood?.let { mood ->
                            Text(text = mood.emoji, style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.width(Spacing.sm))
                        }
                        Text(
                            text = entry.description.ifBlank { "No description" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (entry.tags.isNotEmpty()) {
                        Spacer(Modifier.height(Spacing.xs + Spacing.xxs))
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            entry.tags.take(3).forEach { tag ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(tag.label, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.height(24.dp),
                                    shape = RoundedCornerShape(Radius.sm)
                                )
                            }
                            if (entry.tags.size > 3) {
                                Text(
                                    "+${entry.tags.size - 3}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = Spacing.xs)
                    ) {
                        Icon(
                            Icons.Default.Add, "Add",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(Spacing.xs + Spacing.xxs))
                        Text(
                            "Tap to record this interval",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
