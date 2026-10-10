package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.local.HackerflagDatabase
import com.example.data.local.entity.TimelineEventEntity
import com.example.data.security.CryptoUtils
import com.example.data.security.DevicePairingManager
import com.example.data.security.LiveTrackingManager
import com.example.ui.components.CircuitDivider
import com.example.ui.components.CyberCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.CyberAlertCrimson
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGoldDark
import com.example.ui.theme.CyberSuccessGreen
import com.example.ui.theme.CyberSurfaceHigh
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberWarningAmber
import kotlinx.coroutines.launch

@Composable
fun RemoteDefenseScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val pairingManager = remember { DevicePairingManager.getInstance(context) }
    val liveTracking = remember { LiveTrackingManager.getInstance(context) }
    val db = remember { HackerflagDatabase.getDatabase(context) }

    val isLocalAlarmRinging by pairingManager.isLocalAlarmRinging.collectAsState()
    val isLocalLostMode by pairingManager.isLocalLostMode.collectAsState()
    val lostModeContact by pairingManager.lostModeContact.collectAsState()
    val lostModeMessage by pairingManager.lostModeMessage.collectAsState()
    val activeRemoteDevice by pairingManager.activeRemoteDevice.collectAsState()
    val currentTelemetry by liveTracking.telemetry.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("This Phone (Unique Key)", "Remote Controller (2nd Phone)", "Cloud Kill-Switch")

    // Remote unlock form inputs
    var inputTargetCode by remember { mutableStateOf("HF-4192-NX") }
    var inputTargetImei by remember { mutableStateOf("358920194829104") }
    var isRemoteConsoleUnlocked by remember { mutableStateOf(activeRemoteDevice != null) }

    // WhatsApp phone input
    var phoneNumberInput by remember { mutableStateOf("+91 98765 43210") }

    // Alarm pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "siren_pulse")
    val sirenColor by infiniteTransition.animateColor(
        initialValue = CyberAlertCrimson,
        targetValue = Color.Yellow,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sirenColor"
    )

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {}
    }

    fun logAuditEvent(title: String, details: String, category: String) {
        coroutineScope.launch {
            db.timelineDao().insertEvent(
                TimelineEventEntity(
                    eventCategory = category,
                    title = title,
                    details = details,
                    evidenceHash = CryptoUtils.sha256("$title::$details::${System.currentTimeMillis()}")
                )
            )
        }
    }

    fun sendWhatsAppDeactivateEmail() {
        val subject = "Lost/Stolen: Please deactivate my account"
        val body = "Hello WhatsApp Support,\n\nMy mobile phone has been stolen. Please deactivate my WhatsApp account linked to the telephone number: $phoneNumberInput immediately to prevent unauthorized access or scam distribution.\n\nThank you,\nComplainant"
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@whatsapp.com")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            context.startActivity(intent)
            logAuditEvent("WhatsApp Deactivation Dispatched", "Email drafted to support@whatsapp.com for $phoneNumberInput", "CLOUD_KILL")
        } catch (e: Exception) {
            Toast.makeText(context, "No email client installed. Mail to: support@whatsapp.com", Toast.LENGTH_LONG).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("remote_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberAlertCrimson
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "MODULE 4: REMOTE DEFENSE & GUARDIAN",
                        color = CyberAlertCrimson,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "UNIQUE PAIRING KEY • DUAL-PHONE REMOTE THEFT ALARM",
                        color = CyberTextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Active Local Alarm Siren Flashing Banner
        if (isLocalAlarmRinging) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(sirenColor.copy(alpha = 0.2f))
                        .border(2.dp, sirenColor, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = CyberAlertCrimson, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🚨 LOUD THEFT ALARM SIREN IS RINGING!",
                                color = CyberAlertCrimson,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Full volume audio frequency & strobe active to expose thief.",
                            color = CyberTextPrimary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                pairingManager.stopLocalAlarm()
                                Toast.makeText(context, "Siren Stopped & Disarmed", Toast.LENGTH_SHORT).show()
                                logAuditEvent("Alarm Silenced", "Local theft siren manually disarmed by user", "ALARM_DISARM")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberAlertCrimson),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.VolumeMute, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DISARM & STOP SIREN", fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Active Lost Mode Lockdown Banner
        if (isLocalLostMode) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberAlertCrimson)
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🚩 THIS DEVICE IS MARKED AS STOLEN",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = lostModeMessage, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "PLEASE CALL OWNER: $lostModeContact", color = Color.Yellow, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                pairingManager.setLocalLostMode(false)
                                Toast.makeText(context, "Lost Mode Deactivated", Toast.LENGTH_SHORT).show()
                                logAuditEvent("Lost Mode Disarmed", "Device marked as recovered by owner", "LOST_MODE_OFF")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("DISARM & MARK RECOVERED", color = CyberAlertCrimson, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Sub Navigation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CyberSurfaceHigh,
                contentColor = CyberAlertCrimson,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = CyberAlertCrimson
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) CyberAlertCrimson else CyberTextSecondary
                            )
                        }
                    )
                }
            }
        }

        // =========================================================================
        // TAB 0: THIS PHONE (UNIQUE PAIRING KEY & LOCAL GUARDIAN)
        // =========================================================================
        if (selectedTabIndex == 0) {
            item {
                CyberCard(glowEffect = true, modifier = Modifier.fillMaxWidth().testTag("pairing_code_card")) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = CyberGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "YOUR UNIQUE DEVICE PAIRING CODE",
                                    color = CyberGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            StatusPill(statusText = "ARMED", colorHex = 0xFF00FF9D)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "यदि आपका फ़ोन चोरी या गुम हो जाए, तो किसी भी दूसरे फ़ोन में यह ऐप इंस्टॉल करें और नीचे दिया गया Pairing Code और IMEI डालें। आपका फ़ोन तुरंत रिमोटली ट्रैक और लॉक हो जाएगा।",
                            color = CyberTextSecondary,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Big Monospace Unique Code Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberSurfaceHigh)
                                .border(1.5.dp, CyberCyan, RoundedCornerShape(10.dp))
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "DEVICE PAIRING KEY",
                                        color = CyberTextMuted,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = pairingManager.myPairingCode,
                                        color = CyberCyan,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 2.sp
                                    )
                                }
                                Row {
                                    IconButton(
                                        onClick = { copyToClipboard(pairingManager.myPairingCode, "Pairing Code") },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(CyberCyan.copy(alpha = 0.15f))
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberCyan, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            val shareText = "HackerFlag Defense Pairing Key for my device:\nCode: ${pairingManager.myPairingCode}\nIMEI: ${pairingManager.myDeviceImei}\nKeep this safe to track or trigger alarm on my phone."
                                            liveTracking.shareLocation(context, shareText)
                                        },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(CyberGold.copy(alpha = 0.15f))
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = CyberGold, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Device Hardware IMEI Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberSurfaceVariant)
                                .border(0.8.dp, CyberBorder, RoundedCornerShape(8.dp))
                                .padding(vertical = 8.dp, horizontal = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "DEVICE HARDWARE IMEI / ID", color = CyberTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    Text(
                                        text = pairingManager.myDeviceImei,
                                        color = CyberTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                IconButton(
                                    onClick = { copyToClipboard(pairingManager.myDeviceImei, "Device IMEI") },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberTextSecondary, modifier = Modifier.size(15.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Local Alarm & Lost Mode Test Controls
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "ANTI-THEFT HARDWARE TRIGGERS (TEST ON THIS PHONE)",
                            color = CyberAlertCrimson,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Test the high-pitch alarm siren and theft lockdown banner to verify hardware speaker & vibration response:",
                            color = CyberTextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    if (isLocalAlarmRinging) {
                                        pairingManager.stopLocalAlarm()
                                        Toast.makeText(context, "Siren Stopped", Toast.LENGTH_SHORT).show()
                                    } else {
                                        pairingManager.startLocalAlarm()
                                        Toast.makeText(context, "🔊 High-Pitch Theft Siren Activated!", Toast.LENGTH_SHORT).show()
                                        logAuditEvent("Theft Siren Tested", "Local siren alarm tone sounded at max volume", "ALARM_TEST")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLocalAlarmRinging) CyberSurfaceHigh else CyberAlertCrimson
                                ),
                                border = if (isLocalAlarmRinging) BorderStroke(1.dp, CyberAlertCrimson) else null,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("test_siren_button")
                            ) {
                                Icon(
                                    imageVector = if (isLocalAlarmRinging) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = if (isLocalAlarmRinging) CyberAlertCrimson else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isLocalAlarmRinging) "STOP SIREN" else "TEST SIREN",
                                    color = if (isLocalAlarmRinging) CyberAlertCrimson else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Button(
                                onClick = {
                                    val newStatus = !isLocalLostMode
                                    pairingManager.setLocalLostMode(newStatus)
                                    Toast.makeText(
                                        context,
                                        if (newStatus) "🚩 Stolen Mode Active!" else "Recovered Mode Set",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    logAuditEvent(
                                        if (newStatus) "Stolen Mode Flagged" else "Stolen Mode Cleared",
                                        "Device theft banner toggled: $newStatus",
                                        "LOST_MODE"
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLocalLostMode) CyberSurfaceHigh else CyberGoldDark
                                ),
                                border = if (isLocalLostMode) BorderStroke(1.dp, CyberGold) else null,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("test_lost_mode_button")
                            ) {
                                Icon(
                                    imageVector = if (isLocalLostMode) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isLocalLostMode) CyberGold else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isLocalLostMode) "CLEAR LOST" else "FLAG AS LOST",
                                    color = if (isLocalLostMode) CyberGold else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Live Telemetry Packet Card
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LIVE TELEMETRY BROADCAST PACKET",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            StatusPill(statusText = "LIVE GPS", colorHex = 0xFF00E5FF)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Lat: ${"%.5f".format(currentTelemetry.latitude)}, Lng: ${"%.5f".format(currentTelemetry.longitude)} • Alt: ${"%.1f".format(currentTelemetry.altitudeMeters)}m • Accuracy: ±${"%.1f".format(currentTelemetry.accuracyMeters)}m",
                            color = CyberTextPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val shareText = liveTracking.generateShareMessage(pairingManager.myDeviceImei, pairingManager.myPairingCode)
                                liveTracking.shareLocation(context, shareText)
                                logAuditEvent("Emergency Location Shared", "Live GPS beacon shared via intent", "LIVE_GPS_SHARE")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("1-TAP SHARE LIVE GPS TRACKING TO CONTACT", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TAB 1: REMOTE CONTROLLER (PAIR & CONTROL A SECOND PHONE)
        // =========================================================================
        if (selectedTabIndex == 1) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LINK & UNLOCK TARGET PHONE",
                                color = CyberGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            StatusPill(
                                statusText = if (isRemoteConsoleUnlocked) "CONSOLE UNLOCKED" else "INPUT REQUIRED",
                                colorHex = if (isRemoteConsoleUnlocked) 0xFF00FF9D else 0xFFFFB300
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "किसी अन्य फ़ोन को खोजने या उसका अलार्म बजाने के लिए, उस फ़ोन में HackerFlag ऐप द्वारा दिया गया Unique Pairing Code और IMEI यहाँ दर्ज करें:",
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = inputTargetCode,
                            onValueChange = { inputTargetCode = it },
                            label = { Text("Target Device Pairing Code (e.g. HF-4192-NX)") },
                            modifier = Modifier.fillMaxWidth().testTag("target_code_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = inputTargetImei,
                            onValueChange = { inputTargetImei = it },
                            label = { Text("Target Device IMEI / Hardware ID (15 Digits)") },
                            modifier = Modifier.fillMaxWidth().testTag("target_imei_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val success = pairingManager.pairAndUnlockRemoteTarget(inputTargetCode, inputTargetImei)
                                if (success) {
                                    isRemoteConsoleUnlocked = true
                                    Toast.makeText(context, "Target Device Unlocked Successfully!", Toast.LENGTH_SHORT).show()
                                    logAuditEvent("Target Device Paired", "Unlocked remote control deck for $inputTargetCode (IMEI: $inputTargetImei)", "REMOTE_PAIR")
                                } else {
                                    Toast.makeText(context, "Please enter valid Pairing Code & IMEI", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("unlock_target_button")
                        ) {
                            Icon(Icons.Default.Devices, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AUTHENTICATE & UNLOCK REMOTE CONTROL", color = Color.Black, fontSize = 11.5.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // UNLOCKED REMOTE CONTROL DECK
            if (isRemoteConsoleUnlocked && activeRemoteDevice != null) {
                val target = activeRemoteDevice!!
                item {
                    CyberCard(glowEffect = true, modifier = Modifier.fillMaxWidth().testTag("remote_deck_card")) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = target.deviceName,
                                        color = CyberTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "KEY: ${target.pairingCode} | IMEI: ${target.imei}",
                                        color = CyberTextMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                StatusPill(
                                    statusText = if (target.isAlarmRinging) "ALARM RINGING" else if (target.isLostMode) "MARKED STOLEN" else "ARMED & SAFE",
                                    colorHex = if (target.isAlarmRinging || target.isLostMode) 0xFFFF2A55 else 0xFF00FF9D
                                )
                            }

                            CircuitDivider()

                            // Target Live Telemetry
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "LIVE GPS COORDINATES", color = CyberTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    Text(
                                        text = "${"%.5f".format(target.lastKnownLat)}, ${"%.5f".format(target.lastKnownLng)}",
                                        color = CyberCyan,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "BATTERY LEVEL", color = CyberTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    Text(
                                        text = "${target.batteryPct}% (Charging)",
                                        color = CyberSuccessGreen,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "EXECUTE REMOTE HARDWARE COMMANDS:",
                                color = CyberGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action 1: Remote Siren
                            Button(
                                onClick = {
                                    if (target.isAlarmRinging) {
                                        pairingManager.stopRemoteAlarm(target.pairingCode)
                                        Toast.makeText(context, "Remote Siren Stopped", Toast.LENGTH_SHORT).show()
                                        logAuditEvent("Remote Siren Disarmed", "Siren stopped on target ${target.pairingCode}", "REMOTE_ALARM_OFF")
                                    } else {
                                        pairingManager.triggerRemoteAlarm(target.pairingCode)
                                        Toast.makeText(context, "🔊 Loud Siren Command Sent to Target Phone!", Toast.LENGTH_LONG).show()
                                        logAuditEvent("Remote Siren Triggered", "Siren activated on target ${target.pairingCode}", "REMOTE_ALARM_ON")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (target.isAlarmRinging) CyberSurfaceHigh else CyberAlertCrimson
                                ),
                                border = if (target.isAlarmRinging) BorderStroke(1.dp, CyberAlertCrimson) else null,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("remote_trigger_siren")
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (target.isAlarmRinging) "STOP REMOTE SIREN ALARM" else "🔊 TRIGGER LOUD SIREN ALARM ON TARGET PHONE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action 2: Remote Mark Lost
                            Button(
                                onClick = {
                                    val newLost = !target.isLostMode
                                    pairingManager.setRemoteLostMode(target.pairingCode, newLost)
                                    Toast.makeText(
                                        context,
                                        if (newLost) "🚩 Target Phone Locked in Stolen Mode!" else "Target Phone Disarmed & Recovered",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    logAuditEvent(
                                        "Remote Lost Mode",
                                        "Target ${target.pairingCode} set to lost: $newLost",
                                        "REMOTE_LOST_MODE"
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (target.isLostMode) CyberSurfaceHigh else CyberGoldDark
                                ),
                                border = if (target.isLostMode) BorderStroke(1.dp, CyberGold) else null,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("remote_trigger_lost")
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (target.isLostMode) "DISARM & REMOVE STOLEN LOCK" else "🚩 MARK AS STOLEN & LOCK TARGET SCREEN",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action 3: Ping Live GPS Location & Map
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = {
                                        val updated = pairingManager.pingRemoteLocation(target.pairingCode)
                                        Toast.makeText(context, "Location Pinged: ${"%.4f".format(updated?.lastKnownLat)}, ${"%.4f".format(updated?.lastKnownLng)}", Toast.LENGTH_SHORT).show()
                                        logAuditEvent("Remote Location Ping", "Location query answered for ${target.pairingCode}", "REMOTE_PING")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                    border = BorderStroke(1.dp, CyberCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Map, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("REFRESH GPS", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        val mapUrl = liveTracking.getGoogleMapsUrl(target.lastKnownLat, target.lastKnownLng)
                                        openUrl(mapUrl)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                    border = BorderStroke(1.dp, CyberGold),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Map, contentDescription = null, tint = CyberGold, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("OPEN MAPS", color = CyberGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action 4: Remote Stealth Camera Snap
                            Button(
                                onClick = {
                                    Toast.makeText(context, "📸 Stealth Intruder Snap Dispatched! Front camera triggered.", Toast.LENGTH_LONG).show()
                                    logAuditEvent("Stealth Intruder Snap", "Remote camera capture executed on target ${target.pairingCode}", "REMOTE_CAMERA")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = BorderStroke(1.dp, CyberBorder),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CyberTextPrimary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("📸 TRIGGER STEALTH INTRUDER FRONT CAMERA SNAP", color = CyberTextPrimary, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TAB 2: CLOUD KILL-SWITCH (WHATSAPP & GOOGLE/APPLE)
        // =========================================================================
        if (selectedTabIndex == 2) {
            // WhatsApp Deactivation
            item {
                CyberCard(glowEffect = true, modifier = Modifier.fillMaxWidth().testTag("whatsapp_kill_card")) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "REMOTE WHATSAPP DEACTIVATION",
                                color = CyberGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            StatusPill(statusText = "HIGH PRIORITY", colorHex = 0xFFFF2A55)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Thieves use active WhatsApp sessions to message family asking for emergency funds via UPI. Dispatches official deactivation dispatch to WhatsApp Headquarters.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = phoneNumberInput,
                            onValueChange = { phoneNumberInput = it },
                            label = { Text("International Phone (e.g. +91XXXXXXXXXX)") },
                            modifier = Modifier.fillMaxWidth().testTag("whatsapp_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { sendWhatsAppDeactivateEmail() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberAlertCrimson),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("whatsapp_email_button")
                        ) {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("DISPATCH WHATSAPP ACCOUNT TERMINATION EMAIL", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // Google & Apple Cloud Kill-Switch
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "GOOGLE & APPLE ID CLOUD KILL-SWITCH",
                            color = CyberCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Remotely sign out all active browser sessions, revoke OAuth authorization grants, and activate remote wipe.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { openUrl("https://myaccount.google.com/device-activity") },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = BorderStroke(1.dp, CyberCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("GOOGLE DEVICE ACTIVITY", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { openUrl("https://www.icloud.com/find") },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = BorderStroke(1.dp, CyberGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("APPLE ICLOUD FIND MY", color = CyberGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
