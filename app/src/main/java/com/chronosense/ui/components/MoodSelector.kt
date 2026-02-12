package com.chronosense.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chronosense.domain.model.Mood
import com.chronosense.ui.design.tokens.Radius
import com.chronosense.ui.design.tokens.Spacing
import com.chronosense.ui.theme.getMoodColor

@Composable
fun MoodSelector(
    selectedMood: Mood?,
    onMoodSelected: (Mood?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "How did you feel?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Mood.entries.sortedBy { it.sortOrder }.forEach { mood ->
                val isSelected = selectedMood == mood
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1f,
                    animationSpec = spring(dampingRatio = 0.5f),
                    label = "mood_scale_${mood.name}"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) getMoodColor(mood)
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    label = "mood_border_${mood.name}"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .scale(scale)
                        .semantics { role = Role.RadioButton }
                        .clickable { onMoodSelected(if (isSelected) null else mood) }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(Radius.md)
                        )
                        .background(
                            color = if (isSelected) getMoodColor(mood).copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(Radius.md)
                        )
                        .padding(horizontal = 10.dp, vertical = Spacing.sm)
                ) {
                    Text(text = mood.emoji, fontSize = 24.sp)
                    Spacer(Modifier.height(Spacing.xxs))
                    Text(
                        text = mood.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) getMoodColor(mood)
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
