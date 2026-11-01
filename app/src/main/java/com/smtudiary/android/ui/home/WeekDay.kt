package com.smtudiary.android.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

private val shortDays = listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ")

@Composable
internal fun RowScope.WeekDay(
    index: Int,
    date: LocalDate,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        else MaterialTheme.colorScheme.background,
        animationSpec = tween(300),
        label = "changeAccentColor"
    )

    val accentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.background,
        animationSpec = tween(300),
        label = "changeAccentColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSecondary,
        animationSpec = tween(300),
        label = "changeTextColor"
    )

    Surface (
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .border(
                width = 1.dp,
                color = accentColor,
                shape = RoundedCornerShape(8.dp)
            ),
        color = backgroundColor,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .padding(
                    vertical = 10.dp,
                    horizontal = 12.dp
                )
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = shortDays[index],
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    lineHeight = 14.sp
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    lineHeight = 22.sp
                )
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(
                            color = accentColor,
                            shape = CircleShape
                        ),
                )
            }
        }
    }
}
