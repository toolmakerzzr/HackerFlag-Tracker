package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SimCardAlert
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HackerflagDatabase
import com.example.data.local.entity.StolenDeviceEntity
import com.example.data.local.entity.TimelineEventEntity
import com.example.data.model.CellTower
import com.example.data.security.CryptoUtils
import com.example.data.security.DevicePairingManager
import com.example.data.security.LiveTrackingManager
import com.example.ui.components.CircuitDivider
import com.example.ui.components.CyberCard
import com.example.ui.components.RadarCanvas
import com.example.ui.components.RiskScoreDial
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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ImeiRadarScreen(
    onNavigateBack: () -> Unit,
    initialTabIndex: Int = 0,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = remember { HackerflagDatabase.getDatabase(context) }
    val allStolenDevices by db.stolenDeviceDao().getAllDevices().collectAsState(initial = emptyList())

    val liveTracking = remember { LiveTrackingManager.getInstance(context) }
    val pairingManager = remember { DevicePairingManager.getInstance(context) }

    val currentTelemetry by liveTracking.telemetry.collectAsState()
    val breadcrumbs by liveTracking.breadcrumbs.collectAsState()
    val geofences by liveTracking.geofences.collectAsState()
    val detectedSignals by liveTracking.detectedSignals.collectAsState()
    val isScanningSignals by liveTracking.isScanningSignals.collectAsState()

    var selectedTabIndex by remember(initialTabIndex) { mutableIntStateOf(initialTabIndex) }
    val tabTitles = listOf(
        "Live GPS Tracking",
        "Fences & Detectors",
        "Cell Triangulation",
        "CEIR Portal",
        "ZK Hasher",
        "Public Registry",
        "SIM & Risk"
    )

    // Cell towers for Triangulation
    val towers = remember {
        listOf(
            CellTower(
                id = "TWR-DL-401",
                name = "Airtel eNodeB Connaught Sector 4",
                lac = "2104",
                cid = "48921",
                signalDbm = -68,
                distanceMeters = 420,
                azimuthDegrees = 45f,
                operator = "Airtel 5G",
                isPrimaryServing = true
            ),
            CellTower(
                id = "TWR-DL-402",
                name = "Jio Tower Barakhamba Road",
                lac = "2104",
                cid = "50119",
                signalDbm = -75,
                distanceMeters = 810,
                azimuthDegrees = 160f,
                operator = "Jio True 5G"
            ),
            CellTower(
                id = "TWR-DL-403",
                name = "Vodafone Idea Shivaji Stadium Hub",
                lac = "2104",
                cid = "39812",
                signalDbm = -82,
                distanceMeters = 1150,
                azimuthDegrees = 290f,
                operator = "Vi LTE"
            )
        )
    }

    // Hasher State
    var imeiInput by remember { mutableStateOf(pairingManager.myDeviceImei) }
    val imeiProof = remember(imeiInput) { CryptoUtils.generateZkImeiProof(imeiInput) }

    // Registry Search State
    var searchQuery by remember { mutableStateOf("") }
    val filteredDevices = remember(searchQuery, allStolenDevices) {
        if (searchQuery.isBlank()) allStolenDevices
        else allStolenDevices.filter {
            it.imei.contains(searchQuery.trim(), ignoreCase = true) ||
            it.imeiSha256.contains(searchQuery.trim(), ignoreCase = true) ||
            it.brandModel.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    // CEIR Form State
    var ceirImei1 by remember { mutableStateOf(pairingManager.myDeviceImei) }
    var ceirImei2 by remember { mutableStateOf("358920194829104") }
    var ceirBrand by remember { mutableStateOf("Samsung Galaxy S24 Ultra") }
    var ceirComplaintNo by remember { mutableStateOf("FIR/DEL/2026/0842") }

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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Back Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("radar_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberCyan
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "MODULE 1: RADAR & LIVE TRACKING",
                        color = CyberCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "LIVE GPS • GEOFENCE DETECTORS • CEIR BLACKLIST",
                        color = CyberTextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Sub Navigation Tabs (Scrollable)
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CyberSurfaceHigh,
                contentColor = CyberCyan,
                edgePadding = 8.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = CyberCyan
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
                                color = if (selectedTabIndex == index) CyberCyan else CyberTextSecondary
                            )
                        }
                    )
                }
            }
        }

        // =========================================================================
        // TAB 0: LIVE GPS PHONE TRACKING SYSTEM
        // =========================================================================
        if (selectedTabIndex == 0) {
            item {
                CyberCard(glowEffect = true, modifier = Modifier.fillMaxWidth().testTag("live_gps_telemetry_card")) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.GpsFixed, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE SATELLITE GPS TELEMETRY",
                                    color = CyberGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            StatusPill(statusText = "LIVE 1.0Hz", colorHex = 0xFF00FF9D)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Large Monospace Coordinates Display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberSurfaceHigh)
                                .border(1.2.dp, CyberCyan, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = "LATITUDE & LONGITUDE", color = CyberTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        Text(
                                            text = "${"%.5f".format(currentTelemetry.latitude)}° N, ${"%.5f".format(currentTelemetry.longitude)}° E",
                                            color = CyberCyan,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = "ACCURACY", color = CyberTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        Text(
                                            text = "±${"%.1f".format(currentTelemetry.accuracyMeters)}m",
                                            color = CyberSuccessGreen,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Speed: ${"%.1f".format(currentTelemetry.speedKmh)} km/h • Alt: ${"%.1f".format(currentTelemetry.altitudeMeters)}m",
                                        color = CyberTextSecondary,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Heading: ${currentTelemetry.bearingDegrees.toInt()}°",
                                        color = CyberGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Animated Radar Canvas visualizer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberBackground)
                                .border(1.dp, CyberBorder, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            LiveGpsRadarView(bearingDegrees = currentTelemetry.bearingDegrees)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Instant Live Location Sharing Button
                        Button(
                            onClick = {
                                val shareText = liveTracking.generateShareMessage(pairingManager.myDeviceImei, pairingManager.myPairingCode)
                                liveTracking.shareLocation(context, shareText)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("share_live_location_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SHARE LIVE LOCATION LINK (WHATSAPP / SMS)", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    val mapUrl = liveTracking.getGoogleMapsUrl(currentTelemetry.latitude, currentTelemetry.longitude)
                                    openUrl(mapUrl)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("OPEN IN MAPS", color = CyberCyan, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val mapUrl = liveTracking.getGoogleMapsUrl(currentTelemetry.latitude, currentTelemetry.longitude)
                                    copyToClipboard(mapUrl, "Google Maps Link")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = CyberGold, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("COPY MAP LINK", color = CyberGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Live Breadcrumb Movement Trail
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LIVE BREADCRUMB MOVEMENT TRAIL",
                                color = CyberGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${breadcrumbs.size} FIXES",
                                color = CyberTextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        breadcrumbs.reversed().take(5).forEach { point ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(CyberCyan)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${"%.4f".format(point.latitude)}, ${"%.4f".format(point.longitude)}",
                                        color = CyberTextPrimary,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "${point.formattedTime} • Batt ${point.batteryPct}%",
                                    color = CyberTextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        CircuitDivider()

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    db.timelineDao().insertEvent(
                                        TimelineEventEntity(
                                            eventCategory = "LIVE_GPS_PING",
                                            title = "High-Accuracy GPS Telemetry Locked",
                                            details = "Lat: ${"%.5f".format(currentTelemetry.latitude)}, Lng: ${"%.5f".format(currentTelemetry.longitude)}, Speed: ${currentTelemetry.speedKmh} km/h, Accuracy: ±${currentTelemetry.accuracyMeters}m.",
                                            evidenceHash = CryptoUtils.sha256("GPS_${System.currentTimeMillis()}")
                                        )
                                    )
                                }
                                Toast.makeText(context, "Recorded GPS Telemetry to Immutable Legal Timeline", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("record_gps_ping_button")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RECORD TELEMETRY IN LEGAL EVIDENTIARY LOG", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TAB 1: SURROUNDING FENCES & DETECTORS SCANNER
        // =========================================================================
        if (selectedTabIndex == 1) {
            // Virtual Geofence Sentinels
            item {
                CyberCard(glowEffect = true, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SURROUNDING VIRTUAL GEOFENCES",
                                color = CyberGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            StatusPill(statusText = "SENTINEL ACTIVE", colorHex = 0xFF00FF9D)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "जब फ़ोन सुरक्षित परिधि (Safe Fence) से बाहर जाता है, तो थेफ्ट अलार्म और सुरक्षा अलर्ट स्वचालित रूप से सक्रिय हो जाते हैं।",
                            color = CyberTextSecondary,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        geofences.forEach { fence ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberSurfaceHigh)
                                    .border(1.dp, if (fence.isInside) CyberSuccessGreen.copy(alpha = 0.5f) else CyberAlertCrimson.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = fence.name,
                                        color = CyberTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Radius: ${fence.radiusMeters.toInt()}m | Current Distance: ~${fence.currentDistanceMeters.toInt()}m",
                                        color = CyberTextMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                StatusPill(
                                    statusText = if (fence.isInside) "INSIDE SAFE ZONE" else "OUTSIDE ZONE",
                                    colorHex = if (fence.isInside) 0xFF00FF9D else 0xFFFF2A55
                                )
                            }
                        }
                    }
                }
            }

            // Wireless Detectors Scanner (WiFi, Bluetooth & Rogue Signals)
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SURROUNDING SIGNALS & DETECTORS SCANNER",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            if (isScanningSignals) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberCyan, strokeWidth = 2.dp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "आसपास के वायरलेस फैंसस, बीकन (BLE), वाई-फ़ाई हॉटस्पॉट्स और संदिग्ध IMSI Catcher / जैमर फ़्रीक्वेंसी का रियल-टाइम स्कैन:",
                            color = CyberTextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        detectedSignals.forEach { signal ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (signal.type) {
                                            "WIFI" -> Icons.Default.Wifi
                                            "BLUETOOTH" -> Icons.Default.Bluetooth
                                            else -> Icons.Default.Radar
                                        },
                                        contentDescription = null,
                                        tint = if (signal.threatLevel == "SAFE") CyberCyan else CyberWarningAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = signal.name, color = CyberTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "${signal.identifier} • ${signal.securityProtocol}", color = CyberTextMuted, fontSize = 9.5.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                                Text(
                                    text = "${signal.signalDbm} dBm",
                                    color = if (signal.signalDbm > -60) CyberSuccessGreen else CyberWarningAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        CircuitDivider()

                        Button(
                            onClick = {
                                liveTracking.rescanDetectors()
                                coroutineScope.launch {
                                    db.timelineDao().insertEvent(
                                        TimelineEventEntity(
                                            eventCategory = "GEOFENCE_SCAN",
                                            title = "Surrounding Fences & Detectors Scanned",
                                            details = "RF sweep complete. 5 surrounding nodes catalogued without anomalies.",
                                            evidenceHash = CryptoUtils.sha256("RF_SCAN_${System.currentTimeMillis()}")
                                        )
                                    )
                                }
                                Toast.makeText(context, "Surrounding Detectors Rescanned!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("rescan_detectors_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RESCAN SURROUNDING SIGNALS & DETECTORS", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TAB 2: CELL TRIANGULATION & GIS RADAR
        // =========================================================================
        if (selectedTabIndex == 2) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CELL-TOWER TRIANGULATION RADAR",
                                color = CyberGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            StatusPill(statusText = "LIVE SIGNAL", colorHex = 0xFF00FF9D)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        RadarCanvas(cellTowers = towers, uncertaintyRadiusMeters = 120)
                    }
                }
            }

            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "CONNECTED TOWER TELEMETRY (3 ACTIVE SECTORS)",
                            color = CyberTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        towers.forEach { tower ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tower.name,
                                        color = if (tower.isPrimaryServing) CyberCyan else CyberTextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "CID: ${tower.cid} | LAC: ${tower.lac} | MCC: ${tower.mcc} MNC: ${tower.mnc}",
                                        color = CyberTextMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = "${tower.signalDbm} dBm (~${tower.distanceMeters}m)",
                                    color = if (tower.signalDbm > -70) CyberSuccessGreen else CyberWarningAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TAB 3: CEIR / SANCHAR SAATHI PORTAL
        // =========================================================================
        if (selectedTabIndex == 3) {
            item {
                CyberCard(glowEffect = true, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "CEIR / SANCHAR SAATHI BLACKLIST SUITE",
                            color = CyberGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Official Government of India portal to block stolen IMEI across all Indian telco networks.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = ceirImei1,
                            onValueChange = { ceirImei1 = it },
                            label = { Text("IMEI Slot 1 (15 digits)") },
                            modifier = Modifier.fillMaxWidth().testTag("ceir_imei1_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = ceirImei2,
                            onValueChange = { ceirImei2 = it },
                            label = { Text("IMEI Slot 2 (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = ceirBrand,
                            onValueChange = { ceirBrand = it },
                            label = { Text("Device Model / Brand") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = ceirComplaintNo,
                            onValueChange = { ceirComplaintNo = it },
                            label = { Text("Police Complaint / FIR Ack Number") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val payload = "CEIR_BLOCK_REQUEST\nIMEI1: $ceirImei1\nIMEI2: $ceirImei2\nMODEL: $ceirBrand\nFIR: $ceirComplaintNo\nTIMESTAMP: ${System.currentTimeMillis()}"
                                copyToClipboard(payload, "CEIR Payload")
                                openUrl("https://www.ceir.gov.in/Request/CeirUserReqtForm.jsp")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("ceir_submit_portal_button")
                        ) {
                            Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, tint = CyberBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("COPY PAYLOAD & OPEN CEIR PORTAL", color = CyberBackground, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TAB 4: ZERO-KNOWLEDGE HASHER
        // =========================================================================
        if (selectedTabIndex == 4) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "ZERO-KNOWLEDGE SHA-256 IMEI HASHER",
                            color = CyberCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Never transmit raw IMEIs over untrusted channels. Compute verifiable cryptographic commitments client-side.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = imeiInput,
                            onValueChange = { imeiInput = it },
                            label = { Text("Enter 15-digit IMEI") },
                            modifier = Modifier.fillMaxWidth().testTag("zk_imei_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberSurfaceHigh)
                                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(text = "CLIENT-SIDE PRIVACY PROOF:", color = CyberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Masked IMEI: ${imeiProof.rawImeiMasked}", color = CyberTextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "SHA-256 Digest:", color = CyberTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            Text(text = imeiProof.imeiSha256, color = CyberCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace, lineHeight = 14.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "ZK Commitment (Salted):", color = CyberTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            Text(text = imeiProof.zeroKnowledgeCommitment, color = CyberGold, fontSize = 11.sp, fontFamily = FontFamily.Monospace, lineHeight = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { copyToClipboard(imeiProof.imeiSha256, "IMEI SHA-256 Hash") },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("COPY HASH", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TAB 5: PUBLIC STOLEN DEVICE REGISTRY
        // =========================================================================
        if (selectedTabIndex == 5) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "PUBLIC STOLEN DEVICE REGISTRY",
                            color = CyberGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Global verification database for secondhand phone buyers and law enforcement.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search by IMEI, SHA-256 Hash, or Model") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = CyberCyan) },
                            modifier = Modifier.fillMaxWidth().testTag("registry_search_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                    }
                }
            }

            items(filteredDevices) { device ->
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = device.brandModel,
                                color = CyberTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val (statusColor, statusLabel) = when (device.status) {
                                "CEIR_BLOCKED" -> 0xFFFF9900 to "CEIR BLOCKED"
                                "POLICE_TRACED" -> 0xFF00F0FF to "POLICE TRACED"
                                "RECOVERED" -> 0xFF00FF9D to "RECOVERED"
                                else -> 0xFFFF2A55 to "FLAGGED STOLEN"
                            }
                            StatusPill(statusText = statusLabel, colorHex = statusColor)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "IMEI: ${CryptoUtils.maskImei(device.imei)} | FIR: ${device.firNumber ?: "None"}",
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Police PS: ${device.policeStation ?: "N/A"}",
                            color = CyberTextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SHA-256: ${device.imeiSha256.take(20)}...",
                            color = CyberGoldDark,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // =========================================================================
        // TAB 6: SIM & RISK ENGINE
        // =========================================================================
        if (selectedTabIndex == 6) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.SimCardAlert, contentDescription = null, tint = CyberWarningAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SIM SWAP & UNPLUG DETECTION ENGINE",
                                color = CyberWarningAmber,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Monitors carrier IMSI changes and sudden tower sector disconnections.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberSurfaceHigh)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("SIM Slot 1 State:", color = CyberTextMuted, fontSize = 11.sp)
                                Text("Airtel 5G (IMSI: 40445***)", color = CyberTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            StatusPill(statusText = "VERIFIED", colorHex = 0xFF00FF9D)
                        }
                    }
                }
            }

            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "LIVE DEVICE RISK SCORING ENGINE",
                            color = CyberGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RiskScoreDial(score = 82)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = "CURRENT THREAT LEVEL: HIGH", color = CyberAlertCrimson, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Factors evaluated:", color = CyberTextMuted, fontSize = 11.sp)
                                Text(text = "• Elapsed Time: 2h 15m (+30 pts)", color = CyberTextSecondary, fontSize = 11.sp)
                                Text(text = "• Active Banking Linkages (+25 pts)", color = CyberTextSecondary, fontSize = 11.sp)
                                Text(text = "• SIM Active in Transit (+27 pts)", color = CyberTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

/**
 * Animated Live GPS Compass / Radar Canvas with target crosshair and rotating scanner line
 */
@Composable
private fun LiveGpsRadarView(bearingDegrees: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "gps_radar_sweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = minOf(size.width, size.height) / 2.2f

        // 3 Concentric distance rings
        drawCircle(color = CyberBorder, radius = maxRadius * 0.33f, center = center, style = Stroke(width = 1f))
        drawCircle(color = CyberBorder, radius = maxRadius * 0.66f, center = center, style = Stroke(width = 1f))
        drawCircle(color = CyberCyan.copy(alpha = 0.4f), radius = maxRadius, center = center, style = Stroke(width = 1.5f))

        // Crosshairs
        drawLine(color = CyberBorder.copy(alpha = 0.5f), start = Offset(center.x - maxRadius, center.y), end = Offset(center.x + maxRadius, center.y), strokeWidth = 1f)
        drawLine(color = CyberBorder.copy(alpha = 0.5f), start = Offset(center.x, center.y - maxRadius), end = Offset(center.x, center.y + maxRadius), strokeWidth = 1f)

        // Rotating sweep line
        val sweepRad = Math.toRadians(sweepAngle.toDouble())
        val sweepEnd = Offset(
            (center.x + maxRadius * cos(sweepRad)).toFloat(),
            (center.y + maxRadius * sin(sweepRad)).toFloat()
        )
        drawLine(color = CyberCyan, start = center, end = sweepEnd, strokeWidth = 2f)

        // Bearing pointer arrow (phone direction)
        val bearingRad = Math.toRadians((bearingDegrees - 90).toDouble())
        val bearingEnd = Offset(
            (center.x + (maxRadius * 0.85f) * cos(bearingRad)).toFloat(),
            (center.y + (maxRadius * 0.85f) * sin(bearingRad)).toFloat()
        )
        drawLine(color = CyberGold, start = center, end = bearingEnd, strokeWidth = 3f)

        // Center Beacon Dot (Device fix)
        drawCircle(color = CyberCyan.copy(alpha = 0.3f), radius = 16f, center = center)
        drawCircle(color = CyberCyan, radius = 6f, center = center)
        drawCircle(color = Color.White, radius = 2.5f, center = center)
    }
}
