package com.chronosense.domain.usecase

import com.chronosense.domain.model.ActivityTag
import com.chronosense.domain.model.JournalEntry
import com.chronosense.domain.model.Mood
import com.chronosense.domain.repository.JournalRepository
import java.time.LocalDate
import java.time.LocalTime

/**
 * Validates and persists a journal entry. Returns the persisted entry id.
 *
 * - Trims whitespace from description.
 * - Preserves original [createdAt] on updates.
 */
class SaveEntryUseCase(
    private val journalRepo: JournalRepository
) {
    suspend operator fun invoke(
        id: Long = 0,
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime,
        description: String,
        mood: Mood?,
        tags: List<ActivityTag>
    ): Result<Long> = runCatching {
        val existing = if (id != 0L) journalRepo.getEntryById(id) else null
        val now = System.currentTimeMillis()

        val entry = JournalEntry(
            id = id,
            date = date,
            startTime = startTime,
            endTime = endTime,
            description = description.trim(),
            mood = mood,
            tags = tags,
            createdAt = existing?.createdAt ?: now
        )
        journalRepo.upsertEntry(entry)
    }
}
