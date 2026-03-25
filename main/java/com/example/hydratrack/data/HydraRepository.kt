package com.example.hydratrack.data

import android.content.Context
import com.example.hydratrack.data.dao.DailyTotal
import com.example.hydratrack.utils.Constants
import com.example.hydratrack.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class HydraRepository(context: Context) {

    private val db           = AppDatabase.getInstance(context)
    private val profileDao   = db.userProfileDao()
    private val waterLogDao  = db.waterLogDao()
    // SessionDao needs to be added to AppDatabase if it's used
    // private val sessionDao   = db.sessionLogDao()

    suspend fun saveProfile(profile: UserProfile) = profileDao.insertOrUpdate(profile)

    suspend fun getProfile(userId: String): UserProfile? = profileDao.getProfile(userId)

    suspend fun getAllProfiles(): List<UserProfile> = profileDao.getAllProfiles()

    suspend fun logWater(userId: String, amountMl: Int, method: LogMethod): Long {
        val log = WaterLog(
            userId   = userId,
            amountMl = amountMl,
            method   = method,
            date     = DateUtils.today()
        )
        return waterLogDao.insert(log)
    }

    suspend fun undoLastLog(userId: String): Boolean {
        val last    = waterLogDao.getLastLog(userId) ?: return false
        val elapsed = System.currentTimeMillis() - last.loggedAt
        if (elapsed > Constants.UNDO_WINDOW_MS) return false
        waterLogDao.softDelete(last.id)
        return true
    }

    fun getDailyIntakeFlow(userId: String, date: String = DateUtils.today()): Flow<Int> =
        waterLogDao.getDailyIntakeFlow(userId, date).map { it ?: 0 }

    fun getDailyLogsFlow(userId: String, date: String = DateUtils.today()): Flow<List<WaterLog>> =
        waterLogDao.getDailyLogsFlow(userId, date)

    suspend fun getWeeklyTotals(userId: String): List<DailyTotal> =
        waterLogDao.getWeeklyTotals(userId, DateUtils.daysAgo(6))

    suspend fun getAllLogs(): List<WaterLog> = waterLogDao.getAllLogs()

    suspend fun compileAdminReport(): List<AdminUserReport> {
        val today    = DateUtils.today()
        val profiles = getAllProfiles()

        return profiles.map { profile ->
            val intakeToday  = getDailyIntakeFlow(profile.userId, today).first()
            val shakeCount   = waterLogDao.getShakeCount(profile.userId, today)
            // val sessionCount = sessionDao.getSessionCount(profile.userId, today)

            AdminUserReport(
                userId        = profile.userId,
                name          = profile.name,
                age           = profile.age,
                weightKg      = profile.weightKg,
                dailyGoalMl   = profile.dailyGoalMl,
                date          = today,
                totalIntakeMl = intakeToday,
                goalAchieved  = intakeToday >= profile.dailyGoalMl,
                shakeCount    = shakeCount,
                sessionsCount = 0 // sessionCount
            )
        }
    }
}

data class AdminUserReport(
    val userId: String,
    val name: String,
    val age: Int,
    val weightKg: Float,
    val dailyGoalMl: Int,
    val date: String,
    val totalIntakeMl: Int,
    val goalAchieved: Boolean,
    val shakeCount: Int,
    val sessionsCount: Int
)
