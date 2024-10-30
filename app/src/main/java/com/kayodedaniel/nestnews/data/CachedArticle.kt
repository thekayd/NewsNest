package com.kayodedaniel.nestnews.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_articles")
data class CachedArticle(
    @PrimaryKey
    val link: String,
    val title: String,
    val description: String,
    val thumbnail: String,
    val timestamp: Long = System.currentTimeMillis()
)
