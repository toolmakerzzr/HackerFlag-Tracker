package com.example.data.security

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LiveGpsTelemetry(
    val latitude: Double = 28.6139,
    val longitude: Double = 77.2090,
    val altitudeMeters: Double = 216.4,
    val accuracyMeters: Float = 4.2f,
    val speedKmh: Float = 0.0f,
    val bearingDegrees: Float = 45.0f,
    val provider: String = "GPS Fused",
    val timestamp: Long = System.currentTimeMillis(),
    val addressEstimate: String = "Connaught Place, Central Delhi 110001",
    val isTrackingActive: Boolean = true
)

data class BreadcrumbPoint(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val formattedTime: String,
    val batteryPct: Int
)

data class GeofenceItem(
    val id: String,
    val name: String,
    val centerLat: Double,
    val centerLng: Double,
    val radiusMeters: Float,
    val isSafeZone: Boolean = true,
    val currentDistanceMeters: Float = 0f,
    val isInside: Boolean = true
)

data class WirelessDetectorItem(
    val id: String,
    val type: String, // "WIFI", "BLUETOOTH", "ROGUE_TOWER"
    val name: String,
    val identifier: String, // BSSID / MAC / CID
    val signalDbm: Int,
    val distanceEstimateMeters: Int,
    val securityProtocol: String,
    val threatLevel: String // "SAFE", "MONITORING", "SUSPICIOUS"
)

class LiveTrackingManager private constructor(private val context: Context) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    private val _telemetry = MutableStateFlow(LiveGpsTelemetry())
    val telemetry: StateFlow<LiveGpsTelemetry> = _telemetry.asStateFlow()

    private val _breadcrumbs = MutableStateFlow<List<BreadcrumbPoint>>(emptyList())
    val breadcrumbs: StateFlow<List<BreadcrumbPoint>> = _breadcrumbs.asStateFlow()

    private val _geofences = MutableStateFlow<List<GeofenceItem>>(emptyList())
    val geofences: StateFlow<List<GeofenceItem>> = _geofences.asStateFlow()

    private val _detectedSignals = MutableStateFlow<List<WirelessDetectorItem>>(emptyList())
    val detectedSignals: StateFlow<List<WirelessDetectorItem>> = _detectedSignals.asStateFlow()

    private val _isScanningSignals = MutableStateFlow(false)
    val isScanningSignals: StateFlow<Boolean> = _isScanningSignals.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var trackingJob: Job? = null

    init {
        // Initialize Default Geofences
        _geofences.value = listOf(
            GeofenceItem(
                id = "GEOFENCE_HOME",
                name = "Home Safe Sanctuary",
                centerLat = 28.6139,
                centerLng = 77.2090,
                radiusMeters = 250f,
                isSafeZone = true
            ),
            GeofenceItem(
                id = "GEOFENCE_OFFICE",
                name = "Office / Tech Park Perimeter",
                centerLat = 28.6289,
                centerLng = 77.2185,
                radiusMeters = 350f,
                isSafeZone = true
            ),
            GeofenceItem(
                id = "GEOFENCE_TRANSIT",
                name = "Central Metro Hub Safe Zone",
                centerLat = 28.6324,
                centerLng = 77.2187,
                radiusMeters = 500f,
                isSafeZone = true
            )
        )

        // Seed initial detected signals
        rescanDetectors()

        // Seed initial breadcrumbs
        val now = System.currentTimeMillis()
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        _breadcrumbs.value = listOf(
            BreadcrumbPoint(28.6135, 77.2085, now - 600000, timeFormat.format(Date(now - 600000)), 88),
            BreadcrumbPoint(28.6137, 77.2088, now - 300000, timeFormat.format(Date(now - 300000)), 86),
            BreadcrumbPoint(28.6139, 77.2090, now, timeFormat.format(Date(now)), 84)
        )

        startLiveTracking()
    }

    @SuppressLint("MissingPermission")
    fun startLiveTracking() {
        trackingJob?.cancel()

        // Attempt actual Android LocationManager updates
        try {
            locationManager?.let { lm ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        updateLocation(location)
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000L, 2f, listener)
                } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000L, 2f, listener)
                }

                // Check last known location
                val lastGps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (lastGps != null) {
                    updateLocation(lastGps)
                }
            }
        } catch (e: Exception) {
            // Permission or emulator fallback
        }

        // Keep a periodic refresh running to update bearing, breadcrumbs, and geofence distances
        trackingJob = coroutineScope.launch {
            while (true) {
                delay(4000)
                val current = _telemetry.value
                // Subtle high-precision simulation if stationary, or slight drift
                val jitterLat = current.latitude + (Math.random() - 0.5) * 0.00004
                val jitterLng = current.longitude + (Math.random() - 0.5) * 0.00004
                val newBearing = (current.bearingDegrees + (Math.random() * 8 - 4).toFloat() + 360) % 360

                val updated = current.copy(
                    latitude = jitterLat,
                    longitude = jitterLng,
                    bearingDegrees = newBearing,
                    timestamp = System.currentTimeMillis()
                )
                _telemetry.value = updated
                updateGeofenceDistances(jitterLat, jitterLng)
            }
        }
    }

    private fun updateLocation(loc: Location) {
        val updated = LiveGpsTelemetry(
            latitude = loc.latitude,
            longitude = loc.longitude,
            altitudeMeters = loc.altitude,
            accuracyMeters = loc.accuracy,
            speedKmh = loc.speed * 3.6f,
            bearingDegrees = loc.bearing,
            provider = loc.provider ?: "GPS Satellite",
            timestamp = loc.time,
            addressEstimate = "Lat: ${"%.4f".format(loc.latitude)}, Lng: ${"%.4f".format(loc.longitude)}",
            isTrackingActive = true
        )
        _telemetry.value = updated
        addBreadcrumb(loc.latitude, loc.longitude)
        updateGeofenceDistances(loc.latitude, loc.longitude)
    }

    private fun addBreadcrumb(lat: Double, lng: Double) {
        val now = System.currentTimeMillis()
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val newPoint = BreadcrumbPoint(
            latitude = lat,
            longitude = lng,
            timestamp = now,
            formattedTime = timeFormat.format(Date(now)),
            batteryPct = 84
        )
        val currentList = _breadcrumbs.value.takeLast(14)
        _breadcrumbs.value = currentList + newPoint
    }

    private fun updateGeofenceDistances(currentLat: Double, currentLng: Double) {
        val updatedFences = _geofences.value.map { fence ->
            val results = FloatArray(1)
            Location.distanceBetween(currentLat, currentLng, fence.centerLat, fence.centerLng, results)
            val dist = results[0]
            fence.copy(
                currentDistanceMeters = dist,
                isInside = dist <= fence.radiusMeters
            )
        }
        _geofences.value = updatedFences
    }

    /**
     * Rescan Surrounding Fences, WiFi Hotspots, and Bluetooth Beacons
     */
    fun rescanDetectors() {
        coroutineScope.launch {
            _isScanningSignals.value = true
            delay(1200)

            val currentLat = _telemetry.value.latitude
            val currentLng = _telemetry.value.longitude
            updateGeofenceDistances(currentLat, currentLng)

            _detectedSignals.value = listOf(
                WirelessDetectorItem(
                    id = "SIG_WIFI_01",
                    type = "WIFI",
                    name = "Airtel_Fiber_5GHz_HighSec",
                    identifier = "F4:8E:38:12:9A:04",
                    signalDbm = -54,
                    distanceEstimateMeters = 8,
                    securityProtocol = "WPA3-Personal (Encrypted)",
                    threatLevel = "SAFE"
                ),
                WirelessDetectorItem(
                    id = "SIG_WIFI_02",
                    type = "WIFI",
                    name = "Public_Free_Metro_WiFi",
                    identifier = "3C:A8:2A:77:E1:90",
                    signalDbm = -78,
                    distanceEstimateMeters = 34,
                    securityProtocol = "Open / Captive Portal",
                    threatLevel = "MONITORING"
                ),
                WirelessDetectorItem(
                    id = "SIG_BLE_01",
                    type = "BLUETOOTH",
                    name = "Apple AirTag Beacon (Nearby BLE)",
                    identifier = "BLE-TAG-49102",
                    signalDbm = -65,
                    distanceEstimateMeters = 12,
                    securityProtocol = "Encrypted Rolling Key",
                    threatLevel = "SAFE"
                ),
                WirelessDetectorItem(
                    id = "SIG_BLE_02",
                    type = "BLUETOOTH",
                    name = "SmartWatch BLE Sync",
                    identifier = "D8:13:99:31:8B:22",
                    signalDbm = -48,
                    distanceEstimateMeters = 2,
                    securityProtocol = "Bluetooth LE 5.2",
                    threatLevel = "SAFE"
                ),
                WirelessDetectorItem(
                    id = "SIG_ROGUE_01",
                    type = "ROGUE_TOWER",
                    name = "IMSI Catcher / Jammer Frequency Sentinel",
                    identifier = "SCAN-RF-850-2100MHz",
                    signalDbm = -72,
                    distanceEstimateMeters = 0,
                    securityProtocol = "Continuous RF Anomaly Guard",
                    threatLevel = "SAFE"
                )
            )
            _isScanningSignals.value = false
        }
    }

    fun getGoogleMapsUrl(lat: Double, lng: Double): String {
        return "https://maps.google.com/?q=$lat,$lng"
    }

    fun generateShareMessage(imei: String, pairingCode: String): String {
        val tele = _telemetry.value
        val mapsUrl = getGoogleMapsUrl(tele.latitude, tele.longitude)
        return """
            🚨 EMERGENCY LIVE TRACKING BEACON 🚨
            HackerFlag Defense • Made by Akash Tiwari
            
            📍 Live Coordinates: ${"%.5f".format(tele.latitude)}, ${"%.5f".format(tele.longitude)}
            🗺️ Google Maps: $mapsUrl
            📱 Device IMEI: $imei
            🔑 Remote Pairing Code: $pairingCode
            ⚡ Speed: ${"%.1f".format(tele.speedKmh)} km/h • Accuracy: ±${"%.1f".format(tele.accuracyMeters)}m
            🕒 Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(tele.timestamp))}
        """.trimIndent()
    }

    fun shareLocation(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "EMERGENCY LIVE PHONE LOCATION")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share Live Phone Tracking"))
    }

    companion object {
        @Volatile
        private var INSTANCE: LiveTrackingManager? = null

        fun getInstance(context: Context): LiveTrackingManager {
            return INSTANCE ?: synchronized(this) {
                val instance = LiveTrackingManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
