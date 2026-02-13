import 'package:flutter/material.dart';
import 'package:chronosense/domain/model/models.dart';
import 'package:chronosense/ui/design/tokens.dart';

/// Horizontal scrollable row of 10 FilterChips — multi-select toggle.
class TagSelector extends StatelessWidget {
  final List<ActivityTag> selected;
  final ValueChanged<List<ActivityTag>> onChanged;

  const TagSelector({
    super.key,
    required this.selected,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 44,
      child: ListView.separated(
        scrollDirection: Axis.horizontal,
        padding: const EdgeInsets.symmetric(horizontal: Spacing.xs),
        itemCount: ActivityTag.values.length,
        separatorBuilder: (_, __) => const SizedBox(width: Spacing.sm),
        itemBuilder: (context, index) {
          final tag = ActivityTag.values[index];
          final isSelected = selected.contains(tag);
          final tagColor = Color(tag.colorHex);

          return FilterChip(
            label: Text('${tag.icon} ${tag.label}'),
            selected: isSelected,
            onSelected: (value) {
              final newList = List<ActivityTag>.from(selected);
              if (value) {
                newList.add(tag);
              } else {
                newList.remove(tag);
              }
              onChanged(newList);
            },
            selectedColor: tagColor.withValues(alpha: 0.15),
            checkmarkColor: tagColor,
            side: BorderSide(
              color: isSelected ? tagColor : Theme.of(context).colorScheme.outlineVariant,
              width: isSelected ? 1.5 : 1,
            ),
            labelStyle: Theme.of(context).textTheme.labelMedium?.copyWith(
                  color: isSelected
                      ? tagColor
                      : Theme.of(context).colorScheme.onSurfaceVariant,
                  fontWeight: isSelected ? FontWeight.w600 : FontWeight.w500,
                ),
            showCheckmark: false,
            materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
            visualDensity: VisualDensity.compact,
            padding: const EdgeInsets.symmetric(horizontal: Spacing.sm),
          );
        },
      ),
    );
  }
}
