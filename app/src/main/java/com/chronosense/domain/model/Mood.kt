package com.chronosense.domain.model

/**
 * Mood taxonomy for journal entries.
 *
 * Each mood carries display metadata (emoji, label) and a stable [colorHex]
 * used across the design system. The [sortOrder] controls presentation
 * sequence—high-energy moods first, low-energy last.
 *
 * Cross-platform note: enum values are serialised by [name] in the data layer.
 * Keep names stable across releases to avoid migration issues.
 */
enum class Mood(
    val emoji: String,
    val label: String,
    val colorHex: Long,
    val sortOrder: Int
) {
    ENERGETIC("🔥", "Energetic", 0xFFF59E0B, 0),
    HAPPY("😊", "Happy", 0xFF10B981, 1),
    FOCUSED("🎯", "Focused", 0xFF6366F1, 2),
    CALM("😌", "Calm", 0xFF14B8A6, 3),
    NEUTRAL("😐", "Neutral", 0xFF8B5CF6, 4),
    TIRED("😴", "Tired", 0xFF94A3B8, 5),
    STRESSED("😰", "Stressed", 0xFFF43F5E, 6);

    companion object {
        private val byEmoji = entries.associateBy { it.emoji }
        private val byName = entries.associateBy { it.name.lowercase() }

        fun fromEmoji(emoji: String): Mood? = byEmoji[emoji]
        fun fromName(name: String): Mood? = byName[name.lowercase()]
    }
}
