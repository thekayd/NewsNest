package com.kayodedaniel.nestnews.listeners

import com.kayodedaniel.nestnews.models.User

// Interface to handle user interactions when a conversation is clicked.
interface ConversionListener {
    // Method to be triggered when a user clicks on a conversation. The 'User' object represents the selected user in the conversation.
    fun onConversationClicked(user: User)
}
