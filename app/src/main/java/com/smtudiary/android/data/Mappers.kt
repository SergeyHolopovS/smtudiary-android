package com.smtudiary.android.data

import com.smtudiary.android.data.local.LessonEntity
import com.smtudiary.android.data.model.Lesson
import com.smtudiary.android.data.remote.LessonDto
import java.util.UUID

fun LessonDto.toEntity() = LessonEntity(
    stableId(), name, time, teacher, week,
    room, group, day
)

// The API sends no ids; deriving one from the content keeps refreshes upserting the same rows.
private fun LessonDto.stableId(): UUID = UUID.nameUUIDFromBytes(
    listOf(group, day, time, week, name, room, teacher).joinToString("|").toByteArray()
)

fun LessonEntity.toDomain() = Lesson(
    id, name, time, teacher, week,
    room, group, day
)