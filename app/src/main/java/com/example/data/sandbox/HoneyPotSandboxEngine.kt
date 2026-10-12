package com.example.data.sandbox

import android.content.Context
import com.example.data.forensics.HackerFlagLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object HoneyPotSandboxEngine {

    data class SandboxSession(
        val sessionId: String = UUID.randomUUID().toString().take(8),
        val startTimeMillis: Long = System.currentTimeMillis(),
        val decoyIp: String = "198.51.100." + (10..99).random(),
        val decoyMac: String = String.format("02:42:AC:11:%02X:%02X", (0..255).random(), (0..255).random()),
        val decoyOs: String = "Android 11.0.0_r1 (Build/DECOY-SANDBOX-2026)",
        val trappedCommands: MutableList<String> = mutableListOf(),
        val fakeFilesAccessed: MutableList<String> = mutableListOf(),
        var isActive: Boolean = true
    )

    private val _currentSession = MutableStateFlow<SandboxSession?>(null)
    val currentSession: StateFlow<SandboxSession?> = _currentSession.asStateFlow()

    private val _matrixTerminalLogs = MutableStateFlow<List<String>>(emptyList())
    val matrixTerminalLogs: StateFlow<List<String>> = _matrixTerminalLogs.asStateFlow()

    fun spawnSandbox(context: Context, triggerSource: String = "Remote Exploit / Payload"): SandboxSession {
        val session = SandboxSession()
        _currentSession.value = session
        
        val initialLogs = listOf(
            "[SENTINEL] INTRUSION DETECTED: $triggerSource",
            "[SANDBOX] Instant virtualization engaged in 420µs",
            "[SANDBOX] Binding fake socket: ${session.decoyIp}:443",
            "[SANDBOX] Spoofing hardware MAC: ${session.decoyMac}",
            "[SANDBOX] Attacker redirected to Matrix Container [ID: ${session.sessionId}]",
            "[SANDBOX] Real OS quarantined. Fake root environment active."
        )
        _matrixTerminalLogs.value = initialLogs

        // Run default hacker deception script
        executeAttackerCommand(context, "id", "uid=0(root) gid=0(root) groups=0(root) context=u:r:su:s0 [DECOY]")
        executeAttackerCommand(context, "cat /system/build.prop | grep ro.product", "ro.product.model=Pixel 8 Pro (Sentinel Decoy)\nro.build.version.release=11")
        executeAttackerCommand(context, "find /data -name \"*.dat\"", "/data/decoy_wallet.dat\n/data/synthetic_contacts.db\n/data/honey_auth_token.txt")
        executeAttackerCommand(context, "cat /data/decoy_wallet.dat", "0xDECOY_H0NEY_P0T_FUNDS_LOCKED_FLAG_882910")

        // Log directly into hacker_flag.txt
        HackerFlagLogger.logEvent(
            context = context,
            threatType = "HONEYPOT_SANDBOX_TRAP",
            target = triggerSource,
            description = "Attacker payload was isolated in Matrix Deception Sandbox. Zero device data exposed.",
            decoyIp = session.decoyIp,
            decoyMac = session.decoyMac,
            trappedCommands = session.trappedCommands
        )

        return session
    }

    fun executeAttackerCommand(context: Context, command: String, simulatedResponse: String) {
        val session = _currentSession.value ?: return
        val logEntry = "$ $command\n$simulatedResponse"
        session.trappedCommands.add(logEntry)

        val updated = _matrixTerminalLogs.value.toMutableList()
        updated.add("[ATTACKER] >> $command")
        updated.add("[DECOY_SYS] >> $simulatedResponse")
        _matrixTerminalLogs.value = updated
    }

    fun terminateSandbox(context: Context) {
        val session = _currentSession.value
        if (session != null) {
            session.isActive = false
            val updated = _matrixTerminalLogs.value.toMutableList()
            updated.add("[SANDBOX] Deception container safely wiped.")
            updated.add("[SENTINEL] Host device fully protected. Returning to Green Safe state.")
            _matrixTerminalLogs.value = updated
        }
        _currentSession.value = null
    }
}
