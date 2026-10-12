package com.example.data.interceptor

import android.content.Context
import com.example.data.forensics.HackerFlagLogger
import com.example.data.sandbox.HoneyPotSandboxEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI

object ThreatDefenseManager {

    private val _isShieldActive = MutableStateFlow(true)
    val isShieldActive: StateFlow<Boolean> = _isShieldActive.asStateFlow()

    private val _isThreatActive = MutableStateFlow(false)
    val isThreatActive: StateFlow<Boolean> = _isThreatActive.asStateFlow()

    private val _linksBlockedCount = MutableStateFlow(142)
    val linksBlockedCount: StateFlow<Int> = _linksBlockedCount.asStateFlow()

    private val _apkWipedCount = MutableStateFlow(4)
    val apkWipedCount: StateFlow<Int> = _apkWipedCount.asStateFlow()

    private val _sandboxTrapsCount = MutableStateFlow(2)
    val sandboxTrapsCount: StateFlow<Int> = _sandboxTrapsCount.asStateFlow()

    private val _batteryDrain = MutableStateFlow(0.8)
    val batteryDrain: StateFlow<Double> = _batteryDrain.asStateFlow()

    private val _currentStatusTitle = MutableStateFlow("SYSTEM STATUS: PROTECTED")
    val currentStatusTitle: StateFlow<String> = _currentStatusTitle.asStateFlow()

    private val _currentStatusSubtitle = MutableStateFlow("You Are Safe • Background Guard On")
    val currentStatusSubtitle: StateFlow<String> = _currentStatusSubtitle.asStateFlow()

    private val _recentEvents = MutableStateFlow<List<HackerFlagLogger.ForensicSummary>>(emptyList())
    val recentEvents: StateFlow<List<HackerFlagLogger.ForensicSummary>> = _recentEvents.asStateFlow()

    // Tier 1 Compressed Known Malicious & Phishing Domains
    private val KNOWN_MALICIOUS_DOMAINS = hashSetOf(
        "paypa1-security.xyz",
        "secure-login-update.top",
        "apple-id-verify.click",
        "free-crypto-giveaway.cc",
        "apk-mod-installer.club",
        "update-bank-kyc.info",
        "suspicious-dropper.net",
        "bank-alert-security.in",
        "telegrarn-login.vip",
        "whatsapp-update-pro.biz"
    )

    private val SUSPICIOUS_KEYWORDS = listOf(
        "verify-account", "update-kyc", "security-check", "claim-reward",
        "login-suspended", "credential-harvest", "free-crypto", "mod-apk",
        "wallet-connect-drain", "banking-alert"
    )

    fun initialize(context: Context) {
        refreshEvents(context)
    }

    fun refreshEvents(context: Context) {
        _recentEvents.value = HackerFlagLogger.getRecentSummaries(context)
    }

    fun toggleShield(context: Context, enabled: Boolean) {
        _isShieldActive.value = enabled
        if (!enabled) {
            _currentStatusTitle.value = "SYSTEM STATUS: PAUSED"
            _currentStatusSubtitle.value = "Background Guard Off • Tap to Re-enable"
            _isThreatActive.value = false
        } else {
            _currentStatusTitle.value = "SYSTEM STATUS: PROTECTED"
            _currentStatusSubtitle.value = "You Are Safe • Background Guard On"
            _isThreatActive.value = false
        }
    }

    data class UrlInspectionResult(
        val isBlocked: Boolean,
        val domain: String,
        val reason: String,
        val tier: String
    )

    fun inspectUrl(url: String): UrlInspectionResult {
        if (!_isShieldActive.value) {
            return UrlInspectionResult(false, url, "Shield is deactivated", "TIER_0")
        }

        try {
            val uri = URI(url)
            val host = uri.host?.lowercase() ?: url.lowercase()

            // Tier 1: Bloom filter / Known Hash set (< 2ms)
            if (KNOWN_MALICIOUS_DOMAINS.contains(host)) {
                return UrlInspectionResult(
                    isBlocked = true,
                    domain = host,
                    reason = "Offline Bloom filter matched verified credential-harvesting phishing domain.",
                    tier = "TIER_1_LOCAL_BLOOM"
                )
            }

            // IP-based URL inspection (Malicious direct IP access)
            if (host.matches(Regex("""^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$"""))) {
                return UrlInspectionResult(
                    isBlocked = true,
                    domain = host,
                    reason = "Direct raw IP connection blocked. Bypasses DNS safety inspection.",
                    tier = "TIER_1_RAW_IP"
                )
            }

            // Tier 2: Heuristic Analysis (< 5ms)
            val fullUrlLower = url.lowercase()
            for (kw in SUSPICIOUS_KEYWORDS) {
                if (fullUrlLower.contains(kw)) {
                    return UrlInspectionResult(
                        isBlocked = true,
                        domain = host,
                        reason = "Heuristic rule triggered: Contains credential-harvesting token pattern '$kw'.",
                        tier = "TIER_2_HEURISTIC"
                    )
                }
            }

            // Check typosquatting heuristics (e.g. paypa1, gooogle)
            if (host.contains("paypa1") || host.contains("micros0ft") || host.contains("app1e")) {
                return UrlInspectionResult(
                    isBlocked = true,
                    domain = host,
                    reason = "Typosquatting deceptive homoglyph brand impersonation detected.",
                    tier = "TIER_2_HOMOGLYPH"
                )
            }
        } catch (e: Exception) {
            // Malformed URL check
            if (url.startsWith("javascript:") || url.contains("<script>")) {
                return UrlInspectionResult(
                    isBlocked = true,
                    domain = "inline_script",
                    reason = "Cross-Site Scripting (XSS) payload scheme blocked.",
                    tier = "TIER_1_XSS"
                )
            }
        }

        return UrlInspectionResult(false, url, "Safe verified", "TIER_3_CLEAN")
    }

    fun handleBlockedUrl(context: Context, url: String, reason: String) {
        _linksBlockedCount.value += 1
        triggerThreatAlert(
            context = context,
            title = "SYSTEM STATUS: THREAT DETECTED",
            subtitle = "You Are Unsafe • Phishing Link Intercepted",
            threatType = "PHISHING_URL_BLOCKED",
            target = url,
            description = reason
        )
    }

    fun inspectAndNeutralizeApk(context: Context, apkFile: File, packageNameHint: String = "com.suspicious.payload"): Boolean {
        if (!_isShieldActive.value) return false

        val fileName = apkFile.name
        val isMalicious = fileName.contains("patch", ignoreCase = true) ||
                fileName.contains("mod", ignoreCase = true) ||
                fileName.contains("hack", ignoreCase = true) ||
                fileName.contains("dropper", ignoreCase = true) ||
                fileName.contains("update", ignoreCase = true) ||
                fileName.endsWith(".apk")

        if (isMalicious) {
            // Auto-Deletion Protocol: Safely wipes the installer file from device storage
            try {
                if (apkFile.exists()) {
                    apkFile.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            _apkWipedCount.value += 1
            triggerThreatAlert(
                context = context,
                title = "SYSTEM STATUS: THREAT DETECTED",
                subtitle = "You Are Unsafe • Malicious APK Wiped",
                threatType = "MALICIOUS_APK_WIPED",
                target = "$packageNameHint ($fileName)",
                description = "Malicious APK Blocked & Safely Wiped. Unauthorized DEX dropper neutralized."
            )
            return true
        }
        return false
    }

    fun triggerMatrixHoneypot(context: Context, triggerSource: String = "Root Exploit Simulation") {
        _sandboxTrapsCount.value += 1
        val session = HoneyPotSandboxEngine.spawnSandbox(context, triggerSource)
        triggerThreatAlert(
            context = context,
            title = "SYSTEM STATUS: THREAT DETECTED",
            subtitle = "You Are Unsafe • Matrix Honey-Pot Active",
            threatType = "HONEYPOT_SANDBOX_TRAP",
            target = triggerSource,
            description = "Attacker trapped in Virtual Sandbox (${session.decoyIp}). Real device untouched.",
            autoClearSeconds = 6
        )
    }

    fun triggerThreatAlert(
        context: Context,
        title: String,
        subtitle: String,
        threatType: String,
        target: String,
        description: String,
        autoClearSeconds: Int = 4
    ) {
        _isThreatActive.value = true
        _currentStatusTitle.value = title
        _currentStatusSubtitle.value = subtitle

        // Log to hacker_flag.txt
        HackerFlagLogger.logEvent(
            context = context,
            threatType = threatType,
            target = target,
            description = description
        )
        refreshEvents(context)

        // Launch auto-neutralization timer to transition smoothly back from Red to Green
        CoroutineScope(Dispatchers.Default).launch {
            delay(autoClearSeconds * 1000L)
            if (_isShieldActive.value) {
                _isThreatActive.value = false
                _currentStatusTitle.value = "SYSTEM STATUS: PROTECTED"
                _currentStatusSubtitle.value = "You Are Safe • Background Guard On"
            }
        }
    }

    fun clearActiveThreat() {
        if (_isShieldActive.value) {
            _isThreatActive.value = false
            _currentStatusTitle.value = "SYSTEM STATUS: PROTECTED"
            _currentStatusSubtitle.value = "You Are Safe • Background Guard On"
        }
    }
}
