package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stolen_devices")
data class StolenDeviceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imei: String,
    val imeiSha256: String,
    val brandModel: String,
    val dateFlagged: Long = System.currentTimeMillis(),
    val status: String, // "FLAGGED_STOLEN", "CEIR_BLOCKED", "POLICE_TRACED", "RECOVERED", "CLEAN"
    val firNumber: String? = null,
    val policeStation: String? = null,
    val riskRating: Int = 85,
    val notes: String = ""
)
