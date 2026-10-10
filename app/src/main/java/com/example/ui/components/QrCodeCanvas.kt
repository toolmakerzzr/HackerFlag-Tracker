package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold

@Composable
fun OwnershipQrCanvas(
    payloadString: String,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 180.dp
) {
    // Generate deterministic 21x21 grid pattern based on payload hash
    val matrixSize = 21
    val hash = payloadString.hashCode()

    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF04070D))
            .border(1.5.dp, CyberCyan, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.size(sizeDp - 24.dp)) {
            val moduleSize = size.width / matrixSize

            // 1. Draw corner position detection patterns (top-left, top-right, bottom-left)
            fun drawFinderPattern(startX: Int, startY: Int) {
                // Outer 7x7 box
                drawRect(
                    color = CyberCyan,
                    topLeft = Offset(startX * moduleSize, startY * moduleSize),
                    size = Size(7 * moduleSize, 7 * moduleSize)
                )
                // Inner 5x5 cutout
                drawRect(
                    color = Color(0xFF04070D),
                    topLeft = Offset((startX + 1) * moduleSize, (startY + 1) * moduleSize),
                    size = Size(5 * moduleSize, 5 * moduleSize)
                )
                // Center 3x3 solid core
                drawRect(
                    color = CyberCyan,
                    topLeft = Offset((startX + 2) * moduleSize, (startY + 2) * moduleSize),
                    size = Size(3 * moduleSize, 3 * moduleSize)
                )
            }

            drawFinderPattern(0, 0)
            drawFinderPattern(matrixSize - 7, 0)
            drawFinderPattern(0, matrixSize - 7)

            // 2. Timing lines (horizontal & vertical)
            for (i in 8 until matrixSize - 8) {
                if (i % 2 == 0) {
                    drawRect(
                        color = CyberGold,
                        topLeft = Offset(i * moduleSize, 6 * moduleSize),
                        size = Size(moduleSize, moduleSize)
                    )
                    drawRect(
                        color = CyberGold,
                        topLeft = Offset(6 * moduleSize, i * moduleSize),
                        size = Size(moduleSize, moduleSize)
                    )
                }
            }

            // 3. Data modules derived pseudo-randomly from string characters
            for (row in 0 until matrixSize) {
                for (col in 0 until matrixSize) {
                    // Skip finder pattern zones
                    val inTopLeft = row < 8 && col < 8
                    val inTopRight = row < 8 && col >= matrixSize - 8
                    val inBottomLeft = row >= matrixSize - 8 && col < 8
                    val inTiming = row == 6 || col == 6

                    if (!inTopLeft && !inTopRight && !inBottomLeft && !inTiming) {
                        val bitVal = ((hash xor (row * 37 + col * 17)) + (payloadString.getOrNull((row * col) % payloadString.length)?.code ?: 0)) % 2 == 0
                        if (bitVal) {
                            val isAccent = (row + col) % 5 == 0
                            drawRect(
                                color = if (isAccent) CyberGold else CyberCyan,
                                topLeft = Offset(col * moduleSize, row * moduleSize),
                                size = Size(moduleSize * 0.9f, moduleSize * 0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}
