package com.duallive.app.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object ScreenCastUtils {
    const val SCREEN_CAPTURE_REQUEST_CODE = 1001
    const val NOTIFICATION_ID = 101
    const val CHANNEL_ID = "screen_cast_channel"

    fun createNotification(context: Context): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Live Scoreboard Casting",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("DualLive is Casting")
            .setContentText("Scoreboard is being displayed live.")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()
    }

    // Adding missing functions to satisfy ScreenCastController
    fun startScreenCapture(context: Context) {
        // Placeholder for capture logic
    }

    fun stopScreenCapture(context: Context) {
        // Placeholder for stop logic
    }
}
