package com.kayodedaniel.nestnews.model

data class Article(
    val title: String,
    val description: String,
    val thumbnail: String,
    val link: String // Updated to match the API field "link"
)
