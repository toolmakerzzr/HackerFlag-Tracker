package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.TextureDotColor
import com.example.ui.theme.TextureGridLine

/**
 * Ultra-Comfortable textured background providing a soft luminous paper-grid aesthetic.
 * Eliminates harsh flat glare while remaining ultra-light, tactile, and soothing for prolonged viewing.
 * Crafted & Engineered by Akash Tiwari for zero-fatigue high-clarity viewing.
 */
@Composable
fun ComfortableBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFAFBFE),
                        CyberBackground,
                        Color(0xFFF1F5FA),
                        Color(0xFFEBF1F8)
                    )
                )
            )
    ) {
        // Delicate subtle tactile dot-grid & precision cyber grid Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacingPx = 26.dp.toPx()
            val dotRadius = 1.15f
            val crosshairSize = 4.dp.toPx()

            var colIndex = 0
            var x = spacingPx / 2f
            while (x < size.width) {
                var rowIndex = 0
                var y = spacingPx / 2f
                while (y < size.height) {
                    // Precision crosshair accent every 5 rows & columns
                    if (colIndex % 5 == 0 && rowIndex % 5 == 0) {
                        val accentColor = CyberCyan.copy(alpha = 0.16f)
                        drawLine(
                            color = accentColor,
                            start = Offset(x - crosshairSize, y),
                            end = Offset(x + crosshairSize, y),
                            strokeWidth = 1f
                        )
                        drawLine(
                            color = accentColor,
                            start = Offset(x, y - crosshairSize),
                            end = Offset(x, y + crosshairSize),
                            strokeWidth = 1f
                        )
                    } else {
                        drawCircle(
                            color = TextureDotColor,
                            radius = dotRadius,
                            center = Offset(x, y)
                        )
                    }
                    y += spacingPx
                    rowIndex++
                }
                x += spacingPx
                colIndex++
            }

            // Subtle corner registration marks (Cyber defense telemetry aesthetic)
            val markColor = CyberCyan.copy(alpha = 0.22f)
            val cornerOffset = 18.dp.toPx()
            val cornerArm = 14.dp.toPx()

            // Top-Left corner marker
            drawLine(color = markColor, start = Offset(cornerOffset, cornerOffset), end = Offset(cornerOffset + cornerArm, cornerOffset), strokeWidth = 1.5f)
            drawLine(color = markColor, start = Offset(cornerOffset, cornerOffset), end = Offset(cornerOffset, cornerOffset + cornerArm), strokeWidth = 1.5f)

            // Top-Right corner marker
            drawLine(color = markColor, start = Offset(size.width - cornerOffset, cornerOffset), end = Offset(size.width - cornerOffset - cornerArm, cornerOffset), strokeWidth = 1.5f)
            drawLine(color = markColor, start = Offset(size.width - cornerOffset, cornerOffset), end = Offset(size.width - cornerOffset, cornerOffset + cornerArm), strokeWidth = 1.5f)
        }

        // Overlay screen composable content
        content()
    }
}
