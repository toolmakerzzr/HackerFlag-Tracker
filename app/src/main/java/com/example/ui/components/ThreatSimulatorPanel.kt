package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.interceptor.ThreatDefenseManager
import com.example.ui.theme.CyberCyanAccent
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassCard
import com.example.ui.theme.CyberGreenGlow
import com.example.ui.theme.CyberGreenPrimary
import com.example.ui.theme.CyberRedAlert
import com.example.ui.theme.CyberRedGlow
import com.example.ui.theme.CyberTextDim
import com.example.ui.theme.CyberTextWhite
import java.io.File

@Composable
fun ThreatSimulatorPanel(
    onOpenMatrixSandbox: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var testUrlInput by remember { mutableStateOf("https://paypa1-security.xyz/verify") }
    var inspectionFeedback by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CyberGlassCard.copy(alpha = 0.9f))
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .testTag("threat_simulator_panel")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🧪", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Live Threat Simulator & Testing Lab",
                color = CyberTextWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Verify zero-lag real-time interception, auto-wipe, and Matrix honey-pot triggers.",
            color = CyberTextDim,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Quick simulation action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    ThreatDefenseManager.handleBlockedUrl(
                        context,
                        "https://paypa1-security.xyz/auth?harvest=true",
                        "Phishing URL Blocked (Chrome). Offline Bloom filter matched deceptive domain typosquatting PayPal."
                    )
                    Toast.makeText(context, "Phishing URL Intercepted! State shifted to Unsafe (Red)", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .border(1.dp, CyberCyanAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .testTag("sim_phishing_button")
            ) {
                Icon(Icons.Default.Link, contentDescription = "URL", modifier = Modifier.size(16.dp), tint = CyberCyanAccent)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Test Link", fontSize = 11.sp, color = CyberTextWhite, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    // Create simulated dummy malicious APK file in cache
                    val dummyApk = File(context.cacheDir, "trojan_patch_v3.apk")
                    dummyApk.writeText("MOCK_MALICIOUS_DEX_PAYLOAD")
                    ThreatDefenseManager.inspectAndNeutralizeApk(context, dummyApk, "com.trojan.dropper")
                    Toast.makeText(context, "Malicious APK Wiped safely & deleted!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .border(1.dp, CyberRedAlert.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .testTag("sim_apk_button")
            ) {
                Icon(Icons.Default.BugReport, contentDescription = "APK", modifier = Modifier.size(16.dp), tint = CyberRedAlert)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Test APK", fontSize = 11.sp, color = CyberTextWhite, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    ThreatDefenseManager.triggerMatrixHoneypot(context, "Remote Privilege Escalation (/system/bin/su)")
                    onOpenMatrixSandbox()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .testTag("sim_honeypot_button")
            ) {
                Icon(Icons.Default.Terminal, contentDescription = "Matrix", modifier = Modifier.size(16.dp), tint = Color(0xFFFBBF24))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Honey-Pot", fontSize = 11.sp, color = CyberTextWhite, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Custom URL Inspector Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = testUrlInput,
                onValueChange = {
                    testUrlInput = it
                    inspectionFeedback = null
                },
                placeholder = { Text("Enter URL to scan...", fontSize = 12.sp, color = CyberTextDim) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("custom_url_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberGreenGlow,
                    unfocusedBorderColor = CyberGlassBorder,
                    focusedTextColor = CyberTextWhite,
                    unfocusedTextColor = CyberTextWhite
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val result = ThreatDefenseManager.inspectUrl(testUrlInput.trim())
                    if (result.isBlocked) {
                        ThreatDefenseManager.handleBlockedUrl(context, testUrlInput.trim(), result.reason)
                        inspectionFeedback = "🚫 BLOCKED (${result.tier}): ${result.reason}"
                    } else {
                        inspectionFeedback = "✅ CLEAN: Domain passed Tier 1 Bloom & Heuristic rules."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberGreenPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(50.dp)
                    .testTag("scan_url_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Scan", tint = Color.Black, modifier = Modifier.size(20.dp))
            }
        }

        if (inspectionFeedback != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F172A))
                    .padding(8.dp)
            ) {
                Text(
                    text = inspectionFeedback ?: "",
                    color = if (inspectionFeedback?.startsWith("🚫") == true) CyberRedGlow else CyberGreenGlow,
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
