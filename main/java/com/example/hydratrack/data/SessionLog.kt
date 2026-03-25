package com.example.hydratrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_logs")
data class SessionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val date: String,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null
)
