package com.smtudiary.android.ui.home

import com.smtudiary.android.data.model.Lesson
import java.time.LocalDate
import java.time.LocalTime

private val lessonDays = listOf(
    "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье"
)

private val startTimePattern = Regex("^(\\d{1,2}):(\\d{2})$")

fun Lesson.startTimeText(): String = start.trim()

fun Lesson.endTimeText(): String = end.trim()

private fun Lesson.startTime(): LocalTime? =
    startTimePattern.matchEntire(startTimeText())?.let { match ->
        runCatching { LocalTime.of(match.groupValues[1].toInt(), match.groupValues[2].toInt()) }.getOrNull()
    }

fun lessonsOnDate(lessons: List<Lesson>, date: LocalDate): List<Lesson> {
    val week = currentWeekType(date)
    return lessons.filter { it.day == lessonDays[date.dayOfWeek.value - 1] && it.week == week }
        .sortedBy { it.startTime() ?: LocalTime.MAX }
}

fun lessonCountText(count: Int): String = when {
    count == 0 -> "Нет пар"
    count % 100 in 11..14 -> "$count пар"
    count % 10 == 1 -> "$count пара"
    count % 10 in 2..4 -> "$count пары"
    else -> "$count пар"
}

fun lessonSummary(lessons: List<Lesson>): String {
    val first = lessons.mapNotNull { lesson -> lesson.startTime()?.let { it to lesson } }
        .minByOrNull { it.first }?.second
    return lessonCountText(lessons.size) + (first?.let { " · начало в ${it.startTimeText()}" } ?: "")
}
