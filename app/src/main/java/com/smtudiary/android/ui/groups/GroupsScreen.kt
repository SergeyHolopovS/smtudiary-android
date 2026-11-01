package com.smtudiary.android.ui.groups

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GroupsScreen(
    viewModel: GroupsViewModel = hiltViewModel(),
    onGroupSelected: (group: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var searchField by rememberSaveable { mutableStateOf("") }
    var selectedGroup by rememberSaveable { mutableStateOf("") }

    Scaffold { initialPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(
                    vertical = 16.dp,
                    horizontal = 24.dp
                )
                .fillMaxSize()
                .padding(initialPadding)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp),
                            contentDescription = "App Icon"
                        )
                        Text(
                            text = "ритм",
                            fontWeight = FontWeight.Normal,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = "УЧЕБНЫЙ ДНЕВНИК",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.4f.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
                Column {
                    Column(
                        modifier = Modifier.padding(vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Выбери\n" +
                                    "свою группу.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 46.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            lineHeight = 46.sp
                        )
                        Text(
                            text = "Все пары, аудитории и планы на день.\n" +
                                    "Начнём с твоей группы.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
                Box(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            vertical = 15.dp,
                            horizontal = 16.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.tertiary,
                            contentDescription = "Search icon"
                        )
                        BasicTextField(
                            value = searchField,
                            onValueChange = { searchField = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onPrimary,
                                lineHeight = 16.sp
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (searchField.isEmpty()) {
                                        Text(
                                            text = "Название или номер группы",
                                            color = MaterialTheme.colorScheme.onSecondary,
                                            fontSize = 14.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
                Column(
                    modifier = Modifier.padding(vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "CПИСОК · ГРУПП",
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "СПбГМТУ",
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    LazyColumn(
                        modifier = Modifier.height(238.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.groups.filter {
                            it.group.contains(searchField)
                        }, key = { it.group }) { group ->
                            GroupElement(
                                name = group.group,
                                active = selectedGroup == group.group,
                                onSelect = {
                                    selectedGroup = group.group
                                }
                            )
                        }
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                uiState.error?.let { message ->
                    Text(text = message, color = MaterialTheme.colorScheme.error)
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = selectedGroup.isNotBlank() && !uiState.isSaving,
                    onClick = { viewModel.selectGroup(selectedGroup, onGroupSelected) },
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (uiState.isSaving) "Сохранение…" else "К расписанию",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.background
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowOutward,
                            modifier = Modifier.size(22.dp),
                            tint = MaterialTheme.colorScheme.background,
                            contentDescription = "Go next"
                        )
                    }
                }
                Text(
                    text = "Группу можно изменить в настройках",
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

    }
}

@Composable
fun GroupElement(
    name: String,
    active: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (active) MaterialTheme.colorScheme.surfaceContainerHigh
        else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(300),
        label = "changeBackgroundColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (active) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(300),
        label = "changeBorderColor"
    )

    val checkmarkColor by animateColorAsState(
        targetValue = if (active) MaterialTheme.colorScheme.background
        else Color.Transparent,
        animationSpec = tween(300),
        label = "changeCheckmarkColor"
    )

    val checkmarkBackgroundColor by animateColorAsState(
        targetValue = if (active) MaterialTheme.colorScheme.primary
        else Color.Transparent,
        animationSpec = tween(300),
        label = "changeCheckmarkBackgroundColor"
    )

    Surface (
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(14.dp),
        onClick = { onSelect() },
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Normal,
                fontSize = 21.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        shape = CircleShape,
                        color = checkmarkBackgroundColor
                    )
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    modifier = Modifier.size(14.dp),
                    contentDescription = "Checkmark",
                    tint = checkmarkColor
                )
            }
        }
    }
}
