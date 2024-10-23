package com.kayodedaniel.nestnews.data

import com.kayodedaniel.nestnews.data.local.ArticleEntity
import com.kayodedaniel.nestnews.model.Article

fun Article.toEntity() = ArticleEntity(
    link = link,
    title = title,
    description = description,
    thumbnail = thumbnail,
    isFavorite = isFavorite
)

fun ArticleEntity.toArticle() = Article(
    link = link,
    title = title,
    description = description,
    thumbnail = thumbnail,
    isFavorite = isFavorite
)