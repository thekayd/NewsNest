package com.kayodedaniel.nestnews.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.kayodedaniel.nestnews.api.NewsService
import com.kayodedaniel.nestnews.data.local.ArticleEntity
import com.kayodedaniel.nestnews.data.local.NewsDatabase
import com.kayodedaniel.nestnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository(
    private val context: Context,
    private val newsService: NewsService,
    private val newsDatabase: NewsDatabase
) {
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun getArticles(): Result<List<Article>> = withContext(Dispatchers.IO) {
        try {
            if (isNetworkAvailable()) {
                // Fetch from network
                val response = newsService.getArticles().execute()
                if (response.isSuccessful && response.body() != null) {
                    val articles = response.body()!!.articles
                    // Cache the articles
                    newsDatabase.articleDao().insertArticles(
                        articles.map { it.toEntity() }
                    )
                    Result.success(articles)
                } else {
                    // If network request fails, fall back to cache
                    val cachedArticles = getCachedArticles()
                    Result.success(cachedArticles)
                }
            } else {
                // No network, use cached data
                val cachedArticles = getCachedArticles()
                Result.success(cachedArticles)
            }
        } catch (e: Exception) {
            val cachedArticles = getCachedArticles()
            if (cachedArticles.isNotEmpty()) {
                Result.success(cachedArticles)
            } else {
                Result.failure(e)
            }
        }
    }

    private suspend fun getCachedArticles(): List<Article> {
        return newsDatabase.articleDao().getAllArticles().map { it.toArticle() }
    }
}