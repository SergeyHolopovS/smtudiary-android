package com.smtudiary.android.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()
    val today = LocalDate.now(scheduleZone)
    var selectedEpochDay by rememberSaveable {
        mutableLongStateOf(initialScheduleDate(today).toEpochDay())
    }
    val selectedDate = LocalDate.ofEpochDay(selectedEpochDay)
    val loadedLessons = (lessons as? HomeUiState.Success)?.lessons
    val nextDate = nextScheduleDate(today)
    val nextLabel = if (nextDate == today.plusDays(1)) "Завтра" else formatScheduleDate(nextDate)
    val nextSummary = when (val state = lessons) {
        is HomeUiState.Success -> lessonSummary(lessonsOnDate(state.lessons, nextDate))
        HomeUiState.Error -> "Ошибка загрузки"
        HomeUiState.Loading -> "Загрузка"
    }

    Scaffold { initialPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(initialPadding)
                .padding(vertical = 12.dp, horizontal = 24.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                ScheduleHeader(loadedLessons?.firstOrNull()?.group ?: "Расписание группы")
                ScheduleTitle(
                    date = selectedDate,
                    onPreviousWeek = { selectedEpochDay = selectedDate.minusWeeks(1).toEpochDay() },
                    onNextWeek = { selectedEpochDay = selectedDate.plusWeeks(1).toEpochDay() }
                )
                ScheduleWeek(
                    weekStart = selectedDate.startOfWeek(),
                    selectedDate = selectedDate,
                    onDaySelected = { selectedEpochDay = it.toEpochDay() }
                )
                DaySchedule(lessons, selectedDate, Modifier.weight(1f))
            }
            NextDayButton(
                text = "$nextLabel · $nextSummary",
                onClick = { selectedEpochDay = nextDate.toEpochDay() }
            )
        }
    }
}
