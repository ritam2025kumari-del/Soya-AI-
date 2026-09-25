package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmberGaze
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.OrbGradientEnd
import com.example.ui.theme.OrbGradientMid
import com.example.ui.theme.OrbGradientStart
import com.example.ui.theme.VioletPulse
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SoyaOrbReactor(
    isListening: Boolean,
    isSpeaking: Boolean,
    isProcessing: Boolean,
    audioAmplitude: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_anim")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isProcessing) 2000 else 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val dynamicScale = when {
        isListening -> 1.0f + (audioAmplitude * 0.25f)
        isSpeaking -> 1.0f + (audioAmplitude * 0.20f)
        isProcessing -> pulseScale * 1.05f
        else -> pulseScale
    }

    val glowColor = when {
        isListening -> EmeraldShield
        isSpeaking -> CyanNeon
        isProcessing -> AmberGaze
        else -> VioletPulse
    }

    Box(
        modifier = modifier
            .size(size)
            .testTag("soya_orb_reactor")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Outer Blur Glow
        Box(
            modifier = Modifier
                .size(size * 0.85f * dynamicScale)
                .blur(32.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.6f),
                            OrbGradientMid.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Geometric Quantum Waves Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(this.size.width / 2, this.size.height / 2)
            val baseRadius = (this.size.minDimension / 2) * 0.72f * dynamicScale

            // Draw outer orbit rings
            drawCircle(
                color = glowColor.copy(alpha = 0.35f),
                radius = baseRadius * 1.15f,
                center = centerOffset,
                style = Stroke(width = 2.dp.toPx())
            )

            drawCircle(
                color = OrbGradientStart.copy(alpha = 0.25f),
                radius = baseRadius * 0.95f,
                center = centerOffset,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Orbiting particle nodes
            val particleCount = 6
            for (i in 0 until particleCount) {
                val rad = Math.toRadians((rotationAngle + (i * 360f / particleCount)).toDouble())
                val orbitRadius = baseRadius * (if (i % 2 == 0) 1.15f else 0.95f)
                val px = centerOffset.x + (orbitRadius * cos(rad)).toFloat()
                val py = centerOffset.y + (orbitRadius * sin(rad)).toFloat()

                drawCircle(
                    color = if (i % 2 == 0) glowColor else OrbGradientStart,
                    radius = if (isListening || isSpeaking) 4.dp.toPx() else 2.5.dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }

        // Inner Core Spherical Orb
        Box(
            modifier = Modifier
                .size(size * 0.62f * dynamicScale)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor,
                            OrbGradientStart,
                            OrbGradientMid,
                            OrbGradientEnd.copy(alpha = 0.85f)
                        ),
                        center = Offset.Zero
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Center Core Icon
            val icon = when {
                isListening -> Icons.Default.Mic
                isSpeaking -> Icons.Default.GraphicEq
                else -> Icons.Default.Psychology
            }

            Icon(
                imageVector = icon,
                contentDescription = "SOYA AI Orb Status",
                tint = Color.White,
                modifier = Modifier.size(size * 0.28f)
            )
        }
    }
}
