package com.smtudiary.android.ui.home

import com.smtudiary.android.data.local.WeekType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class ScheduleCalendarTest {
    private val zone = ZoneId.of("Europe/Moscow")

    @Test
    fun `initial schedule keeps weekdays and moves sunday to monday`() {
        val saturday = LocalDate.of(2026, 10, 31)
        val sunday = saturday.plusDays(1)
        val monday = sunday.plusDays(1)

        assertEquals(saturday, initialScheduleDate(saturday))
        assertEquals(monday, initialScheduleDate(sunday))
        assertEquals(monday, nextScheduleDate(saturday))
        assertEquals(monday, nextScheduleDate(sunday))
    }

    @Test
    fun `week navigation preserves weekday across year boundary`() {
        val saturday = LocalDate.of(2026, 12, 26)
        val next = saturday.plusWeeks(1)

        assertEquals(LocalDate.of(2027, 1, 2), next)
        assertEquals(LocalDate.of(2026, 12, 28), next.startOfWeek())
        assertEquals(saturday, next.minusWeeks(1))
    }

    @Test
    fun `dates use genitive month names`() {
        val months = listOf("января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря")
        months.forEachIndexed { index, month ->
            assertEquals("1 $month", formatScheduleDate(LocalDate.of(2026, index + 1, 1)).substringAfter(", "))
        }
        assertEquals("Воскресенье, 1 ноября", formatScheduleDate(LocalDate.of(2026, 11, 1)))
    }

    @Test
    fun `weekdayString returns date in current week`() {
        val wednesday = Instant.parse("2026-10-28T09:00:00Z")

        assertEquals("26", weekdayString(dayIndex = 0, now = wednesday, zone = zone))
        assertEquals("31", weekdayString(dayIndex = 5, now = wednesday, zone = zone))
    }

    @Test
    fun `sunday moves displayed dates to next week`() {
        val sunday = Instant.parse("2026-11-01T09:00:00Z")

        assertEquals("2", weekdayString(dayIndex = 0, weeksAhead = 1, now = sunday, zone = zone))
    }

    @Test
    fun `week type alternates from academic year start week`() {
        assertEquals(WeekType.UPPER, currentWeekType(Instant.parse("2026-09-01T09:00:00Z"), zone))
        assertEquals(WeekType.LOWER, currentWeekType(Instant.parse("2026-09-08T09:00:00Z"), zone))
    }

    @Test
    fun `day index rejects sunday`() {
        assertThrows(IllegalArgumentException::class.java) { dayOfWeekByIndex(6) }
    }

    @Test
    fun `next day advances through teaching days and skips sunday`() {
        val monday = Instant.parse("2026-10-26T09:00:00Z")
        val expectedIndices = listOf(1, 2, 3, 4, 5, 0, 0)

        expectedIndices.forEachIndexed { offset, expected ->
            val now = monday.plusSeconds(offset * 24L * 60 * 60)
            val nextDay = incrementDay(now, zone)

            assertEquals(expected, nextDay)
            assertEquals(expected + 1, dayOfWeekByIndex(nextDay).value)
        }
    }
}
