package com.chronosense.data.mapper

import com.chronosense.data.model.JournalEntryEntity
import com.chronosense.domain.model.ActivityTag
import com.chronosense.domain.model.JournalEntry
import com.chronosense.domain.model.Mood
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Maps between Room entities and domain models.
 *
 * This boundary ensures the domain layer stays free of Room/Android imports,
 * which is critical for KMP migration.
 */
object EntityMapper {

    private val dateFmt: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timeFmt: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    fun toDomain(entity: JournalEntryEntity): JournalEntry = JournalEntry(
        id = entity.id,
        date = LocalDate.parse(entity.date, dateFmt),
        startTime = LocalTime.parse(entity.startTime, timeFmt),
        endTime = LocalTime.parse(entity.endTime, timeFmt),
        description = entity.description,
        mood = Mood.fromName(entity.mood),
        tags = entity.tags
            .split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { ActivityTag.fromLabel(it.trim()) },
        createdAt = entity.createdAt
    )

    fun toEntity(domain: JournalEntry): JournalEntryEntity = JournalEntryEntity(
        id = domain.id,
        date = domain.date.format(dateFmt),
        startTime = domain.startTime.format(timeFmt),
        endTime = domain.endTime.format(timeFmt),
        description = domain.description,
        mood = domain.mood?.name ?: "",
        tags = domain.tags.joinToString(",") { it.label },
        createdAt = domain.createdAt
    )

    fun toDomainList(entities: List<JournalEntryEntity>): List<JournalEntry> =
        entities.map(::toDomain)
}
