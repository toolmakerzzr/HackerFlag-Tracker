package com.example

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FeatureItem
import com.example.ui.components.ComfortableBackground
import com.example.ui.components.PoliceSosButton
import com.example.ui.screens.ActiveTheftPanicScreen
import com.example.ui.screens.AiDefenseAssistantDialog
import com.example.ui.screens.B2bApiScreen
import com.example.ui.screens.BiometricEntryGateScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FinancialLockdownScreen
import com.example.ui.screens.ImeiRadarScreen
import com.example.ui.screens.LegalFirScreen
import com.example.ui.screens.RemoteDefenseScreen
import com.example.ui.theme.CyberAlertCrimson
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberSurfaceHigh
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.HackerflagTheme

enum class ScreenState {
    DASHBOARD,
    ACTIVE_THEFT,
    IMEI_RADAR,
    BANKING,
    LEGAL_FIR,
    REMOTE_DEFENSE,
    B2B_QR
}

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HackerflagTheme {
                HackerflagApp()
            }
        }
    }
}

@Composable
fun HackerflagApp() {
    var isOwnerAuthenticated by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf(ScreenState.DASHBOARD) }
    var showAiDialog by remember { mutableStateOf(false) }
    var aiDialogInitialQuery by remember { mutableStateOf("") }

    // Tab state memory for direct deep-linking from search
    var imeiTabIndex by remember { mutableIntStateOf(0) }
    var bankingTabIndex by remember { mutableIntStateOf(0) }
    var legalTabIndex by remember { mutableIntStateOf(0) }
    var b2bTabIndex by remember { mutableIntStateOf(0) }

    if (!isOwnerAuthenticated) {
        ComfortableBackground(modifier = Modifier.fillMaxSize()) {
            BiometricEntryGateScreen(
                onAuthenticationSuccess = {
                    isOwnerAuthenticated = true
                }
            )
        }
        return
    }

    BackHandler(enabled = currentScreen != ScreenState.DASHBOARD) {
        currentScreen = ScreenState.DASHBOARD
    }

    ComfortableBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                CyberBottomNavigation(
                    currentScreen = currentScreen,
                    onScreenSelected = { currentScreen = it }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        aiDialogInitialQuery = ""
                        showAiDialog = true
                    },
                    containerColor = CyberCyan,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag("fab_ai_assistant")
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI Defense Assistant",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(WindowInsets.systemBars.asPaddingValues()),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 640.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Global Upper Right Corner Bar when on secondary screens
                        if (currentScreen != ScreenState.DASHBOARD) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .border(0.8.dp, CyberBorder)
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "HACKERFLAG",
                                        color = CyberCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = " // MADE BY AKASH TIWARI",
                                        color = CyberGold,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CyberAlertCrimson.copy(alpha = 0.1f))
                                            .border(0.8.dp, CyberAlertCrimson.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                            .clickable { isOwnerAuthenticated = false }
                                            .padding(horizontal = 7.dp, vertical = 4.dp)
                                            .testTag("secondary_lock_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Lock Terminal",
                                            tint = CyberAlertCrimson,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "LOCK",
                                            color = CyberAlertCrimson,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // 3-Second Police SOS Hold Button in Upper Right Corner
                                    PoliceSosButton()
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "screenTransition"
                        ) { screen ->
                            when (screen) {
                                ScreenState.DASHBOARD -> DashboardScreen(
                                    onNavigateToActiveTheft = { currentScreen = ScreenState.ACTIVE_THEFT },
                                    onNavigateToImeiRadar = { tab ->
                                        imeiTabIndex = tab
                                        currentScreen = ScreenState.IMEI_RADAR
                                    },
                                    onNavigateToBanking = { tab ->
                                        bankingTabIndex = tab
                                        currentScreen = ScreenState.BANKING
                                    },
                                    onNavigateToLegalFir = { tab ->
                                        legalTabIndex = tab
                                        currentScreen = ScreenState.LEGAL_FIR
                                    },
                                    onNavigateToRemoteDefense = { currentScreen = ScreenState.REMOTE_DEFENSE },
                                    onNavigateToB2bQr = { tab ->
                                        b2bTabIndex = tab
                                        currentScreen = ScreenState.B2B_QR
                                    },
                                    onOpenAiAssistant = { query ->
                                        aiDialogInitialQuery = query
                                        showAiDialog = true
                                    },
                                    onNavigateToFeature = { feature ->
                                        when (feature.targetScreen) {
                                            ScreenState.DASHBOARD -> currentScreen = ScreenState.DASHBOARD
                                            ScreenState.ACTIVE_THEFT -> currentScreen = ScreenState.ACTIVE_THEFT
                                            ScreenState.IMEI_RADAR -> {
                                                imeiTabIndex = feature.targetTab
                                                currentScreen = ScreenState.IMEI_RADAR
                                            }
                                            ScreenState.BANKING -> {
                                                bankingTabIndex = feature.targetTab
                                                currentScreen = ScreenState.BANKING
                                            }
                                            ScreenState.LEGAL_FIR -> {
                                                legalTabIndex = feature.targetTab
                                                currentScreen = ScreenState.LEGAL_FIR
                                            }
                                            ScreenState.REMOTE_DEFENSE -> currentScreen = ScreenState.REMOTE_DEFENSE
                                            ScreenState.B2B_QR -> {
                                                b2bTabIndex = feature.targetTab
                                                currentScreen = ScreenState.B2B_QR
                                            }
                                        }
                                    },
                                    onLockConsole = { isOwnerAuthenticated = false }
                                )
                                ScreenState.ACTIVE_THEFT -> ActiveTheftPanicScreen(
                                    onNavigateBack = { currentScreen = ScreenState.DASHBOARD },
                                    onNavigateToFir = {
                                        legalTabIndex = 0
                                        currentScreen = ScreenState.LEGAL_FIR
                                    }
                                )
                                ScreenState.IMEI_RADAR -> ImeiRadarScreen(
                                    onNavigateBack = { currentScreen = ScreenState.DASHBOARD },
                                    initialTabIndex = imeiTabIndex
                                )
                                ScreenState.BANKING -> FinancialLockdownScreen(
                                    onNavigateBack = { currentScreen = ScreenState.DASHBOARD },
                                    initialTabIndex = bankingTabIndex
                                )
                                ScreenState.LEGAL_FIR -> LegalFirScreen(
                                    onNavigateBack = { currentScreen = ScreenState.DASHBOARD },
                                    initialTabIndex = legalTabIndex
                                )
                                ScreenState.REMOTE_DEFENSE -> RemoteDefenseScreen(
                                    onNavigateBack = { currentScreen = ScreenState.DASHBOARD }
                                )
                                ScreenState.B2B_QR -> B2bApiScreen(
                                    onNavigateBack = { currentScreen = ScreenState.DASHBOARD },
                                    initialTabIndex = b2bTabIndex
                                )
                            }
                        }
                    }
                }
            }
            }
        }
    }

    if (showAiDialog) {
        AiDefenseAssistantDialog(
            initialQuery = aiDialogInitialQuery,
            onDismiss = {
                showAiDialog = false
                aiDialogInitialQuery = ""
            }
        )
    }
}

@Composable
fun CyberBottomNavigation(
    currentScreen: ScreenState,
    onScreenSelected: (ScreenState) -> Unit
) {
    NavigationBar(
        containerColor = Color.White.copy(alpha = 0.98f),
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(0.8.dp, CyberBorder)
            .testTag("cyber_bottom_nav")
    ) {
        // 1. Dashboard
        NavigationBarItem(
            selected = currentScreen == ScreenState.DASHBOARD,
            onClick = { onScreenSelected(ScreenState.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Command", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberCyan,
                selectedTextColor = CyberCyan,
                unselectedIconColor = CyberTextMuted,
                unselectedTextColor = CyberTextMuted,
                indicatorColor = CyberSurfaceVariant
            )
        )

        // 2. Radar
        NavigationBarItem(
            selected = currentScreen == ScreenState.IMEI_RADAR,
            onClick = { onScreenSelected(ScreenState.IMEI_RADAR) },
            icon = { Icon(Icons.Default.Radar, contentDescription = "Radar") },
            label = { Text("Radar", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberCyan,
                selectedTextColor = CyberCyan,
                unselectedIconColor = CyberTextMuted,
                unselectedTextColor = CyberTextMuted,
                indicatorColor = CyberSurfaceVariant
            )
        )

        // 3. Emergency Panic
        NavigationBarItem(
            selected = currentScreen == ScreenState.ACTIVE_THEFT,
            onClick = { onScreenSelected(ScreenState.ACTIVE_THEFT) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CyberAlertCrimson),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Panic", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            },
            label = { Text("🚨 PANIC", fontSize = 10.sp, fontWeight = FontWeight.Black, color = CyberAlertCrimson) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberAlertCrimson,
                selectedTextColor = CyberAlertCrimson,
                unselectedIconColor = CyberAlertCrimson,
                unselectedTextColor = CyberAlertCrimson,
                indicatorColor = Color.Transparent
            )
        )

        // 4. Banking
        NavigationBarItem(
            selected = currentScreen == ScreenState.BANKING,
            onClick = { onScreenSelected(ScreenState.BANKING) },
            icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Banking") },
            label = { Text("Banks", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberGold,
                selectedTextColor = CyberGold,
                unselectedIconColor = CyberTextMuted,
                unselectedTextColor = CyberTextMuted,
                indicatorColor = CyberSurfaceVariant
            )
        )

        // 5. Legal FIR
        NavigationBarItem(
            selected = currentScreen == ScreenState.LEGAL_FIR,
            onClick = { onScreenSelected(ScreenState.LEGAL_FIR) },
            icon = { Icon(Icons.Default.Gavel, contentDescription = "FIR") },
            label = { Text("FIR Law", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberCyan,
                selectedTextColor = CyberCyan,
                unselectedIconColor = CyberTextMuted,
                unselectedTextColor = CyberTextMuted,
                indicatorColor = CyberSurfaceVariant
            )
        )
    }
}
