package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.api.NewsService
import com.kayodedaniel.nestnews.data.NewsRepository
import com.kayodedaniel.nestnews.data.local.NewsDatabase
import com.kayodedaniel.nestnews.ui.ArticleAdapter
import kotlinx.coroutines.launch

class OfflineArticlesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var newsRepository: NewsRepository
    private lateinit var noArticlesView: TextView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_articles)

        initializeViews()
        setupToolbar()
        setupRepository()
        setupAdapter()
        loadCachedArticles()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerView)
        noArticlesView = findViewById(R.id.noArticlesView)
        toolbar = findViewById(R.id.toolbar)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupToolbar() {
        toolbar.setTitle(R.string.offline_articles)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRepository() {
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl("https://opsc7312.nerfdesigns.com/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()

        val newsService = retrofit.create(NewsService::class.java)
        val newsDatabase = NewsDatabase.getInstance(applicationContext)
        newsRepository = NewsRepository(applicationContext, newsService, newsDatabase)
    }

    private fun setupAdapter() {
        articleAdapter = ArticleAdapter { article ->
            val intent = Intent(this, OfflineArticleDetailActivity::class.java).apply {
                putExtra("link", article.link)
                putExtra("title", article.title)
                putExtra("description", article.description)
            }
            startActivity(intent)
        }
        recyclerView.adapter = articleAdapter
    }

    private fun loadCachedArticles() {
        lifecycleScope.launch {
            val articles = newsRepository.getCachedArticles()
            articleAdapter.submitList(articles)
            updateUIVisibility(articles.isNotEmpty())
        }
    }

    private fun updateUIVisibility(hasArticles: Boolean) {
        recyclerView.visibility = if (hasArticles) View.VISIBLE else View.GONE
        noArticlesView.visibility = if (hasArticles) View.GONE else View.VISIBLE
    }
}
