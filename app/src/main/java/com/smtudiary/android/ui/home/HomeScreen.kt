package com.smtudiary.android.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smtudiary.android.data.model.Lesson

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()

    lessons.let {
        when (val state = lessons) {
            is HomeUiState.Success -> LazyColumn {
                items(state.lessons) { Lesson(it) }
            }
            HomeUiState.Error -> Text("Не удалось загрузить расписание")
            HomeUiState.Loading -> Text("Загрузка...")
        }
    }
}

@Composable
fun Lesson(lesson: Lesson) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column (verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = "${lesson.name}")
                Text(lesson.time)
            }
        }
    }
}