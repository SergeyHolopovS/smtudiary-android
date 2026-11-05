package com.smtudiary.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smtudiary.android.data.model.Group
import com.smtudiary.android.data.repository.GroupRepository
import com.smtudiary.android.data.repository.SettingsRepository
import com.smtudiary.android.data.suspendRunCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isSaving: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val loadError: String? = null,
    val group: String = "",
    val selectedGroup: String = "",
    val query: String = "",
    val groups: List<Group> = emptyList()
) {
    val filteredGroups: List<Group>
        get() = groups.filter { it.group.contains(query.trim(), ignoreCase = true) }
    val canSave: Boolean
        get() = !isSaving && selectedGroup.isNotBlank() && selectedGroup != group &&
            groups.any { it.group == selectedGroup }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val state = MutableStateFlow(SettingsUiState())

    val uiState: StateFlow<SettingsUiState> = combine(
        state, settingsRepository.group, groupRepository.observeGroups()
    ) { local, group, groups -> local.copy(group = group, groups = groups) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    private var refreshJob: kotlinx.coroutines.Job? = null

    init { refresh() }

    fun search(query: String) { state.update { it.copy(query = query) } }

    fun selectGroup(group: String) {
        if (state.value.isSaving) return
        state.update { it.copy(selectedGroup = group, error = null) }
    }

    fun refresh() {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            state.update { it.copy(isLoading = true, loadError = null) }
            val result = groupRepository.refreshGroups()
            state.update {
                it.copy(isLoading = false, loadError = if (result.isFailure)
                    "Не удалось загрузить группы. Проверьте подключение." else null)
            }
        }
    }

    fun saveGroup(onSaved: (String) -> Unit) {
        val current = uiState.value
        if (state.value.isSaving || !current.canSave) return
        state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                suspendRunCatching { settingsRepository.setGroup(current.selectedGroup) }
                    .onSuccess { onSaved(current.selectedGroup) }
                    .onFailure { state.update { it.copy(error = "Не удалось сохранить группу. Попробуйте ещё раз.") } }
            } finally {
                state.update { it.copy(isSaving = false) }
            }
        }
    }
}
