package com.example.hydratrack.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class LogMethod { SHAKE, MANUAL }

@Entity(
    tableName = "water_log",
    foreignKeys = [
        ForeignKey(
            entity        = UserProfile::class,
            parentColumns = ["userId"],
            childColumns  = ["userId"],
            onDelete      = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("date")]
)
data class WaterLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val amountMl: Int,
    val method: LogMethod,
    val loggedAt: Long = System.currentTimeMillis(),
    val isUndone: Boolean = false,
    val date: String,
    val remindersTriggeredToday: Int = 0
)
