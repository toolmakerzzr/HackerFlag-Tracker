package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incident_reports")
data class IncidentReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val victimName: String,
    val contactNumber: String,
    val incidentDate: String,
    val incidentTime: String,
    val location: String,
    val deviceModel: String,
    val imei1: String,
    val imei2: String = "",
    val firDraftText: String,
    val digitalSeal: String,
    val isFiledWithPolice: Boolean = false,
    val policeStation: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
