package com.kayodedaniel.nestnews.model

// Data class representing the structure of the API response for news.
// The response contains a list of Article objects.
data class NewsResponse(
    val articles: List<Article> // List of articles returned by the API.
)
