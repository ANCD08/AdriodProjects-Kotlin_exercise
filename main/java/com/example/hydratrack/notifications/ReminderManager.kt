package com.example.hydratrack.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.hydratrack.MainActivity
import com.example.hydratrack.utils.Constants
import java.util.concurrent.TimeUnit

class ReminderWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        postNotification()
        return Result.success()
    }

    private fun postNotification() {
        val launchIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat
            .Builder(applicationContext, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("💧 Time to hydrate!")
            .setContentText("Haven't logged water recently — shake your phone or tap to add a glass!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(applicationContext)
                .notify(Constants.NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Handle permission not granted on Android 13+
        }
    }
}

object ReminderManager {
    fun schedule(context: Context, intervalHours: Int) {
        cancel(context)
        if (intervalHours <= 0) return

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(
            intervalHours.toLong(), TimeUnit.HOURS
        )
            .addTag(Constants.WORK_TAG_REMINDER)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            Constants.WORK_TAG_REMINDER,
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag(Constants.WORK_TAG_REMINDER)
    }
}
