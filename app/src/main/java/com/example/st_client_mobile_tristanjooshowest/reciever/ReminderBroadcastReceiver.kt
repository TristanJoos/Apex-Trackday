package com.example.st_client_mobile_tristanjooshowest.reciever

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val trackName = intent.getStringExtra("TRACK_NAME") ?: "Track Day"
        val minutesBefore = intent.getIntExtra("MINUTES_BEFORE", 60)

        val hours = minutesBefore / 60
        val mins = minutesBefore % 60
        val timeLabel = if (hours > 0) "$hours uur en $mins min" else "$mins minuten"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "race_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Race Reminders", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("🏁 Race herinnering!")
            .setContentText("Je sessie op $trackName begint over $timeLabel!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(trackName.hashCode(), notification)
    }
}