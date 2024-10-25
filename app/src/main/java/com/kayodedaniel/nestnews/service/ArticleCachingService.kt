package com.kayodedaniel.nestnews.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.kayodedaniel.nestnews.data.local.NewsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ArticleCachingService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var database: NewsDatabase
    private val contentFetcher = ArticleContentFetcher()

    override fun onCreate() {
        super.onCreate()
        database = NewsDatabase.getInstance(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra("articleUrl")?.let { url ->
            serviceScope.launch {
                val content = contentFetcher.fetchContent(url)
                database.articleContentDao().insertArticleContent(
                    ArticleContentEntity(url, content)
                )
                stopSelf(startId)
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}