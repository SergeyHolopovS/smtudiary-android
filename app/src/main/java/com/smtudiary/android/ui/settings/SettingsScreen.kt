package com.smtudiary.android.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: (currentGroup: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { initialPadding ->
        Box(
            modifier = Modifier.padding(initialPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        vertical = 12.dp,
                        horizontal = 24.dp,
                    )
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            color = MaterialTheme.colorScheme.background,
                            onClick = { onBack(uiState.group) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            color = MaterialTheme.colorScheme.background,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Arrow back",
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "К расписанию",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                            Icon(
                            imageVector = Icons.Default.GraphicEq,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp),
                            contentDescription = "App Icon"
                        )
                    }
                }
            }
        }
    }
}
