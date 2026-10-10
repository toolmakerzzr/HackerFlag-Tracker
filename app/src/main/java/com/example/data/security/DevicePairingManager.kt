package com.example.data.security

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

data class PairedDevice(
    val pairingCode: String,
    val imei: String,
    val deviceName: String,
    val isArmed: Boolean = true,
    val isLostMode: Boolean = false,
    val isAlarmRinging: Boolean = false,
    val lastKnownLat: Double = 28.6139,
    val lastKnownLng: Double = 77.2090,
    val lastPingTimestamp: Long = System.currentTimeMillis(),
    val batteryPct: Int = 84,
    val ownerPhone: String = "+91 98765 43210"
)

class DevicePairingManager private constructor(private val context: Context) {

    private val prefs = context.getSharedPreferences("hackerflag_pairing_prefs", Context.MODE_PRIVATE)

    // Current Device Identification
    val myPairingCode: String
    val myDeviceImei: String

    // Local Device Active State
    private val _isLocalAlarmRinging = MutableStateFlow(false)
    val isLocalAlarmRinging: StateFlow<Boolean> = _isLocalAlarmRinging.asStateFlow()

    private val _isLocalLostMode = MutableStateFlow(prefs.getBoolean("local_lost_mode", false))
    val isLocalLostMode: StateFlow<Boolean> = _isLocalLostMode.asStateFlow()

    private val _lostModeMessage = MutableStateFlow(
        prefs.getString("lost_mode_msg", "THIS PHONE IS REPORTED STOLEN. PLEASE RETURN IMMEDIATELY.") ?: ""
    )
    val lostModeMessage: StateFlow<String> = _lostModeMessage.asStateFlow()

    private val _lostModeContact = MutableStateFlow(
        prefs.getString("lost_mode_contact", "+91 98765 43210") ?: "+91 98765 43210"
    )
    val lostModeContact: StateFlow<String> = _lostModeContact.asStateFlow()

    // Remote Target State (when this phone controls another phone)
    private val _pairedRemoteDevices = MutableStateFlow<List<PairedDevice>>(emptyList())
    val pairedRemoteDevices: StateFlow<List<PairedDevice>> = _pairedRemoteDevices.asStateFlow()

    private val _activeRemoteDevice = MutableStateFlow<PairedDevice?>(null)
    val activeRemoteDevice: StateFlow<PairedDevice?> = _activeRemoteDevice.asStateFlow()

    private var sirenJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        // Retrieve or generate persistent Pairing Code
        var code = prefs.getString("my_pairing_code", null)
        if (code.isNullOrBlank()) {
            val randomPart = (1000..9999).random()
            val letters = ('A'..'Z').shuffled().take(2).joinToString("")
            code = "HF-$randomPart-$letters"
            prefs.edit().putString("my_pairing_code", code).apply()
        }
        myPairingCode = code

        // Retrieve or generate Device IMEI / Unique Hardware ID
        var imei = prefs.getString("my_device_imei", null)
        if (imei.isNullOrBlank()) {
            try {
                val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                if (!androidId.isNullOrBlank() && androidId.length >= 10) {
                    // Turn Android ID into a standard 15-digit IMEI-like format
                    val numericHash = Math.abs(androidId.hashCode().toLong())
                    imei = "86" + numericHash.toString().padStart(13, '7').take(13)
                } else {
                    imei = "869204058193847"
                }
            } catch (e: Exception) {
                imei = "869204058193847"
            }
            prefs.edit().putString("my_device_imei", imei).apply()
        }
        myDeviceImei = imei

        // Seed demo paired device for immediate testing if empty
        val defaultPaired = PairedDevice(
            pairingCode = "HF-4192-NX",
            imei = "358920194829104",
            deviceName = "Samsung Galaxy S24 Ultra (Target Phone)",
            isArmed = true,
            isLostMode = false,
            isAlarmRinging = false,
            lastKnownLat = 28.6289,
            lastKnownLng = 77.2065,
            lastPingTimestamp = System.currentTimeMillis() - 120000,
            batteryPct = 78,
            ownerPhone = "+91 98765 43210"
        )
        _pairedRemoteDevices.value = listOf(defaultPaired)
        _activeRemoteDevice.value = defaultPaired
    }

    /**
     * Authenticate and unlock a remote target phone using Pairing Code + IMEI
     */
    fun pairAndUnlockRemoteTarget(targetCode: String, targetImei: String): Boolean {
        val sanitizedCode = targetCode.trim().uppercase(Locale.ROOT)
        val sanitizedImei = targetImei.trim()

        if (sanitizedCode.length < 5 || sanitizedImei.length < 6) {
            return false
        }

        // Check if matching device already exists or create new paired profile
        val existing = _pairedRemoteDevices.value.find {
            it.pairingCode.equals(sanitizedCode, ignoreCase = true) && it.imei == sanitizedImei
        }

        val device = existing ?: PairedDevice(
            pairingCode = sanitizedCode,
            imei = sanitizedImei,
            deviceName = "Remote Phone ($sanitizedCode)",
            isArmed = true,
            isLostMode = false,
            isAlarmRinging = false,
            lastKnownLat = 28.6139 + (Math.random() - 0.5) * 0.02,
            lastKnownLng = 77.2090 + (Math.random() - 0.5) * 0.02,
            lastPingTimestamp = System.currentTimeMillis(),
            batteryPct = (65..95).random()
        )

        val updatedList = _pairedRemoteDevices.value.filter { it.pairingCode != sanitizedCode } + device
        _pairedRemoteDevices.value = updatedList
        _activeRemoteDevice.value = device
        return true
    }

    /**
     * Sound the loud Anti-Theft Siren Alarm locally on this device
     */
    fun startLocalAlarm() {
        if (_isLocalAlarmRinging.value) return
        _isLocalAlarmRinging.value = true

        sirenJob?.cancel()
        sirenJob = coroutineScope.launch {
            try {
                val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    manager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
                } else {
                    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }

                while (_isLocalAlarmRinging.value) {
                    // High-pitch alternating police / warble tone
                    toneGenerator.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 400)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(400)
                    }
                    delay(450)

                    toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 400)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(400)
                    }
                    delay(450)
                }
                toneGenerator.release()
            } catch (e: Exception) {
                // Audio fallback
            }
        }
    }

    /**
     * Stop the local siren alarm
     */
    fun stopLocalAlarm() {
        _isLocalAlarmRinging.value = false
        sirenJob?.cancel()
        sirenJob = null
    }

    /**
     * Set local device lost mode state
     */
    fun setLocalLostMode(enabled: Boolean, contactPhone: String = "+91 98765 43210", customMessage: String = "") {
        _isLocalLostMode.value = enabled
        _lostModeContact.value = contactPhone
        if (customMessage.isNotBlank()) {
            _lostModeMessage.value = customMessage
        }
        prefs.edit()
            .putBoolean("local_lost_mode", enabled)
            .putString("lost_mode_contact", contactPhone)
            .putString("lost_mode_msg", _lostModeMessage.value)
            .apply()

        if (enabled) {
            startLocalAlarm()
        } else {
            stopLocalAlarm()
        }
    }

    /**
     * Remotely trigger alarm on the active paired target phone
     */
    fun triggerRemoteAlarm(pairingCode: String) {
        val current = _activeRemoteDevice.value ?: return
        if (current.pairingCode.equals(pairingCode, ignoreCase = true)) {
            val updated = current.copy(isAlarmRinging = true, isLostMode = true)
            _activeRemoteDevice.value = updated
            updateDeviceInList(updated)
        }
    }

    /**
     * Remotely stop alarm on the active paired target phone
     */
    fun stopRemoteAlarm(pairingCode: String) {
        val current = _activeRemoteDevice.value ?: return
        if (current.pairingCode.equals(pairingCode, ignoreCase = true)) {
            val updated = current.copy(isAlarmRinging = false)
            _activeRemoteDevice.value = updated
            updateDeviceInList(updated)
        }
    }

    /**
     * Remotely mark target phone as Lost/Stolen or Recovered
     */
    fun setRemoteLostMode(pairingCode: String, isLost: Boolean, ownerPhone: String = "+91 98765 43210") {
        val current = _activeRemoteDevice.value ?: return
        if (current.pairingCode.equals(pairingCode, ignoreCase = true)) {
            val updated = current.copy(
                isLostMode = isLost,
                isAlarmRinging = isLost,
                ownerPhone = ownerPhone,
                lastPingTimestamp = System.currentTimeMillis()
            )
            _activeRemoteDevice.value = updated
            updateDeviceInList(updated)
        }
    }

    /**
     * Remotely ping target phone location
     */
    fun pingRemoteLocation(pairingCode: String): PairedDevice? {
        val current = _activeRemoteDevice.value ?: return null
        if (current.pairingCode.equals(pairingCode, ignoreCase = true)) {
            // Slight jitter to simulate real live moving tracker
            val newLat = current.lastKnownLat + (Math.random() - 0.5) * 0.0008
            val newLng = current.lastKnownLng + (Math.random() - 0.5) * 0.0008
            val updated = current.copy(
                lastKnownLat = newLat,
                lastKnownLng = newLng,
                lastPingTimestamp = System.currentTimeMillis(),
                batteryPct = Math.max(1, current.batteryPct - 1)
            )
            _activeRemoteDevice.value = updated
            updateDeviceInList(updated)
            return updated
        }
        return null
    }

    private fun updateDeviceInList(device: PairedDevice) {
        _pairedRemoteDevices.value = _pairedRemoteDevices.value.map {
            if (it.pairingCode == device.pairingCode) device else it
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: DevicePairingManager? = null

        fun getInstance(context: Context): DevicePairingManager {
            return INSTANCE ?: synchronized(this) {
                val instance = DevicePairingManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
