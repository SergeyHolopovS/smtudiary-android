package com.smtudiary.android.ui.home

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.smtudiary.android.data.model.Lesson
import com.smtudiary.android.data.repository.LessonRepository
import com.smtudiary.android.navigation.HomeRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val lessons: List<Lesson>) : HomeUiState
    data object Error : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: LessonRepository
) : ViewModel() {

    private val groupId = savedStateHandle.toRoute<HomeRoute>().group

    private val _lessons = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val lessons : StateFlow<HomeUiState> = _lessons.asStateFlow()

    init {
        observeLessons()
        refresh()
    }

    private fun observeLessons() {
        viewModelScope.launch {
            repository.observeLessons(groupId)
                .map { HomeUiState.Success(it) }
                .catch { _lessons.value = HomeUiState.Error }
                .collect { _lessons.value = it }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshLessons(groupId).onFailure {
                Log.e("HomeViewModel", "Failed to refresh lessons", it)
                if (_lessons.value !is HomeUiState.Success) _lessons.value = HomeUiState.Error
            }
        }
    }

}