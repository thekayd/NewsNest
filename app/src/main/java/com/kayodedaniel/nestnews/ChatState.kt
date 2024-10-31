package com.kayodedaniel.nestnews

import android.graphics.Bitmap
import com.kayodedaniel.nestnews.data.Chat

data class ChatState(
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null,
    val isTyping: Boolean = false
)
