package com.kayodedaniel.nestnews.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kayodedaniel.nestnews.R

class NotificationService(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun sendNotification(title: String, message: String) {
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "my_channel_id",
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val builder = NotificationCompat.Builder(context, "my_channel_id")
            .setSmallIcon(R.drawable.ic_icon) // Icon for the notification
            .setContentTitle(title) // Title of the notification
            .setContentText(message) // Message content
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Notification priority

        // Show the notification
        notificationManager.notify(0, builder.build())
    }
}