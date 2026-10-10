package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Vibrator
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.local.HackerflagDatabase
import com.example.data.local.entity.TimelineEventEntity
import com.example.data.security.CryptoUtils
import com.example.ui.components.CircuitDivider
import com.example.ui.components.CyberCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.CyberAlertCrimson
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberSuccessGreen
import com.example.ui.theme.CyberSurfaceHigh
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.CyberWarningAmber
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ActiveTheftPanicScreen(
    onNavigateBack: () -> Unit,
    onNavigateToFir: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = remember { HackerflagDatabase.getDatabase(context) }

    // 60-Minute Countdown Timer Simulation
    var secondsRemaining by remember { mutableIntStateOf(3540) } // ~59 mins
    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
    }

    // Interactive step checklist
    val completedSteps = remember {
        mutableStateMapOf(
            1 to false,
            2 to false,
            3 to false,
            4 to false,
            5 to false
        )
    }

    fun dialNumber(number: String) {
        try {
            val encodedNumber = Uri.encode(number)
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$encodedNumber"))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Intent fallback
        }
    }

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            // Browser fallback
        }
    }

    fun recordTimelineEvent(title: String, details: String, cat: String) {
        coroutineScope.launch {
            db.timelineDao().insertEvent(
                TimelineEventEntity(
                    eventCategory = cat,
                    title = title,
                    details = details,
                    evidenceHash = CryptoUtils.sha256("$title::$details::${System.currentTimeMillis()}")
                )
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Navigation Back & Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("panic_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberCyan
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "🚨 ACTIVE THEFT PANIC MODE",
                        color = CyberAlertCrimson,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "GOLDEN HOUR ZERO-TRUST LOCKDOWN",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Countdown Banner
        item {
            val mins = secondsRemaining / 60
            val secs = secondsRemaining % 60
            CyberCard(
                glowEffect = true,
                modifier = Modifier.fillMaxWidth().testTag("golden_hour_timer_card")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CRITICAL GOLDEN HOUR COUNTDOWN",
                        color = CyberAlertCrimson,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "RBI Liability Protection: Zero Customer Loss if reported within 3 days",
                        color = CyberGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // TOP 3 URGENT ACTIONS - IMMEDIATE STRIKE
        item {
            Text(
                text = "⚡ TOP 3 IMMEDIATE ACTIONS (NO DELAYS)",
                color = CyberGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
        }

        // Action 1: USSD Banking Freeze
        item {
            UrgentActionCard(
                actionNumber = "1",
                title = "FREEZE UPI & BANK ACCOUNTS",
                description = "Universal national USSD gateway instantly disables linked UPI without internet.",
                primaryButtonText = "DIAL *99*4*1#",
                secondaryButtonText = "NATIONAL 1930 HELPLINE",
                onPrimaryClick = {
                    recordTimelineEvent("USSD *99*4*1# Freeze Dialed", "Immediate UPI linkage halt triggered.", "BANK_FROZEN")
                    dialNumber("*99*4*1#")
                },
                onSecondaryClick = {
                    recordTimelineEvent("1930 Cyber Helpline Dialed", "Reported cyber financial incident to national portal.", "HELPLINE_1930")
                    dialNumber("1930")
                },
                testTag = "urgent_bank_freeze_card"
            )
        }

        // Action 2: Carrier SIM Lock Hotline
        item {
            UrgentActionCard(
                actionNumber = "2",
                title = "BLOCK SIM CARD IMMEDIATELY",
                description = "Thieves use physical SIM in another phone to hijack OTPs & WhatsApp. Block your number now.",
                primaryButtonText = "CALL CARRIER (198 / 121 / 199)",
                secondaryButtonText = "JIO / AIRTEL / VI PORTAL",
                onPrimaryClick = {
                    recordTimelineEvent("Carrier SIM Block Hotline Dialed", "Requested immediate SIM deactivation.", "SIM_BLOCKED")
                    dialNumber("198")
                },
                onSecondaryClick = {
                    openUrl("https://www.jio.com/help/faq/mobile/services/sim-services/how-to-suspend-jio-services")
                },
                testTag = "urgent_sim_lock_card"
            )
        }

        // Action 3: CEIR IMEI Blacklist
        item {
            UrgentActionCard(
                actionNumber = "3",
                title = "BLOCK IMEI ON CEIR PORTAL",
                description = "Government Sanchar Saathi blocks device from connecting to all Indian telecom towers permanently.",
                primaryButtonText = "OPEN CEIR PORTAL (GOV.IN)",
                secondaryButtonText = "GET BLOCK DETAILS",
                onPrimaryClick = {
                    recordTimelineEvent("CEIR Sanchar Saathi Portal Opened", "Initiated national IMEI blocklist request.", "CEIR_FLAGGED")
                    openUrl("https://www.ceir.gov.in/Request/CeirUserReqtForm.jsp")
                },
                onSecondaryClick = {
                    openUrl("https://sancharsaathi.gov.in")
                },
                testTag = "urgent_ceir_block_card"
            )
        }

        // Guided Golden-Hour Stepper
        item {
            CircuitDivider()
            Text(
                text = "GUIDED 5-STAGE LOCKDOWN STEPPER",
                color = CyberCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
        }

        // Stage 1
        item {
            StepperStageCard(
                stageIndex = 1,
                title = "Stage 1: Banking & UPI Lockdown",
                description = "Call your primary bank helpline (SBI: 1800111109, HDFC: 1800164606, ICICI: 18001080) to stop debit cards and netbanking access.",
                isCompleted = completedSteps[1] == true,
                onToggleComplete = {
                    completedSteps[1] = !(completedSteps[1] ?: false)
                    if (completedSteps[1] == true) recordTimelineEvent("Bank Accounts Manually Frozen", "All card and netbanking hotlines contacted.", "BANK_FROZEN")
                },
                actionLabel = "Dial SBI (1800111109)",
                onAction = { dialNumber("1800111109") }
            )
        }

        // Stage 2
        item {
            StepperStageCard(
                stageIndex = 2,
                title = "Stage 2: WhatsApp Web & App Session Kill",
                description = "Send email to support@whatsapp.com with 'Lost/Stolen: Please deactivate my account' or log in to a new spare device.",
                isCompleted = completedSteps[2] == true,
                onToggleComplete = {
                    completedSteps[2] = !(completedSteps[2] ?: false)
                    if (completedSteps[2] == true) recordTimelineEvent("WhatsApp Session Revocation", "Deactivation request dispatched to WhatsApp.", "ACCOUNT_KILL")
                },
                actionLabel = "Compose Deactivation Mail",
                onAction = {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@whatsapp.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Lost/Stolen: Please deactivate my account")
                        putExtra(Intent.EXTRA_TEXT, "Hello WhatsApp Support,\n\nMy mobile phone has been stolen. Kindly deactivate my WhatsApp account immediately for phone number: +91 [ENTER YOUR NUMBER].\n\nThank you.")
                    }
                    try { context.startActivity(emailIntent) } catch (e: Exception) {}
                }
            )
        }

        // Stage 3
        item {
            StepperStageCard(
                stageIndex = 3,
                title = "Stage 3: Google / Apple Remote Erase",
                description = "Open Google Find My Device or Apple iCloud Find My in browser to initiate remote data wipe and device pin lock.",
                isCompleted = completedSteps[3] == true,
                onToggleComplete = {
                    completedSteps[3] = !(completedSteps[3] ?: false)
                    if (completedSteps[3] == true) recordTimelineEvent("Remote Cloud Wipe Initiated", "Triggered Android/iOS remote erase.", "CLOUD_WIPE")
                },
                actionLabel = "Open Google Find Device",
                onAction = { openUrl("https://www.google.com/android/find") }
            )
        }

        // Stage 4
        item {
            StepperStageCard(
                stageIndex = 4,
                title = "Stage 4: File Police Zero-FIR & 1930 Portal",
                description = "Draft state-compliant police complaint text. Carry invoice, IMEI details, and visit the nearest police station or cybercrime.gov.in.",
                isCompleted = completedSteps[4] == true,
                onToggleComplete = {
                    completedSteps[4] = !(completedSteps[4] ?: false)
                },
                actionLabel = "Open Auto-FIR Drafter",
                onAction = onNavigateToFir
            )
        }

        // Stage 5
        item {
            StepperStageCard(
                stageIndex = 5,
                title = "Stage 5: Insurance Claim Affidavit",
                description = "Notify your mobile insurance provider with police complaint acknowledgement and CEIR block receipt within 48 hours.",
                isCompleted = completedSteps[5] == true,
                onToggleComplete = {
                    completedSteps[5] = !(completedSteps[5] ?: false)
                    if (completedSteps[5] == true) recordTimelineEvent("Insurance Claim Notice Dispatched", "Proof of loss affidavit generated.", "INSURANCE_NOTIFIED")
                },
                actionLabel = "View Legal Rights & Claim Info",
                onAction = onNavigateToFir
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun UrgentActionCard(
    actionNumber: String,
    title: String,
    description: String,
    primaryButtonText: String,
    secondaryButtonText: String,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    testTag: String
) {
    CyberCard(
        borderColor = CyberAlertCrimson.copy(alpha = 0.8f),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(CyberAlertCrimson),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = actionNumber,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = CyberTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onPrimaryClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberAlertCrimson),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = primaryButtonText, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onSecondaryClick,
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = secondaryButtonText, color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StepperStageCard(
    stageIndex: Int,
    title: String,
    description: String,
    isCompleted: Boolean,
    onToggleComplete: () -> Unit,
    actionLabel: String,
    onAction: () -> Unit
) {
    CyberCard(
        borderColor = if (isCompleted) CyberSuccessGreen else CyberBorder,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleComplete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Mark Complete",
                        tint = if (isCompleted) CyberSuccessGreen else CyberTextMuted
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = if (isCompleted) CyberSuccessGreen else CyberTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                color = CyberTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 38.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 38.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberSurfaceHigh)
                        .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .clickable { onAction() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = actionLabel,
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
