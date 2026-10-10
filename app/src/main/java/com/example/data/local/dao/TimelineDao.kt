package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.TimelineEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimelineDao {
    @Query("SELECT * FROM timeline_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<TimelineEventEntity>>

    @Query("SELECT COUNT(*) FROM timeline_events")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: TimelineEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<TimelineEventEntity>)

    @Query("DELETE FROM timeline_events")
    suspend fun clearAll()
}
