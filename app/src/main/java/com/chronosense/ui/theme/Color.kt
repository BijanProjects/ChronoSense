package com.chronosense.ui.theme

import androidx.compose.ui.graphics.Color
import com.chronosense.domain.model.Mood

// ── Primary — Indigo ──────────────────────────────────────
val Indigo50 = Color(0xFFEEF2FF)
val Indigo100 = Color(0xFFE0E7FF)
val Indigo200 = Color(0xFFC7D2FE)
val Indigo300 = Color(0xFFA5B4FC)
val Indigo400 = Color(0xFF818CF8)
val Indigo500 = Color(0xFF6366F1)
val Indigo600 = Color(0xFF4F46E5)
val Indigo700 = Color(0xFF4338CA)
val Indigo800 = Color(0xFF3730A3)
val Indigo900 = Color(0xFF312E81)

// ── Accent — Amber ────────────────────────────────────────
val Amber300 = Color(0xFFFCD34D)
val Amber400 = Color(0xFFFBBF24)
val Amber500 = Color(0xFFF59E0B)

// ── Neutrals — Slate ──────────────────────────────────────
val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate300 = Color(0xFFCBD5E1)
val Slate400 = Color(0xFF94A3B8)
val Slate500 = Color(0xFF64748B)
val Slate600 = Color(0xFF475569)
val Slate700 = Color(0xFF334155)
val Slate800 = Color(0xFF1E293B)
val Slate900 = Color(0xFF0F172A)

// ── Dark Surfaces ─────────────────────────────────────────
val DarkSurface = Color(0xFF141225)
val DarkBackground = Color(0xFF0D0B1A)
val DarkCard = Color(0xFF1C1A30)

// ── Mood Colors (derived from Mood.colorHex) ──────────────
fun getMoodColor(mood: Mood?): Color =
    mood?.let { Color(it.colorHex) } ?: Indigo400

/** String-based lookup for backward compat (e.g. notification deep-links). */
fun getMoodColorByEmoji(emoji: String): Color =
    Mood.fromEmoji(emoji)?.let { Color(it.colorHex) } ?: Indigo400
