package com.kayodedaniel.nestnews

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kayodedaniel.nestnews.data.Chat
import com.kayodedaniel.nestnews.data.ChatData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    fun onEvent(event: ChatUIEvent) {
        when (event) {
            is ChatUIEvent.sendPrompt -> {
                if (event.prompt.isNotEmpty()) {
                    addPrompt(event.prompt, event.bitmap)
                    if (event.bitmap != null) {
                        getResponseWithImage(event.prompt, event.bitmap)
                    } else {
                        getResponse(event.prompt)
                    }
                }
            }
            is ChatUIEvent.updatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }
        }
    }

    private fun addPrompt(prompt: String, bitmap: Bitmap?) {
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, Chat(prompt, bitmap, true))
                },
                prompt = "",
                bitmap = null
            )
        }
    }

    private fun getResponse(prompt: String) {
        viewModelScope.launch {
            try {
                val chat = ChatData.getResponse(prompt)
                _chatState.update {
                    it.copy(
                        chatList = it.chatList.toMutableList().apply {
                            add(0, chat)
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error fetching response: ${e.message}")
            }
        }
    }

    private fun getResponseWithImage(prompt: String, bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                val chat = ChatData.getResponseWithImage(prompt, bitmap)
                _chatState.update {
                    it.copy(
                        chatList = it.chatList.toMutableList().apply {
                            add(0, chat)
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error fetching response with image: ${e.message}")
            }
        }
    }

}