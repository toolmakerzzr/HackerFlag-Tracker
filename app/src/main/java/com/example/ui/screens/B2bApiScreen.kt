package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Router
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.security.CryptoUtils
import com.example.ui.components.CircuitDivider
import com.example.ui.components.CyberCard
import com.example.ui.components.OwnershipQrCanvas
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

@Composable
fun B2bApiScreen(
    onNavigateBack: () -> Unit,
    initialTabIndex: Int = 0,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    var selectedTab by remember(initialTabIndex) { mutableIntStateOf(initialTabIndex) }
    val tabTitles = listOf("Ownership QR", "B2B REST API Console", "Proxy & Tunnel")

    // QR State
    var qrImei by remember { mutableStateOf("869204058193847") }
    var qrModel by remember { mutableStateOf("Samsung Galaxy S24 Ultra") }
    var qrInvoiceNo by remember { mutableStateOf("INV/2026/DEL/9014") }

    val qrPayloadString = remember(qrImei, qrModel, qrInvoiceNo) {
        val hash = CryptoUtils.sha256(qrImei)
        "HACKERFLAG::VERIFY::IMEI_HASH=$hash::MODEL=$qrModel::INV=$qrInvoiceNo::SEAL=${CryptoUtils.generateDocumentSeal(qrImei, 1791599000000L)}"
    }

    // B2B API Console State
    var apiImeiQuery by remember { mutableStateOf("869204058193847") }
    var apiEndpoint by remember { mutableStateOf("POST /api/v1/registry/verify-device") }
    var apiResponseJson by remember {
        mutableStateOf(
            """
            {
              "status": "FLAGGED_STOLEN",
              "certified_safe_to_buy": false,
              "fraud_risk_score": 94,
              "telecom_ceir_blocked": true,
              "fir_reference": "FIR/DEL/2026/0842",
              "police_jurisdiction": "Cyber Crime PS New Delhi",
              "query_hash": "63f829a1b0201...",
              "cached_ipv4_route": "104.21.49.201",
              "latency_ms": 18
            }
            """.trimIndent()
        )
    }

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("b2b_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberCyan
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "MODULE 5 & 6: B2B TOOLS & QR",
                        color = CyberCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "OWNERSHIP QR • REST API CONSOLE • PROXY RELAY",
                        color = CyberTextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Sub Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CyberSurfaceHigh,
                contentColor = CyberCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyberCyan
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
                                color = if (selectedTab == index) CyberCyan else CyberTextSecondary
                            )
                        }
                    )
                }
            }
        }

        // TAB 0: Asset Ownership QR Generator
        if (selectedTab == 0) {
            item {
                CyberCard(glowEffect = true, modifier = Modifier.fillMaxWidth().testTag("ownership_qr_card")) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TAMPER-PROOF ASSET OWNERSHIP QR",
                            color = CyberGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Encodes cryptographic proof of ownership for secondhand invoices.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // QR Code Canvas
                        OwnershipQrCanvas(payloadString = qrPayloadString, sizeDp = 200.dp)

                        Spacer(modifier = Modifier.height(14.dp))
                        StatusPill(statusText = "CRYPTOGRAPHICALLY SIGNED", colorHex = 0xFF00FF9D)
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = qrImei,
                            onValueChange = { qrImei = it },
                            label = { Text("Device IMEI") },
                            modifier = Modifier.fillMaxWidth().testTag("qr_imei_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = qrModel,
                            onValueChange = { qrModel = it },
                            label = { Text("Device Model") },
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
                            value = qrInvoiceNo,
                            onValueChange = { qrInvoiceNo = it },
                            label = { Text("Invoice Number / Bill No.") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { copyToClipboard(qrPayloadString, "Ownership QR Payload") },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = CyberBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("COPY ENCODED VERIFICATION DATA", color = CyberBackground, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // TAB 1: B2B REST API Console
        if (selectedTab == 1) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth().testTag("b2b_api_console_card")) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "B2B REFURBISHED RETAILER API CONSOLE",
                                color = CyberGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            StatusPill(statusText = "PORT 8080", colorHex = 0xFF00F0FF)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Retailer & pawnshop endpoint for verifying if a mobile is blacklisted or stolen before buying.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = apiEndpoint,
                            onValueChange = { apiEndpoint = it },
                            label = { Text("Endpoint Route") },
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
                            value = apiImeiQuery,
                            onValueChange = { apiImeiQuery = it },
                            label = { Text("Query Target IMEI / SHA-256") },
                            modifier = Modifier.fillMaxWidth().testTag("b2b_query_input"),
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
                                val isStolen = apiImeiQuery.trim() == "869204058193847" || apiImeiQuery.trim() == "354928109485721"
                                val status = if (isStolen) "FLAGGED_STOLEN" else "CLEAN_AND_VERIFIED"
                                val safe = !isStolen
                                val risk = if (isStolen) 94 else 12
                                apiResponseJson = """
                                {
                                  "status": "$status",
                                  "certified_safe_to_buy": $safe,
                                  "fraud_risk_score": $risk,
                                  "telecom_ceir_blocked": $isStolen,
                                  "query_imei_hash": "${CryptoUtils.sha256(apiImeiQuery).take(24)}...",
                                  "timestamp": ${System.currentTimeMillis()},
                                  "routed_via": "IPv4-FastRoute-Proxy:8080",
                                  "latency_ms": 14
                                }
                                """.trimIndent()
                                Toast.makeText(context, "API Query Executed (200 OK)", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("execute_b2b_query_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = CyberBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("EXECUTE REST API QUERY", color = CyberBackground, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Response Preview
                        Text(text = "HTTP RESPONSE (200 OK):", color = CyberTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberSurfaceHigh)
                                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = apiResponseJson,
                                color = CyberTextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // TAB 2: Infrastructure, Proxy & Offline Resilience
        if (selectedTab == 2) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "THREADED PROXY & IPV4 ENGINE",
                                color = CyberCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            StatusPill(statusText = "RUNNING", colorHex = 0xFF00FF9D)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Single-port threaded proxy server architecture operating on Port 8080. Eliminates CORS boundaries and routes packets over low-latency IPv4 fast-paths, bypassing mobile carrier DNS timeouts.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                        CircuitDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Listen Port:", color = CyberTextMuted, fontSize = 11.sp)
                                Text("8080 (Threaded)", color = CyberTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("DNS Fallback:", color = CyberTextMuted, fontSize = 11.sp)
                                Text("IPv4 Direct 1.1.1.1", color = CyberSuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Offline Resilience:", color = CyberTextMuted, fontSize = 11.sp)
                                Text("Smart Cache Active", color = CyberGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
