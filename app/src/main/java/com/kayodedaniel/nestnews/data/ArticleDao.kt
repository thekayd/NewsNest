package com.kayodedaniel.nestnews.data

import androidx.room.*

@Dao
interface ArticleDao {
    @Query("SELECT * FROM cached_articles ORDER BY timestamp DESC")
    suspend fun getAllCachedArticles(): List<CachedArticle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: CachedArticle)

    @Query("DELETE FROM cached_articles")
    suspend fun deleteAllArticles()

    @Query("SELECT * FROM cached_articles WHERE link = :articleLink")
    suspend fun getArticleByLink(articleLink: String): CachedArticle?
}