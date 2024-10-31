package com.kayodedaniel.nestnews.activities

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kayodedaniel.nestnews.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Create another pending intent for the next notification
        scheduleNextNotification(context)

        // Show the notification
        showNotification(context)
    }
    //function that schedules the next notification using alarmManager to trigger it after the specified interval
    private fun scheduleNextNotification(context: Context) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        //Initialize AlarmManager and set a trigger time
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val triggerTime = System.currentTimeMillis() + (60 * 1000) // 1 minute

        try {
            // For Android 12 and above, check if exact alarms are allowed and set accordingly
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(
                        android.app.AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    // If exact alarms aren't allowed, fall back to inexact alarm
                    alarmManager.set(
                        android.app.AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else {
                // For pre-Android 12 devices
                alarmManager.setExact(
                    android.app.AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (se: SecurityException) {
            // Fall back to inexact alarm if we catch a SecurityException
            alarmManager.set(
                android.app.AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }
    // Builds and shows a notification with a title, text, and an intent to open MainActivity when tapped
    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create an intent for when the notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        // Set up a PendingIntent for launching MainActivity
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Build the notification with basic properties
        val builder = NotificationCompat.Builder(context, "article_notifications")
            .setSmallIcon(R.drawable.ic_icon) // Icon for the notification
            .setContentTitle("News Update") // Title of the notification
            .setContentText("Check out the latest news articles!") // Message content
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Notification priority
            .setAutoCancel(true) // Dismiss notification on tap
            .setContentIntent(pendingIntent) // Intent to trigger on notification tap
        // Display the notification with a unique ID
        notificationManager.notify(101, builder.build())
    }
}