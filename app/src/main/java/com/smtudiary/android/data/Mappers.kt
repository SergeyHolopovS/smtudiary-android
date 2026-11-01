package com.smtudiary.android.data

import com.smtudiary.android.data.local.GroupEntity
import com.smtudiary.android.data.local.LessonEntity
import com.smtudiary.android.data.model.Group
import com.smtudiary.android.data.model.Lesson
import com.smtudiary.android.data.remote.LessonDto
import java.util.UUID

fun LessonDto.toEntity() = LessonEntity(
    stableId(), name, time, teacher, week,
    room, group.toInt(), day
)

fun LessonEntity.toModel() = Lesson(
    id, name, time, teacher, week,
    room, group.toString(), day
)

fun GroupEntity.toModel() = Group(group)

private fun LessonDto.stableId(): UUID = UUID.nameUUIDFromBytes(
    listOf(group, day, time, week, name, room, teacher).joinToString("|").toByteArray()
)