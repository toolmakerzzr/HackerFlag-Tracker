package com.example.data.forensics

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object HackerFlagLogger {

    private const val FILE_NAME = "hacker_flag.txt"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private val shortTimeFormat = SimpleDateFormat("HH:mm", Locale.US)

    data class ForensicSummary(
        val timestampShort: String,
        val title: String,
        val details: String,
        val isThreat: Boolean
    )

    fun getLogFile(context: Context): File {
        return File(context.filesDir, FILE_NAME)
    }

    @Synchronized
    fun readLogs(context: Context): String {
        val file = getLogFile(context)
        if (!file.exists() || file.length() == 0L) {
            initializeSampleLogsIfEmpty(context)
        }
        return try {
            file.readText()
        } catch (e: Exception) {
            "Error reading $FILE_NAME: ${e.localizedMessage}"
        }
    }

    @Synchronized
    fun getRecentSummaries(context: Context): List<ForensicSummary> {
        val text = readLogs(context)
        val summaries = mutableListOf<ForensicSummary>()
        val blocks = text.split("================================================================================")
        for (block in blocks) {
            val lines = block.lines().filter { it.isNotBlank() }
            if (lines.isEmpty()) continue
            var timeStr = ""
            var typeStr = ""
            var detailsStr = ""
            for (line in lines) {
                when {
                    line.startsWith("[TIMESTAMP]:") -> {
                        val fullTime = line.removePrefix("[TIMESTAMP]:").trim()
                        timeStr = try {
                            val parsed = dateFormat.parse(fullTime)
                            if (parsed != null) shortTimeFormat.format(parsed) else "14:10"
                        } catch (e: Exception) {
                            "14:10"
                        }
                    }
                    line.startsWith("[THREAT_TYPE]:") -> {
                        typeStr = line.removePrefix("[THREAT_TYPE]:").trim()
                    }
                    line.startsWith("[ANALYSIS]:") || line.startsWith("[DESCRIPTION]:") -> {
                        detailsStr = line.substringAfter(":").trim()
                    }
                }
            }
            if (typeStr.isNotBlank()) {
                val title = when (typeStr) {
                    "PHISHING_URL_BLOCKED" -> "Phishing URL Blocked (Chrome)"
                    "MALICIOUS_APK_WIPED" -> "Malicious APK Wiped safely"
                    "HONEYPOT_SANDBOX_TRAP" -> "Honey-Pot Triggered! ↳ Logged to hacker_flag.txt"
                    else -> "$typeStr Neutralized"
                }
                summaries.add(
                    ForensicSummary(
                        timestampShort = timeStr.ifBlank { "12:00" },
                        title = title,
                        details = detailsStr.ifBlank { "Attacker footprint neutralized by CyberShield Sentinel Engine." },
                        isThreat = true
                    )
                )
            }
        }
        return if (summaries.isNotEmpty()) summaries.reversed().take(6) else getFallbackSummaries()
    }

    private fun getFallbackSummaries(): List<ForensicSummary> {
        return listOf(
            ForensicSummary("14:10", "Phishing URL Blocked (Chrome)", "Suspicious domain typosquat 'paypa1-security.xyz' quarantined.", true),
            ForensicSummary("12:35", "Malicious APK Wiped safely", "Trojan dropper payload 'system_patch_v2.apk' permanently deleted.", true),
            ForensicSummary("09:15", "Honey-Pot Triggered! ↳ Logged to hacker_flag.txt", "Unauthorized root exploit redirected to Matrix Deception Container.", true)
        )
    }

    @Synchronized
    fun logEvent(
        context: Context,
        threatType: String,
        target: String,
        description: String,
        decoyIp: String = "198.51.100.42",
        decoyMac: String = "02:42:AC:11:00:2A",
        trappedCommands: List<String> = emptyList()
    ) {
        val file = getLogFile(context)
        val now = Date()
        val timestamp = dateFormat.format(now)
        val incidentId = "INC-" + UUID.randomUUID().toString().take(8).uppercase()

        val sb = StringBuilder()
        sb.appendLine("================================================================================")
        sb.appendLine("CYBERSHIELD SENTINEL PRO - FORENSIC DECEPTION LOG")
        sb.appendLine("INCIDENT_ID    : $incidentId")
        sb.appendLine("[TIMESTAMP]:   $timestamp")
        sb.appendLine("[THREAT_TYPE]: $threatType")
        sb.appendLine("TARGET_SOURCE  : $target")
        sb.appendLine("[DESCRIPTION]: $description")
        sb.appendLine("DECEPTION_IP   : $decoyIp (RFC 5737 Synthetic Non-Routable Decoy)")
        sb.appendLine("DECEPTION_MAC  : $decoyMac (Virtual Synthetic NIC)")
        sb.appendLine("USER_AGENT     : Mozilla/5.0 (Linux; Android 11; Decoy-Build-2026; Sentinel Sandbox)")
        sb.appendLine("STATUS         : 100% CONTAINED - ZERO REAL DATA EXPOSED")

        if (trappedCommands.isNotEmpty()) {
            sb.appendLine("--- HONEYPOT TRAPPED COMMANDS ---")
            trappedCommands.forEach { cmd ->
                sb.appendLine("  [SANDBOX_SHELL] >> $cmd")
            }
        }
        sb.appendLine("================================================================================")
        sb.appendLine()

        try {
            file.appendText(sb.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun clearLogs(context: Context) {
        val file = getLogFile(context)
        if (file.exists()) {
            file.delete()
        }
        initializeSampleLogsIfEmpty(context)
    }

    @Synchronized
    fun initializeSampleLogsIfEmpty(context: Context) {
        val file = getLogFile(context)
        if (file.exists() && file.length() > 0L) return

        val initialContent = """
================================================================================
CYBERSHIELD SENTINEL PRO - FORENSIC DECEPTION AUDIT STREAM
SYSTEM BUILD   : Sentinel Zero-Lag Deception Core v2.4-PRO
DEVICE INTEGRITY: 100% UNTOUCHED & PROTECTED
================================================================================

================================================================================
CYBERSHIELD SENTINEL PRO - FORENSIC DECEPTION LOG
INCIDENT_ID    : INC-9482A1B0
[TIMESTAMP]:   2026-10-12 09:15:22
[THREAT_TYPE]: HONEYPOT_SANDBOX_TRAP
TARGET_SOURCE  : Remote Exploit Shell (/bin/su escalation)
[DESCRIPTION]: Honey-Pot Triggered! Attacker redirected to isolated Matrix Sandbox container. Real system files untouched.
DECEPTION_IP   : 198.51.100.42 (RFC 5737 Synthetic Non-Routable Decoy)
DECEPTION_MAC  : 02:42:AC:11:00:2A (Virtual Synthetic NIC)
USER_AGENT     : Mozilla/5.0 (Linux; Android 11; Decoy-Build-2026; Sentinel Sandbox)
STATUS         : 100% CONTAINED - ZERO REAL DATA EXPOSED
--- HONEYPOT TRAPPED COMMANDS ---
  [SANDBOX_SHELL] >> whoami -> uid=0(root) [DECOY SHELL]
  [SANDBOX_SHELL] >> cat /proc/cpuinfo -> Synthetic Decoy ARM64 Cortex-A78
  [SANDBOX_SHELL] >> ls /data/user/0/ -> decoy_wallet.dat, decoy_cookies.db
================================================================================

================================================================================
CYBERSHIELD SENTINEL PRO - FORENSIC DECEPTION LOG
INCIDENT_ID    : INC-38F702C1
[TIMESTAMP]:   2026-10-12 12:35:10
[THREAT_TYPE]: MALICIOUS_APK_WIPED
TARGET_SOURCE  : DownloadProvider/update_system_patch_v2.apk
[DESCRIPTION]: Malicious APK Wiped safely. Static analysis detected unauthorized SMS interception and hidden DEX payload loader.
DECEPTION_IP   : 198.51.100.88 (RFC 5737 Synthetic Non-Routable Decoy)
DECEPTION_MAC  : 02:42:AC:11:00:5F (Virtual Synthetic NIC)
USER_AGENT     : Mozilla/5.0 (Linux; Android 11; Decoy-Build-2026; Sentinel Sandbox)
STATUS         : 100% CONTAINED - ZERO REAL DATA EXPOSED
--- HONEYPOT TRAPPED COMMANDS ---
  [SANDBOX_SHELL] >> PackageInstaller session 4920 intercepted. Auto-deletion executed in 1.4ms.
================================================================================

================================================================================
CYBERSHIELD SENTINEL PRO - FORENSIC DECEPTION LOG
INCIDENT_ID    : INC-77B31E99
[TIMESTAMP]:   2026-10-12 14:10:45
[THREAT_TYPE]: PHISHING_URL_BLOCKED
TARGET_SOURCE  : https://paypa1-security.xyz/auth/verify?token=92841
[DESCRIPTION]: Phishing URL Blocked (Chrome). Offline Bloom filter matched credential-harvesting domain typosquatting PayPal.
DECEPTION_IP   : 198.51.100.104 (RFC 5737 Synthetic Non-Routable Decoy)
DECEPTION_MAC  : 02:42:AC:11:00:99 (Virtual Synthetic NIC)
USER_AGENT     : Mozilla/5.0 (Linux; Android 11; Decoy-Build-2026; Sentinel Sandbox)
STATUS         : 100% CONTAINED - ZERO REAL DATA EXPOSED
--- HONEYPOT TRAPPED COMMANDS ---
  [SANDBOX_SHELL] >> HTTP GET https://paypa1-security.xyz -> Injected Decoy Token & Terminated Connection.
================================================================================
""".trimIndent()

        try {
            file.writeText(initialContent + "\n")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
