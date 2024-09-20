package com.kayodedaniel.nestnews.listeners

import com.kayodedaniel.nestnews.models.User

interface UserListener {

    fun onUserClicked(user: User?)
}