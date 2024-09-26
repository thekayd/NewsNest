package com.kayodedaniel.nestnews.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

// Retrofit interface for making POST requests to send messages via Firebase Cloud Messaging (FCM) API.
interface ApiService {

    // Sends a message using a POST request. The headers are dynamic and passed as a HashMap, and the message body is a plain text string.
    @POST("send")
    fun sendMessage(
        @HeaderMap headers: HashMap<String, String>,  // Dynamic headers (like authorization tokens or content type) passed in the request.
        @Body messageBody: String                     // The body of the request, containing the message to be sent, in plain text.
    ): Call<String>  // Returns a Call object with a String response from the API.
}
