package com.kayodedaniel.nestnews.models

import java.io.Serializable

// Class representing a user in the app, implementing Serializable to allow passing the user object between activities or fragments.
class User : Serializable {
    var name: String? = null    // The user's name.
    var image: String? = null   // URL or path to the user's profile image.
    var email: String? = null   // The user's email address.
    var token: String? = null   // FCM token used for push notifications targeting this user.
    var id: String? = null      // Unique identifier for the user (Firebase UID).
}
