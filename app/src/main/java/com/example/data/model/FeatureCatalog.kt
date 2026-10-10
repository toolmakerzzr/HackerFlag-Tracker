package com.example.data.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ScreenState

data class FeatureItem(
    val id: String,
    val title: String,
    val description: String,
    val category: FeatureCategory,
    val targetScreen: ScreenState,
    val targetTab: Int = 0,
    val keywords: List<String>,
    val badgeLabel: String,
    val isEmergencyAction: Boolean = false
)

enum class FeatureCategory(val label: String, val emoji: String) {
    ALL("All Features", "🌐"),
    EMERGENCY("Emergency", "🚨"),
    BANKING("Banking & UPI", "💳"),
    IMEI_TRACKING("IMEI & Radar", "📱"),
    LEGAL("Police & Legal", "⚖️"),
    REMOTE_DEFENSE("Cloud Defense", "🔐"),
    B2B_TOOLS("B2B & QR", "🛡️")
}

object FeatureCatalog {

    val allFeatures = listOf(
        // Emergency & Golden Hour
        FeatureItem(
            id = "active_theft_panic",
            title = "Active Theft Golden-Hour Panic Mode",
            description = "Interactive 60-min countdown stepper prioritizing Bank Freeze, SIM Lock, CEIR and Zero-FIR.",
            category = FeatureCategory.EMERGENCY,
            targetScreen = ScreenState.ACTIVE_THEFT,
            targetTab = 0,
            keywords = listOf("theft", "stolen", "snatched", "chori", "panic", "emergency", "golden hour", "lockdown"),
            badgeLabel = "CRITICAL",
            isEmergencyAction = true
        ),
        FeatureItem(
            id = "ussd_freeze",
            title = "One-Click USSD Banking Freeze (*99*4*1#)",
            description = "Universal national USSD gateway instantly disables linked UPI & BHIM from any phone without internet.",
            category = FeatureCategory.BANKING,
            targetScreen = ScreenState.BANKING,
            targetTab = 0,
            keywords = listOf("ussd", "*99#", "*99*4*1#", "bank", "freeze", "upi", "bhim", "dial", "offline"),
            badgeLabel = "USSD GATEWAY",
            isEmergencyAction = true
        ),
        FeatureItem(
            id = "cyber_helpline_1930",
            title = "1930 National Cyber Crime Helpline Router",
            description = "Direct 1-tap call router to Ministry of Home Affairs 24x7 cyber financial fraud management system.",
            category = FeatureCategory.BANKING,
            targetScreen = ScreenState.BANKING,
            targetTab = 0,
            keywords = listOf("1930", "helpline", "call", "police", "cyber crime", "complaint", "fraud", "mha"),
            badgeLabel = "24x7 HOTLINE",
            isEmergencyAction = true
        ),
        FeatureItem(
            id = "multi_bank_freeze",
            title = "Multi-Bank Account Freeze Automation",
            description = "Guided emergency hotlines and SMS stop syntax for SBI, HDFC, ICICI, Axis, and PNB accounts.",
            category = FeatureCategory.BANKING,
            targetScreen = ScreenState.BANKING,
            targetTab = 1,
            keywords = listOf("sbi", "hdfc", "icici", "axis", "pnb", "debit card", "atm", "netbanking", "sms stop"),
            badgeLabel = "BANKS"
        ),
        FeatureItem(
            id = "upi_kill",
            title = "UPI Payment Gateway Deactivation",
            description = "Emergency kill desk hotlines and device deregistration guides for PhonePe, Google Pay, Paytm, and BHIM.",
            category = FeatureCategory.BANKING,
            targetScreen = ScreenState.BANKING,
            targetTab = 2,
            keywords = listOf("phonepe", "gpay", "google pay", "paytm", "bhim", "upi", "vpa", "deactivate", "kill"),
            badgeLabel = "UPI APPS"
        ),
        FeatureItem(
            id = "cards_crypto",
            title = "Card Networks & Crypto Panic Lock",
            description = "Toll-free emergency hotlisting for Visa and Mastercard, plus Web3 wallet allowance revocation via revoke.cash.",
            category = FeatureCategory.BANKING,
            targetScreen = ScreenState.BANKING,
            targetTab = 3,
            keywords = listOf("visa", "mastercard", "credit card", "crypto", "metamask", "trust wallet", "revoke", "allowance"),
            badgeLabel = "CARDS & CRYPTO"
        ),
        FeatureItem(
            id = "autopay_audit",
            title = "Auto-Pay Mandate Revocation Tracker",
            description = "Interactive audit checklist to review and pause recurring auto-debits for Netflix, Amazon, and utility billers.",
            category = FeatureCategory.BANKING,
            targetScreen = ScreenState.BANKING,
            targetTab = 4,
            keywords = listOf("autopay", "mandate", "recurring", "sip", "netflix", "amazon pay", "utility"),
            badgeLabel = "AUDIT"
        ),

        // IMEI & Radar
        FeatureItem(
            id = "cell_radar",
            title = "Cell-Tower IMEI Triangulation Radar",
            description = "Visual GIS radar sweep simulating connected cell tower sectors, signal strengths (dBm), and uncertainty radius.",
            category = FeatureCategory.IMEI_TRACKING,
            targetScreen = ScreenState.IMEI_RADAR,
            targetTab = 0,
            keywords = listOf("radar", "cell tower", "triangulation", "signal", "dbm", "location", "gis", "coordinates", "track"),
            badgeLabel = "GIS RADAR"
        ),
        FeatureItem(
            id = "ceir_portal",
            title = "CEIR / Sanchar Saathi IMEI Blacklist Portal",
            description = "Official Indian Government telecom portal workflow to block stolen IMEI across all national cell towers.",
            category = FeatureCategory.IMEI_TRACKING,
            targetScreen = ScreenState.IMEI_RADAR,
            targetTab = 1,
            keywords = listOf("ceir", "sanchar saathi", "imei", "block", "blacklist", "dot", "telecom", "gov.in"),
            badgeLabel = "CEIR PORTAL",
            isEmergencyAction = true
        ),
        FeatureItem(
            id = "zk_hasher",
            title = "Zero-Knowledge SHA-256 IMEI Hasher",
            description = "Client-side cryptographic tool calculating salted commitments and SHA-256 digests to protect raw IMEI privacy.",
            category = FeatureCategory.IMEI_TRACKING,
            targetScreen = ScreenState.IMEI_RADAR,
            targetTab = 2,
            keywords = listOf("zero knowledge", "sha-256", "hasher", "privacy", "salt", "commitment", "encrypt", "imei"),
            badgeLabel = "ZERO-KNOWLEDGE"
        ),
        FeatureItem(
            id = "public_registry",
            title = "Public Stolen Device Registry Lookup",
            description = "Searchable database for secondhand buyers to check if an IMEI or hash is marked CLEAN or STOLEN.",
            category = FeatureCategory.IMEI_TRACKING,
            targetScreen = ScreenState.IMEI_RADAR,
            targetTab = 3,
            keywords = listOf("registry", "search", "secondhand", "buyer", "stolen check", "clean", "impound"),
            badgeLabel = "REGISTRY"
        ),
        FeatureItem(
            id = "sim_watchdog",
            title = "SIM Swap & Unplug Detection Watchdog",
            description = "Monitors carrier IMSI changes and alerts on unauthorized SIM removals or tower swaps.",
            category = FeatureCategory.IMEI_TRACKING,
            targetScreen = ScreenState.IMEI_RADAR,
            targetTab = 4,
            keywords = listOf("sim swap", "unplug", "imsi", "carrier", "watchdog", "detection", "airtel", "jio"),
            badgeLabel = "TELEMETRY"
        ),
        FeatureItem(
            id = "silent_ping",
            title = "Silent Emergency Ping Protocol",
            description = "Dispatches background encrypted device telemetry pings without alerting the unauthorized holder.",
            category = FeatureCategory.IMEI_TRACKING,
            targetScreen = ScreenState.IMEI_RADAR,
            targetTab = 0,
            keywords = listOf("silent ping", "background", "dispatch", "telemetry", "stealth", "covert"),
            badgeLabel = "STEALTH"
        ),
        FeatureItem(
            id = "device_risk_engine",
            title = "Device Risk Scoring Engine (0-100)",
            description = "Dynamic threat score evaluating elapsed loss time, SIM state, lock credentials, and account sensitivity.",
            category = FeatureCategory.IMEI_TRACKING,
            targetScreen = ScreenState.IMEI_RADAR,
            targetTab = 4,
            keywords = listOf("risk", "score", "vulnerability", "exposure", "metric", "threat level"),
            badgeLabel = "RISK ENGINE"
        ),

        // Legal, Police & FIR
        FeatureItem(
            id = "police_fir_draft",
            title = "Automated Police FIR Draft Generator",
            description = "Prepares state-compliant complaint affidavit with victim info, IMEI particulars, and CrPC/BNS sections.",
            category = FeatureCategory.LEGAL,
            targetScreen = ScreenState.LEGAL_FIR,
            targetTab = 0,
            keywords = listOf("fir", "police", "complaint", "draft", "zero fir", "affidavit", "crpc", "bns", "sho"),
            badgeLabel = "FIR DRAFTER",
            isEmergencyAction = true
        ),
        FeatureItem(
            id = "fir_export_seal",
            title = "Digital Tamper Seal & Instant FIR Export",
            description = "Generates tamper-proof SHA-256 digital stamp for legal submission, ready to copy, share, or print.",
            category = FeatureCategory.LEGAL,
            targetScreen = ScreenState.LEGAL_FIR,
            targetTab = 0,
            keywords = listOf("seal", "export", "share", "pdf", "print", "stamp", "signature", "tamper-proof"),
            badgeLabel = "SEALED DOC"
        ),
        FeatureItem(
            id = "police_stations",
            title = "Jurisdiction-Based Police Station Finder",
            description = "Find nearest Cyber Crime police stations with phone numbers, jurisdiction in-charge, and distance.",
            category = FeatureCategory.LEGAL,
            targetScreen = ScreenState.LEGAL_FIR,
            targetTab = 1,
            keywords = listOf("police station", "jurisdiction", "sho", "cyber cell", "near me", "address", "phone"),
            badgeLabel = "STATION FINDER"
        ),
        FeatureItem(
            id = "insurance_proof",
            title = "Mobile Theft Insurance Proof Affidavit",
            description = "Formatted proof-of-loss affidavit bundle required for claims with Acko, Digit, Bajaj, and OEM care.",
            category = FeatureCategory.LEGAL,
            targetScreen = ScreenState.LEGAL_FIR,
            targetTab = 2,
            keywords = listOf("insurance", "claim", "proof of loss", "affidavit", "acko", "digit", "compensation"),
            badgeLabel = "INSURANCE"
        ),
        FeatureItem(
            id = "cybercrime_portal_autofill",
            title = "National Cyber Crime Portal (cybercrime.gov.in) Auto-Filler",
            description = "Pre-organizes stolen device data into formatted fields ready to paste into cybercrime.gov.in.",
            category = FeatureCategory.LEGAL,
            targetScreen = ScreenState.LEGAL_FIR,
            targetTab = 3,
            keywords = listOf("cybercrime.gov.in", "portal", "auto fill", "incident report", "online complaint"),
            badgeLabel = "PORTAL FILLER"
        ),
        FeatureItem(
            id = "legal_advisor",
            title = "Consumer Rights & IT Act 2000 Legal Advisor",
            description = "Legal breakdowns of IT Act Sections 66C, 66D, 85 and RBI Zero Customer Liability within 3 days.",
            category = FeatureCategory.LEGAL,
            targetScreen = ScreenState.LEGAL_FIR,
            targetTab = 4,
            keywords = listOf("legal rights", "it act", "66c", "66d", "rbi", "zero liability", "fraud protection", "law"),
            badgeLabel = "IT ACT 2000"
        ),

        // Remote Cloud & Account Defense
        FeatureItem(
            id = "whatsapp_kill",
            title = "Remote WhatsApp Account Deactivation Mailer",
            description = "Pre-composed official dispatch to support@whatsapp.com to terminate sessions and block OTP hijacks.",
            category = FeatureCategory.REMOTE_DEFENSE,
            targetScreen = ScreenState.REMOTE_DEFENSE,
            targetTab = 0,
            keywords = listOf("whatsapp", "deactivate", "logout", "email", "support@whatsapp.com", "session kill"),
            badgeLabel = "WHATSAPP KILL",
            isEmergencyAction = true
        ),
        FeatureItem(
            id = "google_apple_killswitch",
            title = "Google & Apple ID Emergency Kill-Switch",
            description = "Direct web console launchers for Google Find My Device and Apple iCloud Find My to initiate remote wipe.",
            category = FeatureCategory.REMOTE_DEFENSE,
            targetScreen = ScreenState.REMOTE_DEFENSE,
            targetTab = 0,
            keywords = listOf("google", "apple", "find my", "android find", "icloud", "remote wipe", "erase", "kill switch"),
            badgeLabel = "CLOUD WIPE"
        ),
        FeatureItem(
            id = "social_media_shield",
            title = "Social Media Account Hijack Shield",
            description = "Fast session-kill guides for Instagram, Facebook, X (Twitter), and LinkedIn to revoke phone access.",
            category = FeatureCategory.REMOTE_DEFENSE,
            targetScreen = ScreenState.REMOTE_DEFENSE,
            targetTab = 0,
            keywords = listOf("social media", "instagram", "facebook", "twitter", "x", "linkedin", "hijack", "logout"),
            badgeLabel = "SOCIAL SHIELD"
        ),
        FeatureItem(
            id = "vault_lockout",
            title = "Password Manager & MFA Vault Lockout",
            description = "Instructions and links to deauthorize Bitwarden, 1Password, and TOTP mobile sessions remotely.",
            category = FeatureCategory.REMOTE_DEFENSE,
            targetScreen = ScreenState.REMOTE_DEFENSE,
            targetTab = 0,
            keywords = listOf("password manager", "bitwarden", "1password", "authenticator", "mfa", "totp", "vault"),
            badgeLabel = "VAULT LOCK"
        ),

        // B2B & Ownership QR
        FeatureItem(
            id = "ownership_qr",
            title = "Tamper-Proof Asset Ownership QR Generator",
            description = "Canvas-rendered QR code encoding IMEI SHA-256 hash, invoice details, and cryptographic digital stamp.",
            category = FeatureCategory.B2B_TOOLS,
            targetScreen = ScreenState.B2B_QR,
            targetTab = 0,
            keywords = listOf("qr", "qr code", "ownership", "invoice", "asset", "proof", "tamper proof", "canvas"),
            badgeLabel = "OWNERSHIP QR"
        ),
        FeatureItem(
            id = "b2b_api_console",
            title = "B2B Refurbished Retailer REST API Console",
            description = "Interactive endpoint tester (POST /api/v1/registry/verify-device) for phone sellers to verify stolen status.",
            category = FeatureCategory.B2B_TOOLS,
            targetScreen = ScreenState.B2B_QR,
            targetTab = 1,
            keywords = listOf("b2b", "api", "console", "rest", "retailer", "pawnshop", "refurbished", "json", "endpoint"),
            badgeLabel = "B2B REST API"
        ),
        FeatureItem(
            id = "threaded_proxy_tunnel",
            title = "Threaded Proxy Server (Port 8080) & IPv4 Tunnel",
            description = "Low-latency threaded proxy architecture monitor routing packets via IPv4 to bypass mobile DNS lag.",
            category = FeatureCategory.B2B_TOOLS,
            targetScreen = ScreenState.B2B_QR,
            targetTab = 2,
            keywords = listOf("proxy", "port 8080", "ipv4", "tunnel", "dns", "latency", "offline resilience", "network"),
            badgeLabel = "PROXY & IPV4"
        ),
        FeatureItem(
            id = "evidentiary_timeline",
            title = "Immutable Evidentiary Timeline Ledger",
            description = "Room-backed cryptographic event ledger tracking theft reporting, bank freezes, and CEIR submissions.",
            category = FeatureCategory.EMERGENCY,
            targetScreen = ScreenState.DASHBOARD,
            targetTab = 0,
            keywords = listOf("timeline", "ledger", "evidence", "log", "history", "events", "audit trail"),
            badgeLabel = "AUDIT LEDGER"
        )
    )

    fun search(query: String, category: FeatureCategory = FeatureCategory.ALL): List<FeatureItem> {
        val q = query.trim().lowercase()
        return allFeatures.filter { item ->
            val matchesCategory = category == FeatureCategory.ALL || item.category == category
            val matchesQuery = if (q.isBlank()) true else {
                item.title.lowercase().contains(q) ||
                item.description.lowercase().contains(q) ||
                item.badgeLabel.lowercase().contains(q) ||
                item.keywords.any { it.contains(q) }
            }
            matchesCategory && matchesQuery
        }
    }
}
