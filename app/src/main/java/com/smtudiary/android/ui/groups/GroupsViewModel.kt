package com.smtudiary.android.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smtudiary.android.data.model.Group
import com.smtudiary.android.data.repository.GroupRepository
import com.smtudiary.android.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GroupsUiState(
    val groups: List<Group> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val repository: GroupRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<GroupsUiState> = combine(
        repository.observeGroups(), isLoading, error
    ) { groups, loading, errorMessage -> GroupsUiState(groups, loading, errorMessage) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GroupsUiState())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            repository.refreshGroups().onFailure { throwable ->
                error.value = throwable.message ?: "Не удалось загрузить список групп"
            }
            isLoading.value = false
        }
    }

    fun selectGroup(group: String) {
        viewModelScope.launch {
            settingsRepository.setGroup(group)
        }
    }

}
