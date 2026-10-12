package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.forensics.HackerFlagLogger
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassCard
import com.example.ui.theme.CyberGreenGlow
import com.example.ui.theme.CyberGreenPrimary
import com.example.ui.theme.CyberRedGlow
import com.example.ui.theme.CyberTextDim
import com.example.ui.theme.CyberTextWhite

@Composable
fun ForensicRecentActivityCard(
    events: List<HackerFlagLogger.ForensicSummary>,
    onViewFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CyberGlassCard.copy(alpha = 0.9f))
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("forensics_activity_card")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "📜", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Recent Activity & Hacker Forensics",
                color = CyberTextWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Chronological list matching wireframe
        if (events.isEmpty()) {
            Text(
                text = "System active. Zero intrusion anomalies detected.",
                color = CyberTextDim,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            events.take(4).forEach { event ->
                ForensicItemRow(event = event)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Direct forensic audit viewer button matching wireframe: [ 📁 View hacker_flag.txt ]
        Button(
            onClick = onViewFileClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E293B),
                contentColor = CyberTextWhite
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, CyberGlassBorder, RoundedCornerShape(12.dp))
                .testTag("view_hacker_flag_button")
        ) {
            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = "View Forensics",
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "View hacker_flag.txt",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun ForensicItemRow(event: HackerFlagLogger.ForensicSummary) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "•",
                color = CyberGreenGlow,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "[${event.timestampShort}] ${event.title}",
                color = CyberTextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif
            )
        }
        if (event.title.contains("Honey-Pot", ignoreCase = true) || event.title.contains("hacker_flag", ignoreCase = true)) {
            Row(
                modifier = Modifier.padding(start = 14.dp, top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "↳ Logged to ",
                    color = CyberTextDim,
                    fontSize = 11.sp
                )
                Text(
                    text = "hacker_flag.txt",
                    color = CyberGreenGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
