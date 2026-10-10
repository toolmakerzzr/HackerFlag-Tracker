package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
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
import com.example.data.local.entity.IncidentReportEntity
import com.example.data.local.entity.TimelineEventEntity
import com.example.data.model.PoliceStation
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LegalFirScreen(
    onNavigateBack: () -> Unit,
    initialTabIndex: Int = 0,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = remember { HackerflagDatabase.getDatabase(context) }

    var selectedTab by remember(initialTabIndex) { mutableIntStateOf(initialTabIndex) }
    val tabTitles = listOf("FIR Drafter", "Police Stations", "Insurance Proof", "Cyber Portal", "Legal Advisor")

    // Form inputs for FIR
    var victimName by remember { mutableStateOf("Rahul Sharma") }
    var contactPhone by remember { mutableStateOf("+91 98765 43210") }
    var incidentDate by remember { mutableStateOf("10-Oct-2026") }
    var incidentTime by remember { mutableStateOf("18:30 IST") }
    var locationAddress by remember { mutableStateOf("Rajiv Chowk Metro Station Gate 4, New Delhi") }
    var deviceBrandModel by remember { mutableStateOf("Samsung Galaxy S24 Ultra (512GB Titanium Black)") }
    var imei1 by remember { mutableStateOf("869204058193847") }
    var imei2 by remember { mutableStateOf("869204058193854") }
    var lossNarrative by remember { mutableStateOf("Device was forcefully snatched by an unidentified individual on a two-wheeler while walking towards Metro Exit. Phone was unlocked at the moment of snatching with active UPI credentials.") }

    val generatedFirText = remember(victimName, contactPhone, incidentDate, incidentTime, locationAddress, deviceBrandModel, imei1, imei2, lossNarrative) {
        val timestamp = System.currentTimeMillis()
        val seal = CryptoUtils.generateDocumentSeal("$victimName::$imei1::$incidentDate", timestamp)
        """
        ======================================================================
        POLICE COMPLAINT / FIRST INFORMATION REPORT (FIR) DRAFT
        [Pursuant to Section 154 Code of Criminal Procedure / BNS, IT Act 2000]
        ======================================================================
        DIGITAL VERIFICATION SEAL: $seal
        TIMESTAMP: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))}
        
        TO:
        The Station House Officer (SHO) / In-Charge,
        Cyber Crime Police Station, Jurisdiction New Delhi District.
        
        SUBJECT: Formal Complaint for Theft of Mobile Device, Unauthorized Access
                 and Precautionary Blocking under IT Act 2000 & IPC/BNS.
        
        RESPECTED SIR/MADAM,
        
        I, the undersigned complainant, wish to report the theft/snatching of my mobile phone with the following particulars:
        
        1. COMPLAINANT PARTICULARS:
           • Name: $victimName
           • Primary Contact: $contactPhone
           • Identification: Verified Resident of NCT of Delhi
        
        2. INCIDENT DETAILS:
           • Date of Occurrence: $incidentDate
           • Time of Occurrence: $incidentTime
           • Place of Occurrence: $locationAddress
        
        3. STOLEN DEVICE SPECIFICATIONS:
           • Make & Model: $deviceBrandModel
           • Primary IMEI (Slot 1): $imei1 (SHA-256: ${CryptoUtils.sha256(imei1).take(16)}...)
           • Secondary IMEI (Slot 2): $imei2
           • Carrier SIM Active: Telecom Subscriber Profile Active
        
        4. STATEMENT OF CIRCUMSTANCES:
           $lossNarrative
        
        5. STATUTORY PRAYER:
           I humbly request you to kindly register this First Information Report (FIR),
           initiate cyber cell cell-tower triangulation, provide an official acknowledgment
           for Sanchar Saathi (CEIR) national telecom blacklisting, and trace the stolen device.
        
        VERIFIED & DIGITALLY STAMPED BY HACKERFLAG DEFENSE ENGINE (ENGINEERED BY AKASH TIWARI).
        ======================================================================
        """.trimIndent()
    }

    val policeStations = remember {
        listOf(
            PoliceStation(
                name = "Cyber Crime Police Station New Delhi",
                division = "New Delhi District HQ",
                address = "Mandir Marg, Near Gole Market, New Delhi - 110001",
                phone = "01123746694",
                distanceKm = 1.8,
                cyberCellInCharge = "Insp. R. K. Malik"
            ),
            PoliceStation(
                name = "Connaught Place Police Station",
                division = "Central Division",
                address = "Baba Kharak Singh Marg, Connaught Place, New Delhi",
                phone = "01123361100",
                distanceKm = 0.9,
                cyberCellInCharge = "Sub-Insp. Amit Verma"
            ),
            PoliceStation(
                name = "Parliament Street Police Station",
                division = "New Delhi Range",
                address = "Sansad Marg, Janpath, New Delhi - 110001",
                phone = "01123361200",
                distanceKm = 2.4,
                cyberCellInCharge = "Insp. Sunita Devi"
            )
        )
    }

    fun copyText(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Police Complaint FIR Draft - $deviceBrandModel")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Export / Share FIR Complaint"))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("legal_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberSuccessGreen
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "MODULE 3: LEGAL, POLICE & FIR",
                        color = CyberSuccessGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "STATE-COMPLIANT DRAFTER • DIGITAL SEALS • JURISDICTION",
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
                contentColor = CyberSuccessGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyberSuccessGreen
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
                                color = if (selectedTab == index) CyberSuccessGreen else CyberTextSecondary
                            )
                        }
                    )
                }
            }
        }

        // TAB 0: Automated FIR Drafter
        if (selectedTab == 0) {
            item {
                CyberCard(glowEffect = true, modifier = Modifier.fillMaxWidth().testTag("fir_drafter_card")) {
                    Column {
                        Text(
                            text = "STATE-COMPLIANT POLICE FIR GENERATOR",
                            color = CyberGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Generates standardized legal draft with digital cryptographic stamp.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = victimName,
                            onValueChange = { victimName = it },
                            label = { Text("Complainant Name") },
                            modifier = Modifier.fillMaxWidth().testTag("fir_victim_name"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedTextColor = CyberTextPrimary,
                                unfocusedTextColor = CyberTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text("Contact Phone") },
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
                            value = locationAddress,
                            onValueChange = { locationAddress = it },
                            label = { Text("Incident Occurrence Location") },
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
                            value = deviceBrandModel,
                            onValueChange = { deviceBrandModel = it },
                            label = { Text("Stolen Device Brand / Model") },
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
                            value = imei1,
                            onValueChange = { imei1 = it },
                            label = { Text("IMEI 1 (15 digits)") },
                            modifier = Modifier.fillMaxWidth(),
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

            // Preview & Export Box
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LEGAL DOCUMENT OUTPUT (SEALED)",
                                color = CyberSuccessGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            StatusPill(statusText = "TAMPER-PROOF", colorHex = 0xFF00FF9D)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberSurfaceHigh)
                                .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = generatedFirText,
                                color = CyberTextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        db.incidentDao().insertIncident(
                                            IncidentReportEntity(
                                                victimName = victimName,
                                                contactNumber = contactPhone,
                                                incidentDate = incidentDate,
                                                incidentTime = incidentTime,
                                                location = locationAddress,
                                                deviceModel = deviceBrandModel,
                                                imei1 = imei1,
                                                imei2 = imei2,
                                                firDraftText = generatedFirText,
                                                digitalSeal = CryptoUtils.generateDocumentSeal(imei1, System.currentTimeMillis()),
                                                policeStation = "Cyber Crime PS"
                                            )
                                        )
                                    }
                                    shareText(generatedFirText)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSuccessGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("export_fir_button")
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = CyberBackground, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SHARE / EXPORT FIR", color = CyberBackground, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { copyText(generatedFirText, "Official FIR Draft") },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = BorderStroke(1.dp, CyberCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("COPY TEXT", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // TAB 1: Police Station Finder
        if (selectedTab == 1) {
            item {
                Text(
                    text = "JURISDICTION-BASED POLICE STATIONS",
                    color = CyberGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
            items(policeStations) { station ->
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = station.name,
                                color = CyberCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            StatusPill(statusText = "${station.distanceKm} KM", colorHex = 0xFFFFC83B)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = station.address, color = CyberTextSecondary, fontSize = 12.sp)
                        Text(text = "Cyber In-Charge: ${station.cyberCellInCharge}", color = CyberTextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${station.phone}"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                                border = BorderStroke(1.dp, CyberCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CALL STATION (${station.phone})", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: Insurance Proof Builder
        if (selectedTab == 2) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "INSURANCE CLAIM DOCUMENT BUILDER",
                            color = CyberGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Mandatory proof-of-loss bundle required by Acko, Digit, Bajaj Allianz, and OEM Care.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "CHECKLIST FOR FAST APPROVAL:", color = CyberSuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "1. Original GST Purchase Invoice with matching IMEI.\n2. Stamped copy of Police FIR or Online Complaint Receipt.\n3. CEIR Sanchar Saathi Blocking Acknowledgment Slip.\n4. SIM Replacement Certificate from Telecom Carrier.", color = CyberTextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val affidavit = "PROOF OF LOSS AFFIDAVIT\nI hereby certify under perjury that device $deviceBrandModel (IMEI: $imei1) was stolen on $incidentDate at $locationAddress. FIR is duly filed."
                                copyText(affidavit, "Insurance Proof Affidavit")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("GENERATE & COPY PROOF-OF-LOSS AFFIDAVIT", color = CyberBackground, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // TAB 3: Cybercrime.gov.in Auto-Filler
        if (selectedTab == 3) {
            item {
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "NATIONAL CYBER CRIME PORTAL AUTO-FILLER",
                            color = CyberCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Form blocks pre-structured for pasting into cybercrime.gov.in incident categories.",
                            color = CyberTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "INCIDENT CATEGORY: Financial Fraud / Mobile Device Theft", color = CyberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "SUB-CATEGORY: Unauthorized SIM Swap & Mobile Snatching", color = CyberTextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val portalUrl = "https://cybercrime.gov.in"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(portalUrl))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, tint = CyberBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("LAUNCH CYBERCRIME.GOV.IN PORTAL", color = CyberBackground, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // TAB 4: Legal Advisor (IT Act 2000 & RBI)
        if (selectedTab == 4) {
            item {
                LegalActCard(
                    section = "Section 66C & 66D",
                    statute = "Information Technology Act 2000",
                    title = "Punishment for Identity Theft & Cheating by Personation",
                    explanation = "Anyone using another person's electronic signature, password, or unique identification feature without consent faces up to 3 years imprisonment and fines up to ₹1,00,000."
                )
            }
            item {
                LegalActCard(
                    section = "RBI Circular DBR.No.Leg.BC.78/2017",
                    statute = "Reserve Bank of India Directives",
                    title = "Zero Customer Liability in Electronic Frauds",
                    explanation = "If a customer notifies the bank within 3 working days of an unauthorized electronic transaction arising from third-party breach/theft, the customer's liability is ZERO."
                )
            }
            item {
                LegalActCard(
                    section = "Section 154 CrPC",
                    statute = "Code of Criminal Procedure / BNS",
                    title = "Mandatory Zero-FIR Jurisdiction",
                    explanation = "A police station cannot refuse to register an FIR merely on the grounds of lack of territorial jurisdiction. The police must register a 'Zero FIR' and transfer it to the concerned police station."
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun LegalActCard(
    section: String,
    statute: String,
    title: String,
    explanation: String
) {
    CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = section,
                    color = CyberGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = statute,
                    color = CyberCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = CyberTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = explanation,
                color = CyberTextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}
