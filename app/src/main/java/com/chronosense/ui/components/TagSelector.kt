package com.chronosense.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chronosense.domain.model.ActivityTag
import com.chronosense.ui.design.tokens.Radius
import com.chronosense.ui.design.tokens.Spacing

@Composable
fun TagSelector(
    selectedTags: List<ActivityTag>,
    onTagToggled: (ActivityTag) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "What were you doing?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(Spacing.md))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(ActivityTag.entries.toList()) { tag ->
                val isSelected = tag in selectedTags
                FilterChip(
                    selected = isSelected,
                    onClick = { onTagToggled(tag) },
                    label = {
                        Text(
                            text = "${tag.icon} ${tag.label}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    shape = RoundedCornerShape(Radius.md),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}
