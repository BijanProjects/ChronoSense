package com.chronosense.domain.model

/**
 * Predefined activity categories for tagging journal entries.
 *
 * Each tag carries an [icon] emoji for compact display and a [colorHex]
 * for chart visualisations. Tags are serialised by [label] in the data layer.
 */
enum class ActivityTag(
    val label: String,
    val icon: String,
    val colorHex: Long
) {
    WORK("Work", "💼", 0xFF3B82F6),
    EXERCISE("Exercise", "🏃", 0xFF10B981),
    SOCIAL("Social", "👥", 0xFFF59E0B),
    CREATIVE("Creative", "🎨", 0xFFA855F7),
    REST("Rest", "🛋️", 0xFF64748B),
    LEARNING("Learning", "📚", 0xFF6366F1),
    COMMUTE("Commute", "🚗", 0xFF78716C),
    MEALS("Meals", "🍽️", 0xFFEF4444),
    ENTERTAINMENT("Entertainment", "🎮", 0xFFEC4899),
    SELF_CARE("Self-care", "🧘", 0xFF14B8A6);

    companion object {
        private val byLabel = entries.associateBy { it.label.lowercase() }
        fun fromLabel(label: String): ActivityTag? = byLabel[label.lowercase()]
    }
}
