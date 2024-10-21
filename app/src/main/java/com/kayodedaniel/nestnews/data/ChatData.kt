package com.kayodedaniel.nestnews.data


import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.kayodedaniel.nestnews.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

object ChatData {
    private const val API_KEY = ""
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val geminiApiService = retrofit.create(GeminiApiService::class.java)

    suspend fun getResponse(prompt: String): Chat {
        return try {
            val request = GeminiRequest(
                contents = listOf(
                    Content(parts = listOf(Part(text = prompt)))
                )
            )
            val response = withContext(Dispatchers.IO) {
                geminiApiService.generateContent(API_KEY, request)
            }
            Chat(
                prompt = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Error: No response",
                bitmap = null,
                isFromUser = false
            )
        } catch (e: Exception) {
            Log.e("ChatData", "Error generating response: ${e.message}")
            Chat(
                prompt = e.message ?: "Error: Unknown error occurred",
                bitmap = null,
                isFromUser = false
            )
        }
    }

    suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap): Chat {
        return try {
            val base64Image = bitmapToBase64(bitmap)
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = prompt),
                            Part(
                                inlineData = InlineData(
                                    mimeType = "image/jpeg",
                                    data = base64Image
                                )
                            )
                        )
                    )
                )
            )
            val response = withContext(Dispatchers.IO) {
                geminiApiService.generateContent(API_KEY, request)
            }
            Chat(
                prompt = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Error: No response",
                bitmap = null,
                isFromUser = false
            )
        } catch (e: Exception) {
            Log.e("ChatData", "Error generating response with image: ${e.message}")
            Chat(
                prompt = e.message ?: "Error: Unknown error occurred",
                bitmap = null,
                isFromUser = false
            )
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
