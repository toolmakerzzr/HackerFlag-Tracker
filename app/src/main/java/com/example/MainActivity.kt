package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.forensics.HackerFlagLogger
import com.example.data.interceptor.ThreatDefenseManager
import com.example.service.CyberSentinelService
import com.example.ui.components.CyberSettingsDialog
import com.example.ui.components.EdgeGlowOverlay
import com.example.ui.components.ForensicAuditDialog
import com.example.ui.components.ForensicRecentActivityCard
import com.example.ui.components.HeroShieldSection
import com.example.ui.components.MatrixSandboxDialog
import com.example.ui.components.StatusBanner
import com.example.ui.components.ThreatSimulatorPanel
import com.example.ui.components.ThreatStatsGrid
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassCard
import com.example.ui.theme.CyberGreenGlow
import com.example.ui.theme.CyberGreenPrimary
import com.example.ui.theme.CyberShieldTheme
import com.example.ui.theme.CyberTextDim
import com.example.ui.theme.CyberTextWhite

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize core security engine & sample forensics
        ThreatDefenseManager.initialize(applicationContext)
        HackerFlagLogger.initializeSampleLogsIfEmpty(applicationContext)

        // Launch persistent status daemon
        CyberSentinelService.startService(applicationContext)

        // Handle incoming URL intent if opened from browser or messaging app
        handleIncomingIntent(intent)

        setContent {
            CyberShieldTheme {
                CyberShieldApp(
                    onHandleIntent = { handleIncomingIntent(it) }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        val data = intent.data
        if (action == Intent.ACTION_VIEW && data != null) {
            val urlString = data.toString()
            val result = ThreatDefenseManager.inspectUrl(urlString)
            if (result.isBlocked) {
                ThreatDefenseManager.handleBlockedUrl(this, urlString, result.reason)
                Toast.makeText(this, "CyberShield: Malicious URL Blocked ($urlString)", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "CyberShield: URL verified safe ($urlString)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun CyberShieldApp(
    onHandleIntent: (Intent) -> Unit
) {
    val context = LocalContext.current

    // Request Notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            CyberSentinelService.startService(context)
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Reactive threat telemetry
    val isShieldActive by ThreatDefenseManager.isShieldActive.collectAsState()
    val isThreatActive by ThreatDefenseManager.isThreatActive.collectAsState()
    val statusTitle by ThreatDefenseManager.currentStatusTitle.collectAsState()
    val statusSubtitle by ThreatDefenseManager.currentStatusSubtitle.collectAsState()
    val linksBlocked by ThreatDefenseManager.linksBlockedCount.collectAsState()
    val apkWiped by ThreatDefenseManager.apkWipedCount.collectAsState()
    val sandboxTraps by ThreatDefenseManager.sandboxTrapsCount.collectAsState()
    val batteryDrain by ThreatDefenseManager.batteryDrain.collectAsState()
    val recentEvents by ThreatDefenseManager.recentEvents.collectAsState()

    // Modals state
    var showForensicsDialog by remember { mutableStateOf(false) }
    var showSandboxDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var glowIntensity by remember { mutableFloatStateOf(1.0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF090E17),
                        Color(0xFF0C1424),
                        Color(0xFF070B13)
                    )
                )
            )
    ) {
        // Transparent Edge-Lighting Alert Overlay (Vector Glow perimeter)
        EdgeGlowOverlay(
            isThreat = isThreatActive,
            isShieldActive = isShieldActive,
            glowIntensity = glowIntensity,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // 1. Top Bar (Glassmorphic) matching wireframe
                TopGlassmorphicBar(
                    onSettingsClick = { showSettingsDialog = true }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Active Status Banner (Green -> Red if Unsafe)
                StatusBanner(
                    title = statusTitle,
                    subtitle = statusSubtitle,
                    isThreat = isThreatActive
                )

                // 3. Hero Section with Smooth Glow Effect & ON/OFF switch
                HeroShieldSection(
                    isShieldActive = isShieldActive,
                    isThreat = isThreatActive,
                    onToggle = { enabled ->
                        ThreatDefenseManager.toggleShield(context, enabled)
                    }
                )

                // 4. Live Threat Analytics Grid (2x2)
                ThreatStatsGrid(
                    linksBlocked = linksBlocked,
                    apkWiped = apkWiped,
                    sandboxTraps = sandboxTraps,
                    batteryDrain = batteryDrain
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Recent Activity & Hacker Forensics with [ View hacker_flag.txt ]
                ForensicRecentActivityCard(
                    events = recentEvents,
                    onViewFileClick = { showForensicsDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 6. Threat Simulator & Testing Lab (Test Phishing, APK Wipe, Honey-Pot)
                ThreatSimulatorPanel(
                    onOpenMatrixSandbox = { showSandboxDialog = true }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Dialog Modals
        if (showForensicsDialog) {
            ForensicAuditDialog(
                onDismiss = { showForensicsDialog = false },
                onLogsUpdated = {
                    ThreatDefenseManager.refreshEvents(context)
                }
            )
        }

        if (showSandboxDialog) {
            MatrixSandboxDialog(
                onDismiss = {
                    showSandboxDialog = false
                    ThreatDefenseManager.clearActiveThreat()
                },
                onSandboxUpdated = {
                    ThreatDefenseManager.refreshEvents(context)
                }
            )
        }

        if (showSettingsDialog) {
            CyberSettingsDialog(
                currentGlowIntensity = glowIntensity,
                onGlowIntensityChanged = { glowIntensity = it },
                onDismiss = { showSettingsDialog = false }
            )
        }
    }
}

@Composable
private fun TopGlassmorphicBar(
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberGlassCard.copy(alpha = 0.85f))
            .border(1.dp, CyberGlassBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("top_glassmorphic_bar"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(CyberGreenPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "CyberShield Logo",
                    tint = CyberGreenGlow,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "CyberShield Pro",
                color = CyberTextWhite,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp
            )
        }

        // [ ⚙ Settings ] Button matching wireframe
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { onSettingsClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("settings_button"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[ ",
                color = CyberTextDim,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = CyberTextDim,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Settings ]",
                color = CyberTextDim,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
