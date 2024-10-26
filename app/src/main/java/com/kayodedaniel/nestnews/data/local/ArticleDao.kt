package com.kayodedaniel.nestnews.data.local

import androidx.room.*

@Dao
interface ArticleDao {
//    @Query("SELECT * FROM articles ORDER BY timestamp DESC")
//    suspend fun getAllArticles(): List<ArticleEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertArticles(articles: List<ArticleEntity>)
//
//    @Query("DELETE FROM articles")
//    suspend fun deleteAllArticles()
}