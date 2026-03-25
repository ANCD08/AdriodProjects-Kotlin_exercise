package com.example.hydratrack.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hydratrack.data.SessionLog

@Dao
interface SessionLogDao {
    @Insert
    suspend fun insertSession(session: SessionLog): Long

    @Query("UPDATE session_logs SET endTime = :endTime WHERE id = :id")
    suspend fun endSession(id: Long, endTime: Long)

    @Query("SELECT COUNT(*) FROM session_logs WHERE userId = :userId AND date = :date")
    suspend fun getSessionCount(userId: String, date: String): Int
}
