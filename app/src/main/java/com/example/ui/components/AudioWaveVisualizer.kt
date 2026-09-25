package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.VioletPulse

@Composable
fun AudioWaveVisualizer(
    isActive: Boolean,
    amplitude: Float,
    modifier: Modifier = Modifier,
    barCount: Int = 9,
    maxHeight: Dp = 28.dp,
    barWidth: Dp = 3.dp,
    color: Color = CyanNeon
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_bars")

    val baseAnim1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b1"
    )

    val baseAnim2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "b2"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val factor = when (i % 3) {
                0 -> baseAnim1
                1 -> baseAnim2
                else -> (baseAnim1 + baseAnim2) / 2f
            }

            val targetFraction = if (isActive) {
                (factor * 0.4f + amplitude * 0.6f).coerceIn(0.15f, 1.0f)
            } else {
                0.12f
            }

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(maxHeight * targetFraction)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                if (isActive) color else Color.Gray.copy(alpha = 0.4f),
                                if (isActive) VioletPulse else Color.Gray.copy(alpha = 0.2f)
                            )
                        )
                    )
            )
        }
    }
}
