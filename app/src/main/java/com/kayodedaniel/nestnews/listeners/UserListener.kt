package com.kayodedaniel.nestnews.listeners

import com.kayodedaniel.nestnews.models.User

// Interface to handle user interactions when a user is clicked.
interface UserListener {

    // Method to be triggered when a user is clicked. The 'User' object represents the selected user, and it can be null if no valid user is clicked.
    fun onUserClicked(user: User?)
}
