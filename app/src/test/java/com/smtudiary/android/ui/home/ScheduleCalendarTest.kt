package com.smtudiary.android.ui.home

import com.smtudiary.android.data.local.WeekType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class ScheduleCalendarTest {
    private val zone = ZoneId.of("Europe/Moscow")

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
}
