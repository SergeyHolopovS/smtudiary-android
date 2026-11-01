package com.smtudiary.android.ui.home

import com.smtudiary.android.data.local.WeekType
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

private val scheduleZone = ZoneId.of("Europe/Moscow")

fun dayOfWeekByIndex(index: Int): DayOfWeek {
    require(index in 0..5) { "Индекс должен быть от 0 до 5, получено: $index" }
    return DayOfWeek.of(index + 1)
}

fun weekdayString(
    dayIndex: Int,
    weeksAhead: Long = 0,
    now: Instant = Instant.now(),
    zone: ZoneId = scheduleZone
): String = weekdayInstant(dayOfWeekByIndex(dayIndex), weeksAhead, now, zone)
    .atZone(zone)
    .dayOfMonth
    .toString()

fun weekdayInstant(
    dayOfWeek: DayOfWeek,
    weeksAhead: Long = 0,
    now: Instant = Instant.now(),
    zone: ZoneId = scheduleZone
): Instant {
    val today = now.atZone(zone).toLocalDate()
    val monday = today.minusDays((today.dayOfWeek.value - 1).toLong())
    return monday
        .plusWeeks(weeksAhead)
        .plusDays((dayOfWeek.value - 1).toLong())
        .atStartOfDay(zone)
        .toInstant()
}

fun currentWeekType(
    now: Instant = Instant.now(),
    zone: ZoneId = scheduleZone
): WeekType {
    val today = now.atZone(zone).toLocalDate()
    val academicYear = if (today.monthValue >= 9) today.year else today.year - 1
    val septemberFirst = LocalDate.of(academicYear, 9, 1)
    val firstWeekStart = septemberFirst.minusDays((septemberFirst.dayOfWeek.value - 1).toLong())
    val weeksPassed = ChronoUnit.WEEKS.between(firstWeekStart, today)
    return if (weeksPassed % 2 == 0L) WeekType.UPPER else WeekType.LOWER
}

fun getDayIndex(
    now: Instant = Instant.now(),
    zone: ZoneId = scheduleZone
): Int = now.atZone(zone).dayOfWeek.value - 1
