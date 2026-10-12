package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.forensics.HackerFlagLogger
import com.example.ui.theme.CyberCyanAccent
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberGlassBorder
import com.example.ui.theme.CyberGlassCard
import com.example.ui.theme.CyberGreenGlow
import com.example.ui.theme.CyberGreenPrimary
import com.example.ui.theme.CyberRedAlert
import com.example.ui.theme.CyberTextDim
import com.example.ui.theme.CyberTextWhite

@Composable
fun ForensicAuditDialog(
    onDismiss: () -> Unit,
    onLogsUpdated: () -> Unit
) {
    val context = LocalContext.current
    var logContent by remember { mutableStateOf(HackerFlagLogger.readLogs(context)) }
    val file = HackerFlagLogger.getLogFile(context)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, CyberGreenPrimary.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
            color = CyberDarkBg
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .testTag("forensic_audit_dialog")
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📁", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "hacker_flag.txt",
                                color = CyberGreenGlow,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "Secure Sandbox Forensics • ${file.length()} bytes",
                            color = CyberTextDim,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_audit_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CyberTextWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions Bar: Copy, Share, Clear, Refresh
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("hacker_flag.txt", logContent)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied hacker_flag.txt to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGlassCard),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .border(1.dp, CyberGlassBorder, RoundedCornerShape(10.dp))
                            .testTag("copy_logs_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp), tint = CyberGreenGlow)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy", fontSize = 12.sp, color = CyberTextWhite)
                    }

                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "CyberShield hacker_flag.txt Forensics")
                                putExtra(Intent.EXTRA_TEXT, logContent)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Forensics Log"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGlassCard),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .border(1.dp, CyberGlassBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp), tint = CyberCyanAccent)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", fontSize = 12.sp, color = CyberTextWhite)
                    }

                    Button(
                        onClick = {
                            HackerFlagLogger.clearLogs(context)
                            logContent = HackerFlagLogger.readLogs(context)
                            onLogsUpdated()
                            Toast.makeText(context, "Forensics reset to clean baseline", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGlassCard),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .border(1.dp, CyberGlassBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", modifier = Modifier.size(16.dp), tint = CyberRedAlert)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset", fontSize = 12.sp, color = CyberTextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Terminal Raw File Viewer
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF060A12))
                        .border(1.dp, CyberGlassBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .testTag("log_content_stream")
                    ) {
                        Text(
                            text = logContent,
                            color = CyberGreenGlow,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}
