package com.kayodedaniel.nestnews.model

// Data class representing a news article. Each article has a title, description, thumbnail, and link.
data class Article(
    val title: String,        // Title of the news article.
    val description: String,  // Short description or summary of the article.
    val thumbnail: String,    // URL of the article's thumbnail image.
    val link: String,         // URL or link to the full article, updated to match the API field "link".
    val isFavorite: Boolean = false // New field to track if the article is marked as a favorite, default is 'false'.
)
