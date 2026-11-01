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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GroupsUIState(
    var groups: List<Group> = emptyList(),
    var isLoading: Boolean = false,
    var error: String? = null
)

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val repository: GroupRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val error: MutableStateFlow<String?> = MutableStateFlow(null)

    private val _selectedGroup: MutableStateFlow<String> = MutableStateFlow("")
    val selectedGroup = _selectedGroup.asStateFlow()

    val uiState: StateFlow<GroupsUIState> = combine(
        repository.observeGroups(), isLoading, error
    ) { groups, isLoading, isError -> GroupsUIState(groups, isLoading, isError) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GroupsUIState())

    init {
        viewModelScope.launch {
            settingsRepository.group.collect { group ->
                _selectedGroup.value = group
            }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            repository.refreshGroups()
            isLoading.value = false
        }
    }

    fun selectGroup(group: String) {
        viewModelScope.launch {
            settingsRepository.setGroup(group)
        }
    }

}