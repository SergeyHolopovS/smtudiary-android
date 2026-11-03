package com.smtudiary.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smtudiary.android.data.repository.SettingsRepository
import com.smtudiary.android.data.suspendRunCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isSaving: Boolean = false,
    val error: String? = null,
    val group: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val isSaving = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        isSaving, error, settingsRepository.group
    ) { saving, errorMessage, group ->
        SettingsUiState(isSaving = saving, error = errorMessage, group = group)
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun chooseGroup(newgroup: String) {
        if (newgroup.isBlank() || isSaving.value) return
        isSaving.value = true
        viewModelScope.launch {
            try {
                error.value = null
                suspendRunCatching { settingsRepository.setGroup(newgroup) }
                    .onFailure {
                        error.value = "Не удалось сохранить группу. Попробуйте ещё раз."
                    }
            } finally {
                isSaving.value = false
            }
        }
    }

}
