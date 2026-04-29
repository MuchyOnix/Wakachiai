package com.hikari.anime.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Application
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HikariApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            NEW_EPISODES_CHANNEL_ID,
            "New episodes",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Alerts when tracked anime receive new episodes."
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val NEW_EPISODES_CHANNEL_ID = "new_episodes"
    }
}
