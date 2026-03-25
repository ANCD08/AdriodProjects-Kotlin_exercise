package com.example.hydratrack.data.dao

import androidx.room.*
import com.example.hydratrack.data.WaterLog
import kotlinx.coroutines.flow.Flow

data class DailyTotal(
    val date: String,
    val total: Int
)

@Dao
interface WaterLogDao {
    @Insert
    suspend fun insert(log: WaterLog): Long

    @Query("SELECT * FROM water_log WHERE userId = :userId AND isUndone = 0 ORDER BY loggedAt DESC LIMIT 1")
    suspend fun getLastLog(userId: String): WaterLog?

    @Query("UPDATE water_log SET isUndone = 1 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("SELECT SUM(amountMl) FROM water_log WHERE userId = :userId AND date = :date AND isUndone = 0")
    fun getDailyIntakeFlow(userId: String, date: String): Flow<Int?>

    @Query("SELECT * FROM water_log WHERE userId = :userId AND date = :date AND isUndone = 0 ORDER BY loggedAt DESC")
    fun getDailyLogsFlow(userId: String, date: String): Flow<List<WaterLog>>

    @Query("SELECT date, SUM(amountMl) as total FROM water_log WHERE userId = :userId AND date >= :sinceDate AND isUndone = 0 GROUP BY date ORDER BY date ASC")
    suspend fun getWeeklyTotals(userId: String, sinceDate: String): List<DailyTotal>

    @Query("SELECT * FROM water_log")
    suspend fun getAllLogs(): List<WaterLog>

    @Query("SELECT COUNT(*) FROM water_log WHERE userId = :userId AND date = :date AND method = 'SHAKE' AND isUndone = 0")
    suspend fun getShakeCount(userId: String, date: String): Int
}
