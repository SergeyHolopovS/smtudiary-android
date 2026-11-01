package com.smtudiary.android.ui.home

import com.smtudiary.android.data.local.WeekType
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

val scheduleZone: ZoneId = ZoneId.of("Europe/Moscow")
private val scheduleLocale = Locale.forLanguageTag("ru")
private val monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy", scheduleLocale)

fun LocalDate.startOfWeek(): LocalDate = minusDays((dayOfWeek.value - 1).toLong())

fun nextScheduleDate(today: LocalDate): LocalDate =
    today.plusDays(if (today.dayOfWeek == DayOfWeek.SATURDAY) 2 else 1)

fun initialScheduleDate(today: LocalDate): LocalDate =
    if (today.dayOfWeek == DayOfWeek.SUNDAY) today.plusDays(1) else today

fun formatScheduleMonth(date: LocalDate): String = date.format(monthFormatter)
    .replaceFirstChar { it.titlecase(scheduleLocale) }

private val monthsGenitive = listOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря"
)

fun formatScheduleDate(date: LocalDate): String {
    val weekday = date.dayOfWeek.getDisplayName(
        TextStyle.FULL, scheduleLocale
    ).replaceFirstChar { it.titlecase(scheduleLocale) }
    return "$weekday, ${date.dayOfMonth} ${monthsGenitive[date.monthValue - 1]}"
}

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
    val monday = today.startOfWeek()
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
    return currentWeekType(today)
}

fun currentWeekType(today: LocalDate): WeekType {
    val academicYear = if (today.monthValue >= 9) today.year else today.year - 1
    val septemberFirst = LocalDate.of(academicYear, 9, 1)
    val firstWeekStart = septemberFirst.startOfWeek()
    val weeksPassed = ChronoUnit.WEEKS.between(firstWeekStart, today)
    return if (weeksPassed % 2 == 0L) WeekType.UPPER else WeekType.LOWER
}

fun getDayIndex(
    now: Instant = Instant.now(),
    zone: ZoneId = scheduleZone
): Int = now.atZone(zone).dayOfWeek.value - 1

fun incrementDay(
    now: Instant = Instant.now(),
    zone: ZoneId = scheduleZone
): Int = when (val dayIndex = getDayIndex(now, zone)) {
    in 0..4 -> dayIndex + 1
    else -> 0
}
