package com.kayodedaniel.nestnews.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

// Singleton object for creating and managing the Retrofit instance to interact with the Firebase Cloud Messaging (FCM) API.
object ApiClient {

    // Nullable Retrofit instance.
    private var retrofit: Retrofit? = null

    // Method to get the Retrofit client instance. If it's not initialized, a new instance will be created with a base URL and a ScalarsConverterFactory.
    fun getClient(): Retrofit? {
        // Check if the Retrofit instance is already created, and if not, build it.
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/fcm/") // Base URL for Firebase Cloud Messaging API.
                .addConverterFactory(ScalarsConverterFactory.create()) // Converter to handle scalar (plain text) responses.
                .build()
            val b = false
        }
        return retrofit // Return the Retrofit instance.
    }
}
