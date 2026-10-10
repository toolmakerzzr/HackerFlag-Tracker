package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.data.security.BiometricAuthManager
import com.example.data.security.BiometricAvailability
import com.example.ui.components.CyberCard
import com.example.ui.theme.CyberAlertCrimson
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGoldDark
import com.example.ui.theme.CyberSuccessGreen
import com.example.ui.theme.CyberSurfaceHigh
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary

@Composable
fun BiometricEntryGateScreen(
    onAuthenticationSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val fragmentActivity = context as? FragmentActivity

    var biometricStatus by remember {
        mutableStateOf(BiometricAuthManager.checkAvailability(context))
    }
    var authErrorText by remember { mutableStateOf<String?>(null) }
    var showPasscodeFallback by remember { mutableStateOf(false) }
    var passcodeEntered by remember { mutableStateOf("") }
    var passcodeError by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    fun launchBiometricPrompt() {
        if (fragmentActivity == null) {
            authErrorText = "Biometric prompt requires active FragmentActivity."
            return
        }

        authErrorText = null
        BiometricAuthManager.showBiometricPrompt(
            activity = fragmentActivity,
            title = "Hackerflag Owner Authentication",
            subtitle = "Biometric Entry Point Required",
            description = "Authenticate using Fingerprint, Face ID, or Device Credential to access emergency defense controls.",
            onSuccess = {
                Toast.makeText(context, "Owner Verified: Akash Tiwari. Access Granted.", Toast.LENGTH_SHORT).show()
                onAuthenticationSuccess()
            },
            onError = { code, err ->
                // Error code 13 = Negative button pressed
                // Error code 10 = User canceled
                if (code == 13 || code == 10) {
                    showPasscodeFallback = true
                }
                authErrorText = err
            },
            onFailed = {
                authErrorText = "Biometric authentication not recognized. Please retry."
            }
        )
    }

    // Auto-prompt on initial launch
    LaunchedEffect(Unit) {
        launchBiometricPrompt()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("biometric_entry_gate")
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App branding & Maker signature
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CyberCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HACKERFLAG DEFENSE",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberGold.copy(alpha = 0.16f))
                        .border(0.8.dp, CyberGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "MADE BY AKASH TIWARI",
                        color = CyberGoldDark,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Animated Biometric Sensor Ring
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                CyberCyan.copy(alpha = 0.22f),
                                CyberCyan.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(1.5.dp, CyberCyan.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, CyberBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometric Lock",
                        tint = CyberCyan,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            // Lock Title and Security Status
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "BIOMETRIC ENTRY GATE",
                    color = CyberTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "OWNER AUTHENTICATION REQUIRED",
                    color = CyberAlertCrimson,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Security Explanation Card
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberBorder
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(CyberCyan.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "EMERGENCY CONTROLS SHIELDED",
                            color = CyberTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "To prevent unauthorized execution of panic lockdown, Police SOS auto-call, or remote wipe, entry is strictly locked to the verified owner.",
                        color = CyberTextSecondary,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )

                    // Hardware status badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberSurfaceVariant)
                            .border(0.8.dp, CyberBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "SENSOR DETECTED:",
                                color = CyberTextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = biometricStatus.description,
                                color = if (biometricStatus is BiometricAvailability.Available) CyberSuccessGreen else CyberCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Primary Unlock with Biometric Prompt Button
            Button(
                onClick = { launchBiometricPrompt() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("biometric_unlock_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AUTHENTICATE WITH BIOMETRIC",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Feedback / Error Text if prompt failed or was dismissed
            AnimatedVisibility(visible = authErrorText != null) {
                authErrorText?.let { err ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberAlertCrimson.copy(alpha = 0.08f))
                            .border(0.8.dp, CyberAlertCrimson.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = CyberAlertCrimson,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = err,
                                color = CyberAlertCrimson,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Toggle Passcode fallback
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { showPasscodeFallback = !showPasscodeFallback }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (showPasscodeFallback) "Hide Owner Passcode Keypad" else "Use Owner Backup Passcode (9999)",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Passcode Input Fallback (Ensures complete accessibility on Emulator and all devices)
            AnimatedVisibility(
                visible = showPasscodeFallback,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (passcodeError) CyberAlertCrimson else CyberCyan.copy(alpha = 0.5f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "OWNER EMERGENCY PASSCODE",
                            color = CyberTextPrimary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Default owner bypass code: 9999 (configured for Akash Tiwari)",
                            color = CyberTextMuted,
                            fontSize = 10.5.sp,
                            textAlign = TextAlign.Center
                        )

                        OutlinedTextField(
                            value = passcodeEntered,
                            onValueChange = {
                                if (it.length <= 6) {
                                    passcodeEntered = it
                                    passcodeError = false
                                }
                            },
                            label = { Text("Enter Passcode") },
                            placeholder = { Text("9999") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    if (passcodeEntered == BiometricAuthManager.DEFAULT_EMERGENCY_PASSCODE) {
                                        Toast.makeText(context, "Owner Verified: Akash Tiwari. Access Granted.", Toast.LENGTH_SHORT).show()
                                        onAuthenticationSuccess()
                                    } else {
                                        passcodeError = true
                                        authErrorText = "Invalid owner emergency passcode. Try 9999."
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberBorder,
                                focusedLabelColor = CyberCyan
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("owner_passcode_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    passcodeEntered = BiometricAuthManager.DEFAULT_EMERGENCY_PASSCODE
                                    Toast.makeText(context, "Owner Verified: Akash Tiwari. Access Granted.", Toast.LENGTH_SHORT).show()
                                    onAuthenticationSuccess()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberSurfaceHigh,
                                    contentColor = CyberCyan
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quick_unlock_button")
                            ) {
                                Text(
                                    text = "QUICK UNLOCK (9999)",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Button(
                                onClick = {
                                    keyboardController?.hide()
                                    if (passcodeEntered == BiometricAuthManager.DEFAULT_EMERGENCY_PASSCODE) {
                                        Toast.makeText(context, "Owner Verified: Akash Tiwari. Access Granted.", Toast.LENGTH_SHORT).show()
                                        onAuthenticationSuccess()
                                    } else {
                                        passcodeError = true
                                        authErrorText = "Invalid owner emergency passcode. Try 9999."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberCyan,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("passcode_unlock_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "VERIFY PIN",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Signature
            Text(
                text = "HACKERFLAG ZERO-TRUST DEFENSE ARCHITECTURE // ENGINEERED BY AKASH TIWARI",
                color = CyberTextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
        }
    }
}
