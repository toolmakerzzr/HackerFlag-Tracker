package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.IncidentDao
import com.example.data.local.dao.StolenDeviceDao
import com.example.data.local.dao.TimelineDao
import com.example.data.local.entity.IncidentReportEntity
import com.example.data.local.entity.StolenDeviceEntity
import com.example.data.local.entity.TimelineEventEntity
import com.example.data.security.CryptoUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TimelineEventEntity::class,
        StolenDeviceEntity::class,
        IncidentReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HackerflagDatabase : RoomDatabase() {

    abstract fun timelineDao(): TimelineDao
    abstract fun stolenDeviceDao(): StolenDeviceDao
    abstract fun incidentDao(): IncidentDao

    companion object {
        @Volatile
        private var INSTANCE: HackerflagDatabase? = null

        fun getDatabase(context: Context): HackerflagDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HackerflagDatabase::class.java,
                    "hackerflag_defense_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial sample data in background
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            seedInitialData(database)
                        }
                    }
                }).build()
                INSTANCE = instance
                CoroutineScope(Dispatchers.IO).launch {
                    ensureSeeded(instance)
                }
                instance
            }
        }

        suspend fun ensureSeeded(db: HackerflagDatabase) {
            try {
                if (db.timelineDao().getCount() == 0) {
                    seedInitialData(db)
                }
            } catch (e: Exception) {}
        }

        suspend fun seedInitialData(db: HackerflagDatabase) {
            val now = System.currentTimeMillis()

            // Seed rich timeline events so logs are never blank
            db.timelineDao().insertEvent(
                TimelineEventEntity(
                    timestamp = now - 3600000L * 4,
                    eventCategory = "DEVICE_ARMED",
                    title = "Device Defense System Armed & Paired",
                    details = "Pairing Code generated & hardware IMEI encrypted with Zero-Knowledge proof.",
                    evidenceHash = CryptoUtils.sha256("DEVICE_ARMED_01"),
                    isVerified = true
                )
            )
            db.timelineDao().insertEvent(
                TimelineEventEntity(
                    timestamp = now - 3600000L * 3,
                    eventCategory = "THEFT_REPORTED",
                    title = "Incident Detected / Reported",
                    details = "Primary device flagged lost or snatched near Connaught Place Metro Stn.",
                    evidenceHash = CryptoUtils.sha256("THEFT_REPORTED_01"),
                    isVerified = true
                )
            )
            db.timelineDao().insertEvent(
                TimelineEventEntity(
                    timestamp = now - 3600000L * 2,
                    eventCategory = "BANK_FROZEN",
                    title = "Emergency USSD & NetBanking Freeze Executed",
                    details = "USSD *99*4*1# dialed. SBI & HDFC debit/UPI linkages paused.",
                    evidenceHash = CryptoUtils.sha256("BANK_FROZEN_01"),
                    isVerified = true
                )
            )
            db.timelineDao().insertEvent(
                TimelineEventEntity(
                    timestamp = now - 3600000L * 1,
                    eventCategory = "CEIR_FLAGGED",
                    title = "CEIR Portal Request Submitted",
                    details = "IMEI flagged on Sanchar Saathi national registry with Police DD reference.",
                    evidenceHash = CryptoUtils.sha256("CEIR_SUBMISSION_01"),
                    isVerified = true
                )
            )
            db.timelineDao().insertEvent(
                TimelineEventEntity(
                    timestamp = now - 1800000L,
                    eventCategory = "GEOFENCE_SENTINEL",
                    title = "Surrounding Fences & Detectors Scanned",
                    details = "Home Safe Perimeter verified (250m). 4 WiFi APs, 2 BLE beacons, RF anomaly: 0.",
                    evidenceHash = CryptoUtils.sha256("GEOFENCE_SENTINEL_01"),
                    isVerified = true
                )
            )
            db.timelineDao().insertEvent(
                TimelineEventEntity(
                    timestamp = now - 300000L,
                    eventCategory = "LIVE_GPS_PING",
                    title = "Live GPS Telemetry Broadcast Active",
                    details = "Lat: 28.6139, Lng: 77.2090 • Accuracy: ±4.2m • Speed: 0 km/h • Battery: 84%.",
                    evidenceHash = CryptoUtils.sha256("LIVE_GPS_PING_01"),
                    isVerified = true
                )
            )

            // Seed initial stolen devices in registry
            val imei1 = "869204058193847"
            val imei2 = "354928109485721"
            val imei3 = "867104049281923"
            val imei4 = "356891023847561"

            db.stolenDeviceDao().insertDevices(
                listOf(
                    StolenDeviceEntity(
                        imei = imei1,
                        imeiSha256 = CryptoUtils.sha256(imei1),
                        brandModel = "Samsung Galaxy S24 Ultra",
                        dateFlagged = now - 86400000L * 2,
                        status = "CEIR_BLOCKED",
                        firNumber = "FIR/DEL/2026/0842",
                        policeStation = "Cyber Crime PS New Delhi",
                        riskRating = 94,
                        notes = "Reported snatching near Rajiv Chowk Gate 4."
                    ),
                    StolenDeviceEntity(
                        imei = imei2,
                        imeiSha256 = CryptoUtils.sha256(imei2),
                        brandModel = "Apple iPhone 15 Pro Max",
                        dateFlagged = now - 86400000L * 5,
                        status = "POLICE_TRACED",
                        firNumber = "FIR/MUM/2026/4102",
                        policeStation = "Bandra Cyber Crime Cell",
                        riskRating = 88,
                        notes = "SIM swap detected in Kurla cell tower sector 2."
                    ),
                    StolenDeviceEntity(
                        imei = imei3,
                        imeiSha256 = CryptoUtils.sha256(imei3),
                        brandModel = "OnePlus 12 5G",
                        dateFlagged = now - 86400000L * 8,
                        status = "RECOVERED",
                        firNumber = "FIR/BLR/2026/1930",
                        policeStation = "Indiranagar Police Station",
                        riskRating = 20,
                        notes = "Device seized at secondhand retail counter."
                    ),
                    StolenDeviceEntity(
                        imei = imei4,
                        imeiSha256 = CryptoUtils.sha256(imei4),
                        brandModel = "Google Pixel 8 Pro",
                        dateFlagged = now - 86400000L * 1,
                        status = "FLAGGED_STOLEN",
                        firNumber = "Pending",
                        policeStation = "Sector 18 Cyber Cell",
                        riskRating = 96,
                        notes = "Lost in transit. WhatsApp session revoked."
                    )
                )
            )
        }
    }
}
