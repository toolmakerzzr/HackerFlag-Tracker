package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.sandbox.HoneyPotSandboxEngine
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassCard
import com.example.ui.theme.CyberGreenGlow
import com.example.ui.theme.CyberGreenPrimary
import com.example.ui.theme.CyberRedAlert
import com.example.ui.theme.CyberRedGlow
import com.example.ui.theme.CyberTextDim
import com.example.ui.theme.CyberTextWhite

@Composable
fun MatrixSandboxDialog(
    onDismiss: () -> Unit,
    onSandboxUpdated: () -> Unit
) {
    val context = LocalContext.current
    val session by HoneyPotSandboxEngine.currentSession.collectAsState()
    val terminalLogs by HoneyPotSandboxEngine.matrixTerminalLogs.collectAsState()
    var inputCmd by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(terminalLogs.size) {
        if (terminalLogs.isNotEmpty()) {
            listState.animateScrollToItem(terminalLogs.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, CyberRedAlert.copy(alpha = 0.8f), RoundedCornerShape(24.dp)),
            color = CyberDarkBg
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .testTag("matrix_sandbox_dialog")
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🍯", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Matrix Virtual Sandbox Deception",
                                color = CyberRedGlow,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "Attacker Trapped in Decoy Shell • Real OS Untouched",
                            color = CyberTextDim,
                            fontSize = 11.5.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_sandbox_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CyberTextWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Telemetry Info Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberGlassCard)
                        .border(1.dp, CyberGlassBorder, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("DECOY IP", color = CyberTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(session?.decoyIp ?: "198.51.100.42", color = CyberGreenGlow, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    Column {
                        Text("SPOOFED MAC", color = CyberTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(session?.decoyMac ?: "02:42:AC:11:00:2A", color = CyberGreenGlow, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                    Column {
                        Text("CONTAINMENT", color = CyberTextDim, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("100% ISOLATED", color = CyberRedGlow, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Matrix Live Deception Terminal
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF05070D))
                        .border(1.dp, CyberRedAlert.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(terminalLogs) { line ->
                            val textColor = when {
                                line.startsWith("[ATTACKER]") -> CyberRedGlow
                                line.startsWith("[DECOY_SYS]") -> CyberGreenGlow
                                line.startsWith("[SANDBOX]") -> Color(0xFFFBBF24)
                                else -> CyberTextDim
                            }
                            Text(
                                text = line,
                                color = textColor,
                                fontSize = 11.5.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Command Simulator Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputCmd,
                        onValueChange = { inputCmd = it },
                        placeholder = { Text("Simulate attacker cmd (e.g. whoami, ls /data)", fontSize = 12.sp, color = CyberTextDim) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("attacker_command_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberRedGlow,
                            unfocusedBorderColor = CyberGlassBorder,
                            focusedTextColor = CyberTextWhite,
                            unfocusedTextColor = CyberTextWhite
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (inputCmd.isNotBlank()) {
                                val cmd = inputCmd.trim()
                                val response = when (cmd) {
                                    "whoami" -> "uid=0(root) gid=0(root) groups=0(root) [DECOY SHELL]"
                                    "ls /data" -> "decoy_crypto.key\ndecoy_telegram_session.db\nsynthetic_photos.bin"
                                    "uname -a" -> "Linux sentinel-sandbox-decoy 5.15.0-arm64 #1 SMP PREEMPT"
                                    "cat /proc/version" -> "Linux version 5.15.0-decoy-hardened (sentinel@build) aarch64"
                                    else -> "root@decoy-sandbox:~# $cmd\n[Simulated Exec: Trapped into hacker_flag.txt]"
                                }
                                HoneyPotSandboxEngine.executeAttackerCommand(context, cmd, response)
                                inputCmd = ""
                                onSandboxUpdated()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberRedAlert),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("send_attacker_command")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Simulation Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = {
                            HoneyPotSandboxEngine.executeAttackerCommand(
                                context,
                                "cat /data/decoy_wallet.dat",
                                "[DECOY_HONEYTOKEN: 0x94819aD04E8569... Funds Frozen: Trap Broadcasted]"
                            )
                            onSandboxUpdated()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Trap Wallet", fontSize = 11.sp, color = CyberTextWhite)
                    }

                    Button(
                        onClick = {
                            HoneyPotSandboxEngine.executeAttackerCommand(
                                context,
                                "dumpsys ip",
                                "Decoy Interface: dummy0 -> Injected IP: ${session?.decoyIp ?: "198.51.100.42"}"
                            )
                            onSandboxUpdated()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Decoy IP", fontSize = 11.sp, color = CyberTextWhite)
                    }

                    Button(
                        onClick = {
                            HoneyPotSandboxEngine.terminateSandbox(context)
                            Toast.makeText(context, "Sandbox wiped. Returning to safe state.", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Purge & Exit", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
