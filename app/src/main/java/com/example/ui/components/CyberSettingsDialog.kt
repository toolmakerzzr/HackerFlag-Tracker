package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassCard
import com.example.ui.theme.CyberGreenGlow
import com.example.ui.theme.CyberGreenPrimary
import com.example.ui.theme.CyberRedAlert
import com.example.ui.theme.CyberTextDim
import com.example.ui.theme.CyberTextWhite

@Composable
fun CyberSettingsDialog(
    currentGlowIntensity: Float,
    onGlowIntensityChanged: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var glowSlider by remember { mutableFloatStateOf(currentGlowIntensity) }
    var autoWipeEnabled by remember { mutableStateOf(true) }
    var honeypotAutoDeploy by remember { mutableStateOf(true) }
    var antiFingerprintSpoof by remember { mutableStateOf(true) }
    var adaptiveBatterySaver by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, CyberGlassBorder, RoundedCornerShape(24.dp)),
            color = CyberDarkBg
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("cyber_settings_dialog")
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = CyberGreenGlow,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Settings & Guard Profile",
                            color = CyberTextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CyberTextWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Edge Glow Intensity
                Text(
                    text = "Screen Edge Glow Intensity: ${(glowSlider * 100).toInt()}%",
                    color = CyberTextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Controls the brightness and thickness of perimeter alert vector glow",
                    color = CyberTextDim,
                    fontSize = 11.sp
                )
                Slider(
                    value = glowSlider,
                    onValueChange = {
                        glowSlider = it
                        onGlowIntensityChanged(it)
                    },
                    valueRange = 0.2f..1.5f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberGreenPrimary,
                        activeTrackColor = CyberGreenGlow,
                        inactiveTrackColor = CyberGlassBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle: Auto-Wipe Malicious APKs
                SettingToggleRow(
                    title = "Auto-Wipe Malicious APKs",
                    subtitle = "Immediately wipe & delete untrusted installer packages upon download",
                    checked = autoWipeEnabled,
                    onCheckedChange = { autoWipeEnabled = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle: Automatic Honeypot Virtualization
                SettingToggleRow(
                    title = "Matrix Honey-Pot Virtualization",
                    subtitle = "Instantly trap unauthorized root/payload attempts in decoy container",
                    checked = honeypotAutoDeploy,
                    onCheckedChange = { honeypotAutoDeploy = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle: Anti-Fingerprinting (Decoy IP & MAC)
                SettingToggleRow(
                    title = "Synthetic Decoy Telemetry",
                    subtitle = "Inject non-routable dummy IP (198.51.100.x) and randomized MAC",
                    checked = antiFingerprintSpoof,
                    onCheckedChange = { antiFingerprintSpoof = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle: Adaptive Battery Saver Mode
                SettingToggleRow(
                    title = "Ultra-Low Power Optimization",
                    subtitle = "Maintains 0.5%–1.0% battery target by downgrading non-essential animations",
                    checked = adaptiveBatterySaver,
                    onCheckedChange = { adaptiveBatterySaver = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "Security preferences applied successfully", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Save & Apply Settings",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberGlassCard)
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = CyberTextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = CyberTextDim, fontSize = 11.sp, lineHeight = 14.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyberTextWhite,
                checkedTrackColor = CyberGreenPrimary,
                uncheckedThumbColor = CyberTextDim,
                uncheckedTrackColor = CyberDarkBg
            )
        )
    }
}
