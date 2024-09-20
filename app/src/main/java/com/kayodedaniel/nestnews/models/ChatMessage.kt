package com.kayodedaniel.nestnews.models

data class ChatMessage(
    var senderId: String? = null,
    var receiverId: String? = null,
    var message: String? = null,
    var dataTime: String? = null
)
