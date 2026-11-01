package com.smtudiary.android.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
internal fun ScheduleHeader(groupName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(17.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Text(
                text = groupName,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp
            )
        }
        Surface(
            border = BorderStroke(
                color = MaterialTheme.colorScheme.onSecondary,
                width = 1.dp
            ),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(36.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    modifier = Modifier.size(18.dp),
                    contentDescription = "Settings button",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
internal fun ScheduleTitle(date: LocalDate, onPreviousWeek: () -> Unit, onNextWeek: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(
                top = 18.dp,
                bottom = 22.dp
            ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Расписание",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 46.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatScheduleMonth(date),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondary
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onPreviousWeek,
                    modifier = Modifier
                        .size(16.dp),
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    color = MaterialTheme.colorScheme.background
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onSecondary,
                        contentDescription = "Предыдущая неделя"
                    )
                }
                Surface(
                    onClick = onNextWeek,
                    modifier = Modifier
                        .size(16.dp),
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    color = MaterialTheme.colorScheme.background
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onSecondary,
                        contentDescription = "Следующая неделя"
                    )
                }
            }
        }
    }
}

@Composable
internal fun ScheduleWeek(weekStart: LocalDate, selectedDate: LocalDate, onDaySelected: (LocalDate) -> Unit) {
    AnimatedContent(
        targetState = weekStart,
        transitionSpec = { dayFadeTransition() },
        modifier = Modifier.height(72.dp).fillMaxWidth(),
        label = "weekDays"
    ) { monday ->
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(6) { index ->
                WeekDay(
                    index = index,
                    date = monday.plusDays(index.toLong()),
                    selected = monday.plusDays(index.toLong()) == selectedDate,
                    onClick = { onDaySelected(monday.plusDays(index.toLong())) },
                )
            }
        }
    }
}

@Composable
internal fun DaySchedule(lessons: HomeUiState, selectedDate: LocalDate, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(top = 22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AnimatedContent(
            targetState = selectedDate to (lessons as? HomeUiState.Success)?.let { lessonsOnDate(it.lessons, selectedDate).size },
            transitionSpec = { dayFadeTransition() },
            modifier = Modifier.fillMaxWidth(),
            label = "dayHeading"
        ) { (date, count) ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formatScheduleDate(date),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = count?.let { lessonCountText(it) } ?: "",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
        when (val state = lessons) {
            is HomeUiState.Success -> {
                AnimatedContent(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    targetState = selectedDate,
                    transitionSpec = { dayFadeTransition() },
                    label = "dayLessons",
                ) { date ->
                    val dayLessons = lessonsOnDate(state.lessons, date)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = dayLessons,
                            key = { it.id }
                        ) { LessonCard(it) }
                        if (dayLessons.isEmpty()) {
                            item { Text("Нет пар") }
                        }
                    }
                }
            }
            HomeUiState.Error -> Text("Ошибка")
            HomeUiState.Loading -> Text("Загрузка")
        }
    }
}

@Composable
internal fun NextDayButton(text: String, onClick: () -> Unit) {
    Surface(
        contentColor = MaterialTheme.colorScheme.onSecondary,
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSecondary
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
                contentDescription = "Arrow forward"
            )
        }
    }
}

private const val DAY_FADE_DURATION = 150

internal fun dayFadeTransition() = ContentTransform(
    targetContentEnter = fadeIn(tween(DAY_FADE_DURATION, delayMillis = DAY_FADE_DURATION)),
    initialContentExit = fadeOut(tween(DAY_FADE_DURATION)),
    sizeTransform = null,
)

