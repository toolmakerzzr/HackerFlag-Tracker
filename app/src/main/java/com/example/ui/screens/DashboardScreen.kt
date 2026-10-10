package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.fragment.app.FragmentActivity
import com.example.data.security.BiometricAuthManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ScreenState
import com.example.data.local.HackerflagDatabase
import com.example.data.local.entity.TimelineEventEntity
import com.example.data.model.FeatureCatalog
import com.example.data.model.FeatureCategory
import com.example.data.model.FeatureItem
import com.example.ui.components.CircuitDivider
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberTicker
import com.example.ui.components.EmergencyAlertButton
import com.example.ui.components.FeatureSearchBar
import com.example.ui.components.PoliceSosButton
import com.example.ui.components.RiskScoreDial
import com.example.ui.components.SearchResultCard
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigateToActiveTheft: () -> Unit,
    onNavigateToImeiRadar: (tab: Int) -> Unit,
    onNavigateToBanking: (tab: Int) -> Unit,
    onNavigateToLegalFir: (tab: Int) -> Unit,
    onNavigateToRemoteDefense: () -> Unit,
    onNavigateToB2bQr: (tab: Int) -> Unit,
    onOpenAiAssistant: (initialQuery: String) -> Unit,
    onNavigateToFeature: (FeatureItem) -> Unit,
    onLockConsole: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val db = HackerflagDatabase.getDatabase(context)
    val timelineEvents by db.timelineDao().getAllEvents().collectAsState(initial = emptyList())

    // Feature Search State
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FeatureCategory.ALL) }

    val searchResults = remember(searchQuery, selectedCategory) {
        FeatureCatalog.search(searchQuery, selectedCategory)
    }

    val isFilteringActive = searchQuery.isNotBlank() || selectedCategory != FeatureCategory.ALL

    // Quick comfort search suggestions
    val comfortQuickQueries = listOf(
        "⚡ 1930 Helpline" to "1930",
        "💳 USSD Freeze" to "*99*4*1#",
        "📱 Block IMEI" to "CEIR",
        "📝 Draft FIR" to "FIR",
        "🔐 WhatsApp Kill" to "WhatsApp",
        "📡 Cell Radar" to "Radar",
        "🛡️ Ownership QR" to "QR"
    )

    fun dial(number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(number)}"))
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
        // 1. Live Intelligence Telemetry Ticker
        item {
            Spacer(modifier = Modifier.height(2.dp))
            CyberTicker(text = "CEIR Sync: ONLINE | 1930 Router: ACTIVE | Made by Akash Tiwari | Zero-Trust Armed")
        }

        // 2. Cyber Header with Badge, Titles, & Upper Right Corner 3-Sec Police SOS Button
        item {
            CyberCard(
                glowEffect = true,
                modifier = Modifier.fillMaxWidth().testTag("cyber_defense_header")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Side: Emblem and Platform Titles
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(2.dp, CyberCyan, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.hackerflag_icon),
                                contentDescription = "Hackerflag Cyber Defense Badge",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "HACKERFLAG TRACKER",
                                color = CyberTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "DEFENSE PLATFORM",
                                    color = CyberCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberGold.copy(alpha = 0.16f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "BY AKASH TIWARI",
                                        color = CyberGoldDark,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(CyberSuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "ZERO-TRUST ACTIVE",
                                    color = CyberSuccessGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberCyan.copy(alpha = 0.12f))
                                        .border(0.8.dp, CyberCyan.copy(alpha = 0.45f), RoundedCornerShape(4.dp))
                                        .clickable { onLockConsole() }
                                        .padding(horizontal = 5.dp, vertical = 1.5.dp)
                                        .testTag("header_quick_lock_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Lock",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(9.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "LOCK",
                                        color = CyberCyan,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Upper Right Corner: 3-Second Hold Police SOS Button
                    PoliceSosButton()
                }
            }
        }

        // 2.5. Biometric Entry Point & Emergency Controls Console Card
        item {
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("biometric_guard_card"),
                borderColor = CyberCyan.copy(alpha = 0.45f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyan.copy(alpha = 0.12f))
                                    .border(1.dp, CyberCyan.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(9.dp))
                            Column {
                                Text(
                                    text = "BIOMETRIC ENTRY GUARD",
                                    color = CyberTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "OWNER VERIFIED: AKASH TIWARI",
                                    color = CyberSuccessGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Instant Lock button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberAlertCrimson.copy(alpha = 0.1f))
                                .border(0.8.dp, CyberAlertCrimson.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .clickable { onLockConsole() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("lock_console_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock",
                                tint = CyberAlertCrimson,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LOCK CONSOLE",
                                color = CyberAlertCrimson,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Text(
                        text = "Emergency controls (3-second Police SOS, Device Wipe, Bank Freeze) are locked to owner biometrics via Android BiometricPrompt API.",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                (context as? FragmentActivity)?.let { act ->
                                    BiometricAuthManager.showBiometricPrompt(
                                        activity = act,
                                        title = "Hackerflag Biometric Test",
                                        subtitle = "Verify Owner Biometrics",
                                        description = "Testing BiometricPrompt API integration for emergency controls.",
                                        onSuccess = {
                                            Toast.makeText(context, "BiometricPrompt: Owner Verified (Akash Tiwari)", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { _, err ->
                                            Toast.makeText(context, "BiometricPrompt: $err", Toast.LENGTH_SHORT).show()
                                        },
                                        onFailed = {
                                            Toast.makeText(context, "Biometric recognition failed", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberSurfaceHigh,
                                contentColor = CyberCyan
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("test_biometric_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "TEST BIOMETRIC",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Button(
                            onClick = { onLockConsole() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberCyan,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("secure_lock_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "LOCK CONSOLE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. ULTRA COMFORTABLE FEATURE SEARCHBAR
        item {
            FeatureSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                resultsCount = searchResults.size
            )
        }

        // Quick Comfort Suggestion Pills (Shown when search query is empty)
        if (searchQuery.isBlank() && selectedCategory == FeatureCategory.ALL) {
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(comfortQuickQueries) { (label, keyword) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberSurfaceHigh)
                                .border(0.8.dp, CyberBorder, RoundedCornerShape(8.dp))
                                .clickable { searchQuery = keyword }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                color = CyberTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // 4. SEARCH RESULTS VIEW & RICH SEARCH SUGGESTIONS HELP
        if (isFilteringActive) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FOUND ${searchResults.size} MATCHING FEATURES",
                        color = CyberGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "CLEAR FILTER ✕",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            searchQuery = ""
                            selectedCategory = FeatureCategory.ALL
                        }
                    )
                }
            }

            // WHEN NO FEATURES ARE FOUND: DISPLAY COMPREHENSIVE SEARCH SUGGESTION HELP
            if (searchResults.isEmpty()) {
                item {
                    SearchSuggestionHelpCard(
                        query = searchQuery,
                        onSelectSuggestion = { searchQuery = it },
                        onNavigateToFeature = onNavigateToFeature,
                        onAskAi = { onOpenAiAssistant(searchQuery) }
                    )
                }
            } else {
                items(searchResults) { feature ->
                    SearchResultCard(
                        feature = feature,
                        onClick = { onNavigateToFeature(feature) }
                    )
                }
            }

            item {
                CircuitDivider()
            }
        }

        // 5. Urgent Active Theft Emergency Button (Primary Call-to-action)
        item {
            EmergencyAlertButton(
                text = "🚨 ACTIVE THEFT PANIC MODE",
                subtext = "CRITICAL FIRST 60-MINS: BANK, SIM & IMEI LOCKDOWN",
                onClick = onNavigateToActiveTheft
            )
        }

        // 6. Ultra-Comfort Quick Hotkeys Bar (1-touch instant triggers)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Hotkey 1: USSD
                QuickHotkeyButton(
                    icon = Icons.Default.Dialpad,
                    label = "*99*4*1#",
                    sub = "USSD Lock",
                    accentColor = CyberAlertCrimson,
                    onClick = { dial("*99*4*1#") },
                    modifier = Modifier.weight(1f)
                )
                // Hotkey 2: 1930
                QuickHotkeyButton(
                    icon = Icons.Default.Call,
                    label = "1930",
                    sub = "Cyber Help",
                    accentColor = CyberCyan,
                    onClick = { dial("1930") },
                    modifier = Modifier.weight(1f)
                )
                // Hotkey 3: FIR
                QuickHotkeyButton(
                    icon = Icons.Default.Gavel,
                    label = "Auto FIR",
                    sub = "Drafter",
                    accentColor = CyberSuccessGreen,
                    onClick = { onNavigateToLegalFir(0) },
                    modifier = Modifier.weight(1f)
                )
                // Hotkey 4: AI
                QuickHotkeyButton(
                    icon = Icons.Default.SmartToy,
                    label = "AI Help",
                    sub = "Intel Desk",
                    accentColor = CyberGold,
                    onClick = { onOpenAiAssistant("") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 7. Live Risk Exposure & Golden-Hour Card
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DEVICE RISK EXPOSURE",
                                color = CyberGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Real-time threat vulnerability metric",
                                color = CyberTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        StatusPill(
                            statusText = "ELEVATED RISK",
                            colorHex = 0xFFFF9900
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RiskScoreDial(score = 78)

                        Spacer(modifier = Modifier.width(18.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            RiskFactorItem(title = "Bank USSD Freeze", isDone = true)
                            Spacer(modifier = Modifier.height(6.dp))
                            RiskFactorItem(title = "SIM Card Lock", isDone = false)
                            Spacer(modifier = Modifier.height(6.dp))
                            RiskFactorItem(title = "CEIR IMEI Blacklist", isDone = true)
                            Spacer(modifier = Modifier.height(6.dp))
                            RiskFactorItem(title = "Remote Cloud Erase", isDone = false)
                        }
                    }

                    CircuitDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GOLDEN HOUR WINDOW",
                                color = CyberTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "38 Mins Remaining for Zero Liability",
                                color = CyberAlertCrimson,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberSurfaceHigh)
                                .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { onNavigateToActiveTheft() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "EXECUTE LOCK →",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // 8. Six Core Feature Modules Header
        item {
            Text(
                text = "CYBER DEFENSE MODULE MATRIX",
                color = CyberCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        // Module 1: Anti-Theft & IMEI Radar
        item {
            ModuleNavCard(
                icon = Icons.Default.Radar,
                title = "Module 1: Anti-Theft & IMEI Signal Radar",
                description = "Cell-tower triangulation GIS radar, CEIR blocking portal, zero-knowledge SHA-256 IMEI hasher & public registry.",
                badgeText = "GIS RADAR",
                accentColor = CyberCyan,
                onClick = { onNavigateToImeiRadar(0) },
                testTag = "nav_imei_radar"
            )
        }

        // Module 2: Emergency Banking Lockdown
        item {
            ModuleNavCard(
                icon = Icons.Default.AccountBalance,
                title = "Module 2: Financial & Banking Lockdown",
                description = "1-click *99*4*1# USSD gateway, 1930 Cyber helpline router, SBI/HDFC/ICICI freeze & UPI deactivation.",
                badgeText = "FINANCIAL SHIELD",
                accentColor = CyberGold,
                onClick = { onNavigateToBanking(0) },
                testTag = "nav_banking"
            )
        }

        // Module 3: Legal, Police & FIR Auto-Draft
        item {
            ModuleNavCard(
                icon = Icons.Default.Gavel,
                title = "Module 3: Legal, Police & FIR Automation",
                description = "Instant state-compliant Police FIR generator, PDF export with SHA-256 seal, station finder & IT Act advisor.",
                badgeText = "FIR DRAFTER",
                accentColor = CyberSuccessGreen,
                onClick = { onNavigateToLegalFir(0) },
                testTag = "nav_legal_fir"
            )
        }

        // Module 4: Remote Account & Identity Defense
        item {
            ModuleNavCard(
                icon = Icons.Default.Lock,
                title = "Module 4: Remote Account & Identity Defense",
                description = "WhatsApp remote deactivation, Google Find My / Apple iCloud kill-switch & social media hijack shield.",
                badgeText = "KILL-SWITCH",
                accentColor = CyberAlertCrimson,
                onClick = onNavigateToRemoteDefense,
                testTag = "nav_remote_defense"
            )
        }

        // Module 5 & 6: B2B Stolen Registry & Ownership QR
        item {
            ModuleNavCard(
                icon = Icons.Default.QrCode,
                title = "Module 5 & 6: B2B Stolen Registry & Ownership QR",
                description = "Refurbished mobile B2B API console, tamper-proof invoice QR generator & IPv4 low-latency server tunnel.",
                badgeText = "B2B API & QR",
                accentColor = CyberCyan,
                onClick = { onNavigateToB2bQr(0) },
                testTag = "nav_b2b_qr"
            )
        }

        // AI Intelligence Engine Card
        item {
            ModuleNavCard(
                icon = Icons.Default.SmartToy,
                title = "Cyber Intelligence AI Engine",
                description = "Dual-model load balancing with Gemini AI & smart emergency offline defense mode for instant theft guidance.",
                badgeText = "AI CONSULTANT",
                accentColor = CyberGold,
                onClick = { onOpenAiAssistant("") },
                testTag = "nav_ai_consultant"
            )
        }

        // 9. Evidentiary Timeline Section
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IMMUTABLE EVIDENTIARY TIMELINE",
                    color = CyberGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${timelineEvents.size} EVENTS RECORDED",
                    color = CyberTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        items(timelineEvents.take(4)) { event ->
            TimelineEventRow(event = event)
        }

        // 10. Platform Creator & Architecture Badge
        item {
            Spacer(modifier = Modifier.height(4.dp))
            CyberCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("made_by_akash_footer_badge"),
                borderColor = CyberCyan.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = 0.12f))
                            .border(1.2.dp, CyberCyan.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛡️", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "HACKERFLAG DEFENSE",
                                color = CyberTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberGold.copy(alpha = 0.18f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "MADE BY AKASH TIWARI",
                                    color = CyberGoldDark,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Crafted with ❤️ by Akash Tiwari • Anti-Theft & Cybercrime Mitigation Platform with Zero-Trust Security",
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

/**
 * Rich Search Suggestion Help Card displayed when no features match the search query.
 */
@Composable
private fun SearchSuggestionHelpCard(
    query: String,
    onSelectSuggestion: (String) -> Unit,
    onNavigateToFeature: (FeatureItem) -> Unit,
    onAskAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val q = query.trim().lowercase()

    // Key emergency features to suggest immediately
    val emergencyQuickFeatures = remember {
        FeatureCatalog.allFeatures.filter {
            it.id in listOf("ussd_freeze", "cyber_helpline_1930", "ceir_portal", "police_fir_draft", "whatsapp_kill", "cell_radar")
        }
    }

    // Popular keywords for quick replacement
    val popularKeywords = listOf("Bank", "CEIR", "FIR", "1930", "WhatsApp", "SIM", "Radar", "QR")

    CyberCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = CyberWarningAmber.copy(alpha = 0.6f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberWarningAmber.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "Search Help",
                        tint = CyberWarningAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "SEARCH SUGGESTION HELP",
                        color = CyberWarningAmber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "No exact module named \"$query\"",
                        color = CyberTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ghabraiye mat (Don't panic). If you are looking for urgent assistance or a specific tool, here are the most critical actions:",
                color = CyberTextPrimary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 1-Tap Emergency Action Buttons
            Text(
                text = "RECOMMENDED TOOLS (1-TAP LAUNCH):",
                color = CyberGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))

            emergencyQuickFeatures.take(4).forEach { feature ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurfaceHigh)
                        .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                        .clickable { onNavigateToFeature(feature) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = feature.category.emoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = feature.title,
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "OPEN →",
                            color = CyberGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Keyword Suggestions
            Text(
                text = "POPULAR SEARCH KEYWORDS (TAP TO REPLACE):",
                color = CyberTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(popularKeywords) { keyword ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberSurfaceHigh)
                            .border(0.8.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .clickable { onSelectSuggestion(keyword) }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = keyword,
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            CircuitDivider()

            // Button to query the AI assistant with this query
            Button(
                onClick = onAskAi,
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = CyberBackground, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ASK AI CONSULTANT FOR \"$query\"",
                    color = CyberBackground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun QuickHotkeyButton(
    icon: ImageVector,
    label: String,
    sub: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurfaceHigh)
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = CyberTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = sub,
                color = CyberTextMuted,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun RiskFactorItem(title: String, isDone: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(if (isDone) CyberSuccessGreen else CyberAlertCrimson.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Text("✓", color = CyberBackground, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            } else {
                Text("!", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = if (isDone) CyberTextPrimary else CyberTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isDone) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun ModuleNavCard(
    icon: ImageVector,
    title: String,
    description: String,
    badgeText: String,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        borderColor = CyberBorder,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = CyberTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = accentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = CyberTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun TimelineEventRow(event: TimelineEventEntity) {
    val formatter = SimpleDateFormat("HH:mm:ss dd MMM", Locale.getDefault())
    val dateString = formatter.format(Date(event.timestamp))

    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = CyberBorder.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .padding(top = 4.dp)
                    .clip(CircleShape)
                    .background(CyberCyan)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = event.title,
                        color = CyberTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateString,
                        color = CyberTextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = event.details,
                    color = CyberTextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "SEAL: ${event.evidenceHash.take(16)}...",
                    color = CyberGoldDark,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
