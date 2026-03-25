package com.example.hydratrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val userId: String,
    val name: String,
    val age: Int,
    val weightKg: Float,
    val dailyGoalMl: Int,
    val deviceModel: String = android.os.Build.MODEL,
    val createdAt: Long = System.currentTimeMillis()
)
