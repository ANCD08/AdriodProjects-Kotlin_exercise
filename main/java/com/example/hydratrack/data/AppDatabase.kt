package com.example.hydratrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hydratrack.data.dao.SessionLogDao
import com.example.hydratrack.data.dao.UserProfileDao
import com.example.hydratrack.data.dao.WaterLogDao
import com.example.hydratrack.utils.Constants

@Database(
    entities = [UserProfile::class, WaterLog::class, SessionLog::class],
    version = Constants.DB_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun waterLogDao(): WaterLogDao
    abstract fun sessionLogDao(): SessionLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
