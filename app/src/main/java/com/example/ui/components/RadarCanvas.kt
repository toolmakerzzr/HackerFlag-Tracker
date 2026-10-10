package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CellTower
import com.example.ui.theme.CyberAlertCrimson
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberSuccessGreen
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarCanvas(
    cellTowers: List<CellTower>,
    modifier: Modifier = Modifier,
    targetDetected: Boolean = true,
    uncertaintyRadiusMeters: Int = 120
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF060A12))
            .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = minOf(size.width, size.height) / 2f * 0.88f

            // 1. Concentric distance rings
            val rings = listOf(0.33f, 0.66f, 1.0f)
            rings.forEach { ratio ->
                drawCircle(
                    color = CyberCyan.copy(alpha = 0.2f),
                    radius = maxRadius * ratio,
                    center = center,
                    style = Stroke(width = 1.2f)
                )
            }

            // Crosshair axes
            drawLine(
                color = CyberCyan.copy(alpha = 0.25f),
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1f
            )
            drawLine(
                color = CyberCyan.copy(alpha = 0.25f),
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1f
            )

            // 2. Rotating radar beam line
            val rad = Math.toRadians(sweepAngle.toDouble())
            val lineEnd = Offset(
                x = center.x + (maxRadius * cos(rad)).toFloat(),
                y = center.y + (maxRadius * sin(rad)).toFloat()
            )
            drawLine(
                brush = Brush.linearGradient(
                    listOf(CyberCyan.copy(alpha = 0.9f), CyberCyan.copy(alpha = 0.1f)),
                    start = center,
                    end = lineEnd
                ),
                start = center,
                end = lineEnd,
                strokeWidth = 2.5f
            )

            // 3. Draw Cell Towers
            cellTowers.forEachIndexed { index, tower ->
                val angleDeg = tower.azimuthDegrees
                val towerRad = Math.toRadians(angleDeg.toDouble())
                val distRatio = (tower.distanceMeters.coerceIn(300, 2000) / 2000f) * maxRadius
                val towerOffset = Offset(
                    x = center.x + (distRatio * cos(towerRad)).toFloat(),
                    y = center.y + (distRatio * sin(towerRad)).toFloat()
                )

                // Draw Tower Triangle
                drawCircle(
                    color = CyberGold,
                    radius = 4f,
                    center = towerOffset
                )
                drawCircle(
                    color = CyberGold.copy(alpha = 0.35f),
                    radius = 8f,
                    center = towerOffset,
                    style = Stroke(1.5f)
                )

                // Triangulation connector line from tower to target center
                val targetCenter = Offset(center.x + 20f, center.y - 15f)
                drawLine(
                    color = CyberGold.copy(alpha = 0.25f),
                    start = towerOffset,
                    end = targetCenter,
                    strokeWidth = 1f
                )
            }

            // 4. Triangulated Device Blip & Uncertainty Zone
            if (targetDetected) {
                val targetCenter = Offset(center.x + 20f, center.y - 15f)

                // Uncertainty Ellipse
                drawCircle(
                    color = CyberAlertCrimson.copy(alpha = 0.15f),
                    radius = 35f,
                    center = targetCenter
                )
                drawCircle(
                    color = CyberAlertCrimson.copy(alpha = 0.5f),
                    radius = 35f,
                    center = targetCenter,
                    style = Stroke(1.2f)
                )

                // Pulsing wave
                drawCircle(
                    color = CyberAlertCrimson.copy(alpha = (0.6f / pulseScale)),
                    radius = 25f * pulseScale,
                    center = targetCenter,
                    style = Stroke(1.5f)
                )

                // Central red target dot
                drawCircle(
                    color = CyberAlertCrimson,
                    radius = 5.5f,
                    center = targetCenter
                )
            }
        }

        // HUD Text Overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
        ) {
            Text(
                text = "GIS RADAR: ACTIVE TRIANGULATION",
                color = CyberCyan,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ACCURACY: ±${uncertaintyRadiusMeters}m (3-TOWER SECTOR)",
                color = CyberTextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "LAT: 28.6324° N  LNG: 77.2187° E",
                color = CyberSuccessGreen,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "SERVING CID: 48921  LAC: 2104",
                color = CyberGold,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
