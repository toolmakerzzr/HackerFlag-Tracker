package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberGreenGlass
import com.example.ui.theme.CyberGreenGlow
import com.example.ui.theme.CyberGreenPrimary
import com.example.ui.theme.CyberRedAlert
import com.example.ui.theme.CyberRedGlass
import com.example.ui.theme.CyberRedGlow
import com.example.ui.theme.CyberTextDim

@Composable
fun StatusBanner(
    title: String,
    subtitle: String,
    isThreat: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isThreat) CyberRedGlass else CyberGreenGlass,
        animationSpec = tween(500),
        label = "banner_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isThreat) CyberRedAlert.copy(alpha = 0.5f) else CyberGreenPrimary.copy(alpha = 0.5f),
        animationSpec = tween(500),
        label = "banner_border"
    )
    val dotColor by animateColorAsState(
        targetValue = if (isThreat) CyberRedGlow else CyberGreenGlow,
        animationSpec = tween(500),
        label = "banner_dot"
    )
    val titleColor by animateColorAsState(
        targetValue = if (isThreat) CyberRedAlert else CyberGreenPrimary,
        animationSpec = tween(500),
        label = "banner_title"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .testTag("status_banner")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "[ $subtitle ]",
                color = if (isThreat) CyberRedGlow else CyberTextDim,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
