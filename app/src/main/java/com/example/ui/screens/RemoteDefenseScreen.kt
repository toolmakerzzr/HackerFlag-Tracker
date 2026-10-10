package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

@Composable
fun RemoteDefenseScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    var phoneNumberInput by remember { mutableStateOf("+91 98765 43210") }

    fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {}
    }

    fun sendWhatsAppDeactivateEmail() {
        val subject = "Lost/Stolen: Please deactivate my account"
        val body = "Hello WhatsApp Support,\n\nMy mobile phone has been stolen. Please deactivate my WhatsApp account linked to the telephone number: $phoneNumberInput immediately to prevent unauthorized access or scam distribution.\n\nThank you,\nComplainant"
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@whatsapp.com")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No email client installed. Mail to: support@whatsapp.com", Toast.LENGTH_LONG).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("remote_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CyberAlertCrimson
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "MODULE 4: REMOTE IDENTITY DEFENSE",
                        color = CyberAlertCrimson,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "SESSION TERMINATION • CLOUD KILL-SWITCH • MFA REVOCATION",
                        color = CyberTextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // 1. WhatsApp Remote Logout Protocol
        item {
            CyberCard(glowEffect = true, modifier = Modifier.fillMaxWidth().testTag("whatsapp_kill_card")) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REMOTE WHATSAPP DEACTIVATION",
                            color = CyberGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        StatusPill(statusText = "HIGH PRIORITY", colorHex = 0xFFFF2A55)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thieves use active WhatsApp sessions to message family asking for emergency funds via UPI. Dispatches official deactivation dispatch to WhatsApp Headquarters.",
                        color = CyberTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = phoneNumberInput,
                        onValueChange = { phoneNumberInput = it },
                        label = { Text("International Phone (e.g. +91XXXXXXXXXX)") },
                        modifier = Modifier.fillMaxWidth().testTag("whatsapp_phone_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = CyberTextPrimary,
                            unfocusedTextColor = CyberTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { sendWhatsAppDeactivateEmail() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberAlertCrimson),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("whatsapp_email_button")
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DISPATCH WHATSAPP ACCOUNT TERMINATION EMAIL", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // 2. Google & Apple Cloud Kill-Switch
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "GOOGLE & APPLE ID CLOUD KILL-SWITCH",
                        color = CyberCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Remotely sign out all active browser sessions, revoke OAuth authorization grants, and activate remote wipe.",
                        color = CyberTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { openUrl("https://myaccount.google.com/device-activity") },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                            border = BorderStroke(1.dp, CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("GOOGLE DEVICE ACTIVITY", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { openUrl("https://www.icloud.com/find") },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                            border = BorderStroke(1.dp, CyberGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("APPLE ICLOUD FIND MY", color = CyberGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. Social Media Hijack Shield
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "SOCIAL MEDIA ACCOUNT HIJACK SHIELD",
                        color = CyberTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Terminate active mobile sessions from desktop browser settings:",
                        color = CyberTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Instagram: Settings -> Accounts Center -> Password & Security -> 'Where you're logged in' -> Log out of stolen phone.", color = CyberTextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "• Facebook: Settings -> Security & Login -> 'Log out of all sessions'.", color = CyberTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "• X / Twitter: Settings -> Security and account access -> Apps and sessions -> Log out other sessions.", color = CyberTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "• LinkedIn: Settings -> Sign in & Security -> Where you're signed in -> End session.", color = CyberTextSecondary, fontSize = 11.sp)
                }
            }
        }

        // 4. MFA & Password Manager Vault Lockout
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = CyberGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PASSWORD MANAGER & MFA VAULT LOCKOUT",
                            color = CyberGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Bitwarden: Log in to Web Vault -> Account Settings -> Security -> 'Deauthorize Sessions' (invalidates mobile biometric unlock).\n• 1Password: Sign in to 1Password.com -> My Profile -> Authorized Devices -> Remove lost phone.\n• Google Authenticator: Re-sync seed keys on a secondary trusted device.",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { openUrl("https://vault.bitwarden.com/#/settings/security") },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceHigh),
                        border = BorderStroke(1.dp, CyberGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("OPEN BITWARDEN DEAUTHORIZATION PORTAL", color = CyberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
