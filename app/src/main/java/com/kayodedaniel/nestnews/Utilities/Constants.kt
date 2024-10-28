
package com.kayodedaniel.nestnews.Utilities

import org.json.JSONException
import org.json.JSONObject

object Constants {
    const val KEY_IS_AFRIKAANS = "key_is_afrikaans"
    const val KEY_FINGERPRINT_ID = "fingerprintId"
    const val KEY_IS_DARK_MODE = "is_dark_mode"
    const val KEY_COLLECTION_USERS = "users"
    const val KEY_NAME = "name"
    const val KEY_EMAIL = "email"
    const val KEY_PASSWORD = "password"
    const val KEY_PREFERENCE_NAME = "chatAppPreference"
    const val KEY_IS_SIGNED_IN = "isSignedIn"
    const val KEY_USER_ID = "userId"
    const val KEY_IMAGE = "image"
    const val KEY_FCM_TOKEN = "fcmToken"
    const val KEY_USER = "user"
    const val KEY_COLLECTION_CHAT = "chat"
    const val KEY_SENDER_ID = "senderId"
    const val KEY_RECEIVER_ID = "receiverID"
    const val KEY_MESSAGE = "message"
    const val KEY_TIMESTAMP = "timestamp"
    const val KEY_COLLECTION_CONVERSATIONS = "conversations"
    const val KEY_SENDER_IMAGE = "senderImage"
    const val KEY_SENDER_NAME = "senderName"
    const val KEY_RECEIVER_NAME = "receiverName"
    const val KEY_RECEIVER_IMAGE = "receiverImage"
    const val KEY_LAST_MESSAGE = "lastMessage"
    const val KEY_AVAILABILITY = "availability"
    const val REMOTE_MSG_AUTHORIZATION = "Authorization"
    const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
    const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"

    private var remoteMsgHeaders: HashMap<String, String>? = null

    fun getRemoteMsgHeaders(): HashMap<String, String> {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = HashMap()
            remoteMsgHeaders?.apply {
                put(REMOTE_MSG_AUTHORIZATION, "key_here")
                put(REMOTE_MSG_CONTENT_TYPE, "application/json")
            }
        }
        return remoteMsgHeaders as HashMap<String, String>
    }

    const val REMOTE_MSG_DATA = "message"
    const val REMOTE_MSG_NOTIFICATION = "notification"
    const val REMOTE_MSG_TITLE = "title"
    const val REMOTE_MSG_BODY = "body"

    @Throws(JSONException::class)
    fun createFcmMessage(token: String?, title: String?, message: String?): JSONObject {
        val jNotification = JSONObject()
        jNotification.put("title", title)
        jNotification.put("body", message)
        val jData = JSONObject()
        jData.put("title", title)
        jData.put("message", message)
        val jMessage = JSONObject()
        jMessage.put("token", token)
        jMessage.put("notification", jNotification)
        jMessage.put("data", jData)
        val jFcm = JSONObject()
        jFcm.put("message", jMessage)
        return jFcm
    }
}
