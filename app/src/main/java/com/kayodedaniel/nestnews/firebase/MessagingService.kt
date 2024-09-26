package com.kayodedaniel.nestnews.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// Custom Firebase Messaging Service to handle token generation and message reception for push notifications.
class MessagingService : FirebaseMessagingService() {

    // Called when a new FCM token is generated for the device. This token can be used to send notifications to the specific device.
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Logs the new token for debugging or can be sent to the server to associate the token with a user.
        Log.d("FCM", "Token: $token")
    }

    // Called when a message is received from Firebase Cloud Messaging. Handles the message contents (notification or data payload).
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Logs the message body (if available) for debugging purposes.
        Log.d("FCM", "Message: " + remoteMessage.notification?.body)
    }
}
