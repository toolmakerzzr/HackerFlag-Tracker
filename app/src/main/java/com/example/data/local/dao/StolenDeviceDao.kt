package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.StolenDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StolenDeviceDao {
    @Query("SELECT * FROM stolen_devices ORDER BY dateFlagged DESC")
    fun getAllDevices(): Flow<List<StolenDeviceEntity>>

    @Query("SELECT * FROM stolen_devices WHERE imei = :imei OR imeiSha256 = :imeiSha256 LIMIT 1")
    suspend fun findDeviceByImeiOrHash(imei: String, imeiSha256: String): StolenDeviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: StolenDeviceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<StolenDeviceEntity>)

    @Update
    suspend fun updateDevice(device: StolenDeviceEntity)

    @Query("UPDATE stolen_devices SET status = :newStatus WHERE id = :deviceId")
    suspend fun updateStatus(deviceId: Long, newStatus: String)

    @Query("SELECT COUNT(*) FROM stolen_devices")
    suspend fun getDeviceCount(): Int
}
