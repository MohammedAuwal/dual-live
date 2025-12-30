package com.duallive.app.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ScreenCastService : Service() {
    private val CHANNEL_ID = "ScreenCastChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Live Streaming")
            .setContentText("DualLive is broadcasting your match...")
            // Fix: Using a system drawable to avoid "Unresolved reference"
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .build()
        
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "Screen Cast",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
