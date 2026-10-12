package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberDarkBgAlt
import com.example.ui.theme.CyberGreenDark
import com.example.ui.theme.CyberGreenGlow
import com.example.ui.theme.CyberGreenPrimary
import com.example.ui.theme.CyberRedAlert
import com.example.ui.theme.CyberRedDark
import com.example.ui.theme.CyberRedGlow
import com.example.ui.theme.CyberTextDim
import com.example.ui.theme.CyberTextWhite

@Composable
fun HeroShieldSection(
    isShieldActive: Boolean,
    isThreat: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor by animateColorAsState(
        targetValue = when {
            !isShieldActive -> CyberTextDim
            isThreat -> CyberRedAlert
            else -> CyberGreenPrimary
        },
        animationSpec = tween(500),
        label = "shield_color"
    )

    val activeGlowColor by animateColorAsState(
        targetValue = when {
            !isShieldActive -> Color.Transparent
            isThreat -> CyberRedGlow
            else -> CyberGreenGlow
        },
        animationSpec = tween(500),
        label = "shield_glow"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "halo")
    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isThreat) 700 else 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing Hero Shield Avatar
        Box(
            modifier = Modifier
                .size(170.dp)
                .clip(CircleShape)
                .clickable { onToggle(!isShieldActive) }
                .testTag("hero_shield_toggle"),
            contentAlignment = Alignment.Center
        ) {
            // Ambient radial halo
            if (isShieldActive) {
                Canvas(modifier = Modifier.size(170.dp)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = (size.width / 2) * haloPulse
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                activeGlowColor.copy(alpha = 0.45f),
                                activeColor.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )
                }
            }

            // Shield Icon with dual layer depth
            Box(
                modifier = Modifier.size(105.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "CyberShield Icon Glow",
                    tint = activeGlowColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(105.dp)
                )
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "CyberShield Primary",
                    tint = activeColor,
                    modifier = Modifier.size(92.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tap to Toggle Shield",
            color = CyberTextDim,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ON / OFF Toggle Switch matching wireframe
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ON",
                color = if (isShieldActive) CyberGreenGlow else CyberTextDim.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Switch(
                checked = isShieldActive,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyberTextWhite,
                    checkedTrackColor = if (isThreat) CyberRedAlert else CyberGreenPrimary,
                    uncheckedThumbColor = CyberTextDim,
                    uncheckedTrackColor = CyberDarkBgAlt
                ),
                modifier = Modifier.testTag("shield_switch")
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "OFF",
                color = if (!isShieldActive) CyberRedGlow else CyberTextDim.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
