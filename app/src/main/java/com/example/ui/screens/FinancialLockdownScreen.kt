package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.data.model.BankProtocol
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
import kotlinx.coroutines.launch

@Composable
fun FinancialLockdownScreen(
    onNavigateBack: () -> Unit,
    initialTabIndex: Int = 0,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = remember { HackerflagDatabase.getDatabase(context) }

    var selectedTab by remember(initialTabIndex) { mutableIntStateOf(initialTabIndex) }
    val tabTitles = listOf("USSD & 1930", "Banks Freeze", "UPI Kill", "Cards & Crypto", "Auto-Pay Audit")

    val banks = remember {
        listOf(
            BankProtocol(
                bankName = "State Bank of India (SBI)",
                tollFreePhone = "1800111109",
                smsCommand = "BLOCK XXXX (Last 4 digits)",
                smsNumber = "567676",
                ussdQuickCode = "*99*41#",
                netBankingLockUrl = "https://retail.onlinesbi.sbi/retail/userlock.htm",
                iconEmoji = "🏛️"
            ),
            BankProtocol(
                bankName = "HDFC Bank",
                tollFreePhone = "1800164606",
                smsCommand = "CCBLK XXXX",
                smsNumber = "5676712",
                ussdQuickCode = "*99*42#",
                netBankingLockUrl = "https://netbanking.hdfcbank.com/netbanking/",
                iconEmoji = "🏦"
            ),
            BankProtocol(
                bankName = "ICICI Bank",
                tollFreePhone = "18001080",
                smsCommand = "IBLOCK XXXX",
                smsNumber = "9215676766",
                ussdQuickCode = "*99*44#",
                netBankingLockUrl = "https://infinity.icicibank.com/corp/AuthenticationController",
                iconEmoji = "💳"
            ),
            BankProtocol(
                bankName = "Axis Bank",
                tollFreePhone = "18604195555",
                smsCommand = "BLOCK XXXX",
                smsNumber = "5676782",
                ussdQuickCode = "*99*45#",
                netBankingLockUrl = "https://www.axisbank.com/bank-smart/internet-banking",
                iconEmoji = "🏛️"
            ),
            BankProtocol(
                bankName = "Punjab National Bank (PNB)",
                tollFreePhone = "18001802222",
                smsCommand = "HOT XXXX",
                smsNumber = "5607040",
                ussdQuickCode = "*99*43#",
                netBankingLockUrl = "https://netpnb.com",
                iconEmoji = "🏦"
            )
        )
    }

    val autoPayItems = remember {
        mutableStateMapOf(
            "Netflix & Entertainment" to false,
            "Amazon Pay & E-commerce Mandates" to false,
            "Mutual Fund SIP Auto-Debits" to false,
            "Electricity & Utility Billers" to false,
            "Credit Card Auto-Pay Facilities" to false
        )
    }

    fun dial(number: String, eventTitle: String) {
        coroutineScope.launch {
            db.timelineDao().insertEvent(
                TimelineEventEntity(
                    eventCategory = "FINANCIAL_LOCKDOWN",
                    title = eventTitle,
                    details = "Dialed $number to initiate emergency freeze.",
                    evidenceHash = CryptoUtils.sha256("CALL_$number::${System.currentTimeMillis()}")
                )
            )
        }
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(number)}"))
            context.startActivity(intent)
        } catch (e: Exception) {}
    }

    fun sendSms(destNumber: String, body: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$destNumber")).apply {
                putExtra("sms_body", body)
            }
            context.startActivity(intent)
        } catch (e: Exception) {}
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
                IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("banking_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberGold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "MODULE 2: FINANCIAL & BANKING LOCKDOWN",
                        color = CyberGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "USSD GATEWAY • 1930 HELPLINE • UPI DEACTIVATION",
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
                selectedTabIndex = selectedTab,
                containerColor = CyberSurfaceHigh,
                contentColor = CyberGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyberGold
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) CyberGold else CyberTextSecondary
                            )
                        }
                    )
                }
            }
        }

        // TAB 0: USSD & 1930
        if (selectedTab == 0) {
            item {
                CyberCard(glowEffect = true, modifier = Modifier.fillMaxWidth().testTag("ussd_gateway_card")) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1-CLICK USSD BANKING FREEZE",
                                color = CyberGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            StatusPill(statusText = "OFFLINE DIALER", colorHex = 0xFF00FF9D)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "USSD code *99*4*1# operates over GSM telecom signaling. It disables linked BHIM/UPI accounts from any secondary device without data connectivity.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { dial("*99*4*1#", "Universal USSD UPI Freeze Dialed") },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberAlertCrimson),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("dial_ussd_button")
                        ) {
                            Icon(imageVector = Icons.Default.Dialpad, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "LAUNCH USSD GATEWAY (*99*4*1#)", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            item {
                CyberCard(modifier = Modifier.fillMaxWidth().testTag("helpline_1930_card")) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1930 NATIONAL CYBER CRIME HELPLINE",
                                color = CyberCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            StatusPill(statusText = "24x7 TOLL-FREE", colorHex = 0xFF00F0FF)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Direct hotline to Ministry of Home Affairs Cyber Financial Fraud Management System. Freezes funds in scammer accounts in real-time.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { dial("1930", "1930 National Cyber Helpline Call") },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("call_1930_button")
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = CyberBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "SPEED DIAL 1930 HELPLINE", color = CyberBackground, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // TAB 1: Banks Freeze
        if (selectedTab == 1) {
            items(banks) { bank ->
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${bank.iconEmoji} ${bank.bankName}",
                                color = CyberTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Code: ${bank.ussdQuickCode}",
                                color = CyberGold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Toll-Free Hotline: ${bank.tollFreePhone} | SMS: ${bank.smsCommand} to ${bank.smsNumber}",
                            color = CyberTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { dial(bank.tollFreePhone, "${bank.bankName} Hotline Dialed") },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = BorderStroke(1.dp, CyberGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = CyberGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("CALL", color = CyberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { sendSms(bank.smsNumber, bank.smsCommand) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = BorderStroke(1.dp, CyberCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SMS STOP", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { openUrl(bank.netBankingLockUrl) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = BorderStroke(1.dp, CyberBorder),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, tint = CyberTextSecondary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WEB LOCK", color = CyberTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: UPI Apps Kill
        if (selectedTab == 2) {
            item {
                UpiKillCard(
                    appName = "PhonePe",
                    hotline = "08068727374",
                    procedure = "Call PhonePe fraud desk -> State registered number -> Request immediate device unlinking and wallet pause.",
                    onCall = { dial("08068727374", "PhonePe Fraud Desk Call") }
                )
            }
            item {
                UpiKillCard(
                    appName = "Google Pay (GPay)",
                    hotline = "18004190157",
                    procedure = "Call Google Pay support -> Select option to report lost device -> Remove linked bank accounts remotely.",
                    onCall = { dial("18004190157", "Google Pay Support Call") }
                )
            }
            item {
                UpiKillCard(
                    appName = "Paytm",
                    hotline = "01203888388",
                    procedure = "Call 0120-3888388 -> Select 'Report Loss or Theft of Phone' -> Choose 'Log out from all devices'.",
                    onCall = { dial("01203888388", "Paytm Theft Hotline Call") }
                )
            }
            item {
                UpiKillCard(
                    appName = "BHIM NPCI",
                    hotline = "18001201740",
                    procedure = "Call NPCI toll-free helpline -> Request blocking of UPI ID and VPA handles tied to stolen SIM.",
                    onCall = { dial("18001201740", "BHIM NPCI Support Call") }
                )
            }
        }

        // TAB 3: Cards & Crypto Panic Lock
        if (selectedTab == 3) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, tint = CyberAlertCrimson)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CARD NETWORKS EMERGENCY HOTLIST",
                                color = CyberAlertCrimson,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { dial("1800227692", "Visa Hotlist Hotline") },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = BorderStroke(1.dp, CyberCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("VISA: 1800-227-692", fontSize = 10.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { dial("1800112424", "Mastercard Hotlist Hotline") },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = BorderStroke(1.dp, CyberGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("MASTERCARD: 1800-112-424", fontSize = 10.sp, color = CyberGold, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CurrencyBitcoin, contentDescription = null, tint = CyberGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CRYPTO WALLET PANIC LOCK",
                                color = CyberGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Metamask / Trust Wallet: Revoke allowance permissions immediately via revoke.cash.\n• Move funds to hardware cold wallet or secondary multisig.\n• Exchange sessions (CoinDCX, Binance, WazirX): Change password on desktop to invalidate mobile JWT tokens.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { openUrl("https://revoke.cash") },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                            border = BorderStroke(1.dp, CyberGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("OPEN REVOKE.CASH ALLOWANCE REVOCATION", color = CyberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // TAB 4: Auto-Pay Revocation Tracker
        if (selectedTab == 4) {
            item {
                Text(
                    text = "RECURRING MANDATE REVOCATION AUDIT",
                    color = CyberGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            items(autoPayItems.keys.toList()) { key ->
                val checked = autoPayItems[key] == true
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (checked) CyberSuccessGreen else CyberBorder
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { autoPayItems[key] = !checked },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (checked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (checked) CyberSuccessGreen else CyberTextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = key,
                            color = if (checked) CyberSuccessGreen else CyberTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun UpiKillCard(
    appName: String,
    hotline: String,
    procedure: String,
    onCall: () -> Unit
) {
    CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appName,
                    color = CyberCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = hotline,
                    color = CyberGold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = procedure,
                color = CyberTextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onCall,
                colors = ButtonDefaults.buttonColors(containerColor = CyberAlertCrimson),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("CALL $appName EMERGENCY FRAUD DESK", fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
