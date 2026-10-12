package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberGreenGlow
import com.example.ui.theme.CyberGreenPrimary
import com.example.ui.theme.CyberRedAlert
import com.example.ui.theme.CyberRedGlow

@Composable
fun EdgeGlowOverlay(
    isThreat: Boolean,
    isShieldActive: Boolean,
    glowIntensity: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    if (!isShieldActive) return

    val targetColor = if (isThreat) CyberRedAlert else CyberGreenPrimary
    val targetGlowColor = if (isThreat) CyberRedGlow else CyberGreenGlow

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
        label = "edge_color"
    )

    val animatedGlowColor by animateColorAsState(
        targetValue = targetGlowColor,
        animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
        label = "edge_glow_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isThreat) 700 else 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "edge_pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidthPx = 3.5.dp.toPx() * glowIntensity
        val cornerRadiusPx = 28.dp.toPx()
        val effectiveAlpha = (pulseAlpha * glowIntensity).coerceIn(0.1f, 1f)

        // Subtle outer perimeter edge glow
        drawRoundRect(
            color = animatedGlowColor.copy(alpha = effectiveAlpha * 0.35f),
            topLeft = Offset(0f, 0f),
            size = Size(size.width, size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx),
            style = Stroke(width = strokeWidthPx * 2.5f)
        )

        // Sharp perimeter vector line
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    animatedColor.copy(alpha = effectiveAlpha),
                    animatedGlowColor.copy(alpha = effectiveAlpha * 0.7f),
                    animatedColor.copy(alpha = effectiveAlpha)
                ),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            ),
            topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
            size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx),
            style = Stroke(width = strokeWidthPx)
        )
    }
}
