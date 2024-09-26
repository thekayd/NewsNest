package com.kayodedaniel.nestnews.models

import java.util.Date

// Class representing a chat message exchanged between users.
class ChatMessage {
    var senderId: String? = null          // ID of the user sending the message.
    var receiverId: String? = null        // ID of the user receiving the message.
    var message: String? = null           // The actual text message.
    var dateTime: String? = null          // The timestamp when the message was sent, stored as a String.
    var dateObject: Date? = null          // The message timestamp stored as a Date object.
    var conversionId: String? = null      // ID representing the conversation between users.
    var conversionName: String? = null    // Name of the person in the conversation.
    var conversionImage: String? = null   // Profile image or group image for the conversation.
}
