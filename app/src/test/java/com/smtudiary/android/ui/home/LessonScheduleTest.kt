package com.smtudiary.android.ui.home

import com.smtudiary.android.data.local.WeekType
import com.smtudiary.android.data.model.Lesson
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.util.UUID

class LessonScheduleTest {
    private fun lesson(start: String, end: String = "", day: String = "Понедельник", week: WeekType = WeekType.UPPER) =
        Lesson(UUID.randomUUID(), "Математика", start, end, null, null, week, "101", "ИС-24-1", day)

    @Test
    fun `schedule filters day and week and sorts by actual start time`() {
        val early = lesson("9:00", "10:30")
        val late = lesson("11:00", "12:30")
        val lessons = listOf(late, lesson("08:00", "09:30", week = WeekType.LOWER),
            lesson("08:00", "09:30", day = "Вторник"), early)

        assertEquals(listOf(early, late), lessonsOnDate(lessons, LocalDate.of(2026, 9, 14)))
        assertEquals("2 пары · начало в 9:00", lessonSummary(listOf(late, early)))
    }

    @Test
    fun `summary handles empty or invalid times without inventing start time`() {
        assertEquals("Нет пар", lessonSummary(emptyList()))
        assertEquals("1 пара", lessonSummary(listOf(lesson("уточняется"))))
        assertEquals("", lesson("уточняется").endTimeText())
        assertEquals("10:30", lesson("9:00", "10:30").endTimeText())
    }

    @Test
    fun `counts use russian plural forms`() {
        val expected = mapOf(0 to "Нет пар", 1 to "1 пара", 2 to "2 пары", 5 to "5 пар",
            11 to "11 пар", 14 to "14 пар", 21 to "21 пара", 22 to "22 пары")
        expected.forEach { (count, text) -> assertEquals(text, lessonCountText(count)) }
    }
}
