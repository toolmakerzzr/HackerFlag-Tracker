package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timeline_events")
data class TimelineEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventCategory: String, // "THEFT_REPORTED", "BANK_FROZEN", "SIM_BLOCKED", "CEIR_FLAGGED", "FIR_FILED"
    val title: String,
    val details: String,
    val evidenceHash: String,
    val isVerified: Boolean = true
)
