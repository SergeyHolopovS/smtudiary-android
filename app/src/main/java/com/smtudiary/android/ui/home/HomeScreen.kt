package com.smtudiary.android.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smtudiary.android.data.local.WeekType
import com.smtudiary.android.data.model.Lesson
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

val days = arrayOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
val shortDays = arrayOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб")

private val russianDaysOfWeek: Map<String, DayOfWeek> = mapOf(
    "понедельник" to DayOfWeek.MONDAY,
    "вторник" to DayOfWeek.TUESDAY,
    "среда" to DayOfWeek.WEDNESDAY,
    "четверг" to DayOfWeek.THURSDAY,
    "пятница" to DayOfWeek.FRIDAY,
    "суббота" to DayOfWeek.SATURDAY,
    "воскресенье" to DayOfWeek.SUNDAY,
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()

    var selectedDay by remember {
        mutableIntStateOf(
            Instant.now().atZone(ZoneId.of("Europe/Moscow")).dayOfWeek.value - 1
        )
    }

    Scaffold { initialPadding ->
        Column(modifier = Modifier.padding(initialPadding)) {
            Box(
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AnimatedContent(
                        targetState = selectedDay,
                        transitionSpec = { dayFadeTransition() },
                        label = "dayTitle",
                    ) { day ->
                        Text(
                            text = days[day],
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(6) { day ->
                            WeekDay(index = day, selectedDay == day, onClick = { selectedDay = day })
                        }
                    }
                }
            }
            lessons.let {
                when (val state = lessons) {
                    is HomeUiState.Success -> AnimatedContent(
                        targetState = selectedDay,
                        transitionSpec = { dayFadeTransition() },
                        label = "dayLessons",
                    ) { day ->
                        LazyColumn {
                            items(
                                items = state.lessons.filter { element ->
                                    element.day == days[day] && (currentWeekType(
                                        weekdayString(
                                            dayOfWeekByIndex(day)
                                        )
                                    ) == element.week)
                                },
                                key = { it.id }
                            ) { Lesson(it) }
                        }
                    }
                    HomeUiState.Error -> Text("Не удалось загрузить расписание")
                    HomeUiState.Loading -> Text("Загрузка...")
                }
            }
        }
    }

}

private const val DAY_FADE_DURATION = 150

private fun dayFadeTransition() = ContentTransform(
    targetContentEnter = fadeIn(tween(DAY_FADE_DURATION, delayMillis = DAY_FADE_DURATION)),
    initialContentExit = fadeOut(tween(DAY_FADE_DURATION)),
    sizeTransform = null,
)

@Composable
fun Lesson(lesson: Lesson) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${lesson.name}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.background
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.height(1.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = lesson.time,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.background
                        )
                        Text(
                            text = lesson.room,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.background
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.WeekDay(index: Int, selected: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    )

    Surface (
        modifier = Modifier
            .weight(1f)
            .height(72.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = shortDays[index],
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
            Text(
                text = weekdayString(dayIndex = index),
                color = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
    }
}

fun dayOfWeekByIndex(index: Int): DayOfWeek {
    require(index in 0..5) { "Индекс должен быть от 0 до 5, получено: $index" }
    return DayOfWeek.of(index + 1)
}

fun dayOfMonthString(
    instant: Instant,
    zone: ZoneId = ZoneId.of("Europe/Moscow")
): String = instant.atZone(zone).dayOfMonth.toString()

fun weekdayString(
    dayIndex: Int,
    weeksAhead: Long = 0,
    now: Instant = Instant.now(),
    zone: ZoneId = ZoneId.of("Europe/Moscow")
): String = dayOfMonthString(
    weekdayString(
        dayOfWeekByIndex(dayIndex),
        weeksAhead,
        now,
        zone
    )
)

fun weekdayString(
    dayOfWeek: DayOfWeek,
    weeksAhead: Long = 0,
    now: Instant = Instant.now(),
    zone: ZoneId = ZoneId.of("Europe/Moscow")
): Instant {
    val today = now.atZone(zone).toLocalDate()
    val monday = today.minusDays((today.dayOfWeek.value - 1).toLong())
    val targetDate = monday
        .plusWeeks(weeksAhead)
        .plusDays((dayOfWeek.value - 1).toLong())

    return targetDate.atStartOfDay(zone).toInstant()
}

fun currentWeekType(now: Instant = Instant.now()): WeekType {
    // Получаем текущие дату и время
    val today = now.atZone(ZoneId.of("Europe/Moscow")).toLocalDate()

    // Учебный год начинается 1 сентября текущего года или прошлого, если сейчас раньше
    val year = if (today >= LocalDate.of(today.year, 9, 1)) today.year else today.year - 1
    val sept1 = LocalDate.of(year, 9, 1)

    // Понедельник недели, в которую попадает 1 сентября
    val weekStart = sept1.minusDays((sept1.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())

    // Считаем прошедшие недели и выдаём результат
    val weeksPassed = ChronoUnit.DAYS.between(weekStart, today) / 7
    return if (weeksPassed % 2 == 0L) WeekType.UPPER else WeekType.LOWER
}