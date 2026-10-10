package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SimCardAlert
import androidx.compose.material.icons.filled.VpnKey
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
import com.example.data.local.entity.StolenDeviceEntity
import com.example.data.local.entity.TimelineEventEntity
import com.example.data.model.CellTower
import com.example.data.security.CryptoUtils
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

    var selectedTabIndex by remember(initialTabIndex) { mutableIntStateOf(initialTabIndex) }
    val tabTitles = listOf("Radar & GIS", "CEIR Portal", "Zero-Knowledge Hasher", "Public Registry", "SIM & Risk")

    // Mock cell towers for Triangulation
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
    var imeiInput by remember { mutableStateOf("869204058193847") }
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
    var ceirImei1 by remember { mutableStateOf("869204058193847") }
    var ceirImei2 by remember { mutableStateOf("869204058193854") }
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
            .background(CyberBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        text = "MODULE 1: ANTI-THEFT & IMEI RADAR",
                        color = CyberCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "TRIANGULATION • CEIR BLACKLIST • ZERO-KNOWLEDGE",
                        color = CyberTextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Sub Navigation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CyberSurfaceHigh,
                contentColor = CyberCyan,
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

        // TAB 0: GIS Radar & Triangulation
        if (selectedTabIndex == 0) {
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

                        CircuitDivider()

                        // Silent Ping Protocol Button
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    db.timelineDao().insertEvent(
                                        TimelineEventEntity(
                                            eventCategory = "SILENT_PING",
                                            title = "Silent Emergency Ping Dispatched",
                                            details = "Encrypted telemetry ping routed: Lat 28.6324, Lng 77.2187, Battery 68%, Serving CID 48921.",
                                            evidenceHash = CryptoUtils.sha256("PING_${System.currentTimeMillis()}")
                                        )
                                    )
                                }
                                Toast.makeText(context, "Silent Emergency Ping Logged & Dispatched", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("silent_ping_button")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "DISPATCH SILENT EMERGENCY PING PROTOCOL", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // TAB 1: CEIR / Sanchar Saathi Portal Workflow
        if (selectedTabIndex == 1) {
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

        // TAB 2: Zero-Knowledge Hasher
        if (selectedTabIndex == 2) {
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

                        // Output Block
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

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { copyToClipboard(imeiProof.imeiSha256, "IMEI SHA-256 Hash") },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("COPY HASH", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // TAB 3: Public Stolen Flag Registry
        if (selectedTabIndex == 3) {
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

        // TAB 4: SIM Swap & Risk Engine
        if (selectedTabIndex == 4) {
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

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberSurfaceHigh)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("SIM Slot 2 State:", color = CyberTextMuted, fontSize = 11.sp)
                                Text("No Secondary SIM", color = CyberTextSecondary, fontSize = 12.sp)
                            }
                            StatusPill(statusText = "STANDBY", colorHex = 0xFF9AAAC2)
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
