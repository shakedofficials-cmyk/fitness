package com.recompos.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) {
    fun scheduleDefaults() {
        createChannel(context)
        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .addTag("recompos_reminders")
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "recompos_daily_reminders",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelAll() {
        WorkManager.getInstance(context).cancelAllWorkByTag("recompos_reminders")
    }

    companion object {
        const val channelId = "recompos_coach"
        fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val manager = context.getSystemService(NotificationManager::class.java)
                val channel = NotificationChannel(channelId, "Greek God Physique Coach", NotificationManager.IMPORTANCE_DEFAULT)
                manager.createNotificationChannel(channel)
            }
        }
    }
}

class DailyReminderWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        ReminderScheduler.createChannel(applicationContext)
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val messages = listOf(
            "Morning weigh-in: log it before food or water.",
            "8,000 steps minimum. Get the walk in.",
            "Shoulder rule: pain >3/10 means swap the movement.",
            "Creatine: 5 g daily, any time with a meal."
        )
        val message = messages[(System.currentTimeMillis() / TimeUnit.DAYS.toMillis(1)).toInt() % messages.size]
        val notification = NotificationCompat.Builder(applicationContext, ReminderScheduler.channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Greek God Physique")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(1001, notification)
        return Result.success()
    }
}
