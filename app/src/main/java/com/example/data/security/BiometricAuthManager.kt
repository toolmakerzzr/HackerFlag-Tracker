package com.example.data.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

sealed class BiometricAvailability {
    object Available : BiometricAvailability()
    object DeviceCredentialOnly : BiometricAvailability()
    object NoneEnrolled : BiometricAvailability()
    object NoHardware : BiometricAvailability()
    object SecurityUnavailable : BiometricAvailability()

    val description: String
        get() = when (this) {
            is Available -> "Biometric Sensors Active & Enrolled"
            is DeviceCredentialOnly -> "Device PIN / Pattern Enrolled"
            is NoneEnrolled -> "No Biometrics Enrolled on Device"
            is NoHardware -> "No Biometric Sensor Detected (Emulator Mode)"
            is SecurityUnavailable -> "Biometric Hardware Temporarily Busy"
        }
}

object BiometricAuthManager {

    const val DEFAULT_EMERGENCY_PASSCODE = "9999"

    fun checkAvailability(context: Context): BiometricAvailability {
        val biometricManager = BiometricManager.from(context)

        val biometricStatus = biometricManager.canAuthenticate(BIOMETRIC_STRONG)
        if (biometricStatus == BiometricManager.BIOMETRIC_SUCCESS) {
            return BiometricAvailability.Available
        }

        val credentialStatus = biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        if (credentialStatus == BiometricManager.BIOMETRIC_SUCCESS) {
            return BiometricAvailability.DeviceCredentialOnly
        }

        return when (biometricStatus) {
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NoneEnrolled
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NoHardware
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.SecurityUnavailable
            else -> BiometricAvailability.NoHardware
        }
    }

    /**
     * Launch official BiometricPrompt dialog for owner authentication.
     */
    fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String = "Hackerflag Owner Verification",
        subtitle: String = "Biometric entry point for emergency controls",
        description: String = "Confirm biometric identity to access emergency controls and anti-theft countermeasures.",
        onSuccess: () -> Unit,
        onError: (errorCode: Int, errString: String) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errorCode, errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)

        val biometricManager = BiometricManager.from(activity)
        val canUseDeviceCredential = biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)

        if (canUseDeviceCredential) {
            // Allows PIN / Pattern / Password fallback
            promptInfoBuilder.setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        } else {
            promptInfoBuilder.setAllowedAuthenticators(BIOMETRIC_STRONG)
            promptInfoBuilder.setNegativeButtonText("Use Emergency PIN")
        }

        try {
            prompt.authenticate(promptInfoBuilder.build())
        } catch (e: Exception) {
            onError(-1, e.message ?: "Failed to initialize BiometricPrompt")
        }
    }
}
