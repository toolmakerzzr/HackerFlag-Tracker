package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyanAccent
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassCard
import com.example.ui.theme.CyberGreenGlow
import com.example.ui.theme.CyberGreenPrimary
import com.example.ui.theme.CyberTextDim
import com.example.ui.theme.CyberTextWhite

@Composable
fun ThreatStatsGrid(
    linksBlocked: Int,
    apkWiped: Int,
    sandboxTraps: Int,
    batteryDrain: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CyberGlassCard.copy(alpha = 0.9f))
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("threat_stats_grid")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📊",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Live Threat Analytics Grid",
                color = CyberTextWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Row 1: Links Blocked & APK Wiped
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                icon = Icons.Default.Link,
                iconTint = CyberCyanAccent,
                label = "Links Blocked",
                value = linksBlocked.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.BugReport,
                iconTint = CyberGreenGlow,
                label = "APK Wiped",
                value = String.format("%02d", apkWiped),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 2: Sandbox Traps & Battery
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                icon = Icons.Default.Savings, // Honey pot / trap
                iconTint = Color(0xFFFBBF24),
                label = "Sandbox Traps",
                value = String.format("%02d", sandboxTraps),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.BatteryChargingFull,
                iconTint = CyberGreenPrimary,
                label = "Battery",
                value = String.format("%.1f%%", batteryDrain),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F1728))
            .border(1.dp, CyberGlassBorder.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = label,
                    color = CyberTextDim,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    color = CyberTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
