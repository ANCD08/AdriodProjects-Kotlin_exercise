package com.example.hydratrack.data.dao

import androidx.room.*
import com.example.hydratrack.data.UserProfile

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE userId = :userId")
    suspend fun getProfile(userId: String): UserProfile?

    @Query("SELECT * FROM user_profile")
    suspend fun getAllProfiles(): List<UserProfile>
}
