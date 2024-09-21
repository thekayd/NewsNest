package com.kayodedaniel.nestnews.listeners

import com.kayodedaniel.nestnews.models.User

interface ConversionListener {
    fun onConversationClicked(user: User)
}
