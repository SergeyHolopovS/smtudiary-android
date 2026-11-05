package com.smtudiary.android.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.smtudiary.android.ui.theme.AndroidTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: (currentGroup: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(
        uiState = uiState,
        onBack = { onBack(uiState.group) },
        onQueryChange = viewModel::search,
        onGroupSelected = viewModel::selectGroup,
        onRetry = viewModel::refresh,
        onSave = { viewModel.saveGroup(onBack) }
    )
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onGroupSelected: (String) -> Unit,
    onRetry: () -> Unit,
    onSave: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Scaffold(containerColor = colors.background) { initialPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(initialPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(onClick = onBack, color = colors.background) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(36.dp)
                                .border(1.dp, colors.onBackground, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", Modifier.size(18.dp), tint = colors.tertiary)
                        }
                        SettingsText("К расписанию", 13, colors.tertiary)
                    }
                }
                Icon(Icons.Default.GraphicEq, null, Modifier.size(22.dp), tint = colors.primary)
            }
            Spacer(Modifier.height(20.dp))
            Text("Настройки", color = colors.onPrimary, fontSize = 38.sp,
                lineHeight = 46.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1.5).sp)
            Spacer(Modifier.height(8.dp))
            SettingsText("Твоя группа — твой учебный ритм.", 14, colors.tertiary)
            Spacer(Modifier.height(24.dp))
            Surface(color = colors.surfaceContainer, shape = RoundedCornerShape(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(Modifier.width(4.dp).height(42.dp).background(colors.primary, RoundedCornerShape(2.dp)))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("ТЕКУЩАЯ ГРУППА", color = colors.onSecondary, fontSize = 9.sp, letterSpacing = 1.6.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SettingsText(uiState.group.ifBlank { "Не выбрана" }, 21, colors.onPrimary)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp)
                    .background(colors.surfaceContainer, RoundedCornerShape(10.dp)).padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Search, null, Modifier.size(18.dp), tint = colors.tertiary)
                BasicTextField(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(color = colors.onPrimary, fontSize = 14.sp),
                    cursorBrush = SolidColor(colors.primary),
                    decorationBox = { field ->
                        Box {
                            if (uiState.query.isEmpty()) SettingsText("Название или номер группы", 14, colors.onSecondary)
                            field()
                        }
                    }
                )
            }
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ГРУППЫ", color = colors.tertiary, fontSize = 10.sp, letterSpacing = 1.5.sp)
                SettingsText(uiState.filteredGroups.size.toString().padStart(2, '0'), 11, colors.onSecondary)
            }
            Spacer(Modifier.height(12.dp))
            uiState.loadError?.let { message ->
                SettingsText(message, 12, colors.error)
                TextButton(onClick = onRetry) { Text("Повторить") }
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 250.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.filteredGroups, key = { it.group }) { group ->
                    SettingsGroupCard(
                        name = group.group,
                        current = group.group == uiState.group,
                        selected = group.group == uiState.selectedGroup,
                        enabled = !uiState.isSaving,
                        onClick = { onGroupSelected(group.group) }
                    )
                }
                if (!uiState.isLoading && uiState.filteredGroups.isEmpty()) {
                    item {
                        SettingsText(if (uiState.query.isBlank()) "Список групп пуст" else "Группы не найдены", 14, colors.tertiary)
                    }
                }
            }
            Spacer(Modifier.height(28.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                SettingsText("Новая группа", 11, colors.onSecondary)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsText(uiState.group.ifBlank { "—" }, 12, colors.onSecondary)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(14.dp), tint = colors.primary)
                    SettingsText(uiState.selectedGroup.ifBlank { "—" }, 12, colors.primary)
                }
            }
            Spacer(Modifier.height(12.dp))
            uiState.error?.let { SettingsText(it, 12, colors.error) }
            Surface(onClick = onSave, enabled = uiState.canSave,
                color = if (uiState.canSave) colors.primary else colors.surfaceContainerHigh,
                shape = RoundedCornerShape(14.dp)) {
                Row(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    SettingsText(if (uiState.isSaving) "Сохранение…" else "Сохранить группу", 16, if (uiState.canSave) colors.background else colors.onSecondary)
                    Icon(Icons.Default.Check, null, Modifier.size(22.dp), tint = colors.background)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Расписание обновится после сохранения", modifier = Modifier.align(Alignment.CenterHorizontally),
                color = colors.onSecondary, fontSize = 11.sp)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SettingsGroupCard(
    name: String,
    current: Boolean = false,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) colors.surfaceContainerHigh else colors.surfaceContainer,
        border = BorderStroke(1.dp, if (selected) colors.secondary else colors.onBackground)
    ) {
        Row(Modifier.fillMaxWidth().height(78.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SettingsText(name, 21, colors.onPrimary)
                    if (current) SettingsText("Текущая", 10, colors.onSecondary)
                }
            }
            Box(
                Modifier.size(23.dp)
                    .background(if (selected) colors.primary else androidx.compose.ui.graphics.Color.Transparent, CircleShape)
                    .border(1.dp, if (selected) colors.primary else colors.onSecondary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) Icon(Icons.Default.Check, null, Modifier.size(15.dp), tint = colors.background)
            }
        }
    }
}

@Composable
private fun SettingsText(text: String, size: Int, color: androidx.compose.ui.graphics.Color) {
    Text(text, color = color, fontSize = size.sp, lineHeight = (size + 4).sp, letterSpacing = 0.sp)
}

@Preview(showBackground = true, widthDp = 412, heightDp = 876)
@Composable
private fun SettingsScreenPreview() {
    AndroidTheme { SettingsContent(SettingsUiState(isLoading = false), {}, {}, {}, {}, {}) }
}
