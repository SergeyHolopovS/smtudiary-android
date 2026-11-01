package com.smtudiary.android.ui.loading

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smtudiary.android.ui.theme.AndroidTheme

@Composable
fun LoadingScreen() {
    val colors = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "loadingRhythm")

    Scaffold(containerColor = colors.background) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(300.dp).background(
                    brush = Brush.radialGradient(
                        listOf(colors.primary.copy(alpha = 0.10f), colors.primary.copy(alpha = 0f))
                    ),
                    shape = CircleShape
                )
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Box(
                    modifier = Modifier.size(88.dp)
                        .background(colors.surfaceContainer, RoundedCornerShape(28.dp))
                        .border(1.dp, colors.primary.copy(alpha = 0.20f), RoundedCornerShape(28.dp))
                        .semantics {
                            contentDescription = "Загрузка"
                            progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.height(36.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            val height by transition.animateFloat(
                                initialValue = 0.3f,
                                targetValue = 0.3f,
                                animationSpec = infiniteRepeatable(
                                    animation = keyframes {
                                        durationMillis = 1400
                                        0.3f at 0
                                        0.3f at index * 120 using FastOutSlowInEasing
                                        1f at index * 120 + 350 using FastOutSlowInEasing
                                        0.3f at index * 120 + 700
                                        0.3f at 1400
                                    },
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "rhythmBar$index"
                            )
                            Box(
                                modifier = Modifier.width(5.dp).height(36.dp * height)
                                    .background(colors.primary, RoundedCornerShape(3.dp))
                            )
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "ритм",
                    color = colors.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 42.sp,
                    letterSpacing = (-1).sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Твой день. В твоём ритме.",
                    color = colors.tertiary,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(32.dp))
                Text(
                    text = "Загрузка…",
                    color = colors.onSecondary,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                text = "УЧЕБНЫЙ ДНЕВНИК",
                color = colors.onSecondary,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun LoadingScreenPreview() {
    AndroidTheme { LoadingScreen() }
}
