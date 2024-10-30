package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.data.AppDatabase
import com.kayodedaniel.nestnews.data.CachedArticle
import com.kayodedaniel.nestnews.model.Article
import kotlinx.coroutines.launch
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kayodedaniel.nestnews.utils.NetworkUtils

class OfflineActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: OfflineArticleAdapter
    private lateinit var noArticlesText: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline)

        recyclerView = findViewById(R.id.recyclerViewOffline)
        noArticlesText = findViewById(R.id.noArticlesText)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        setupRecyclerView()
        setupSwipeRefresh()
        loadCachedArticles()
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            checkNetworkAndRefresh()
        }
    }

    private fun checkNetworkAndRefresh() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            // If network is available, transition to online mode
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // If still offline, just refresh the cached articles
            loadCachedArticles()
        }
    }
    private fun setupRecyclerView() {
        articleAdapter = OfflineArticleAdapter { cachedArticle ->
            val intent = Intent(this, OfflineArticleDetailActivity::class.java).apply {
                putExtra("title", cachedArticle.title)
                putExtra("description", cachedArticle.description)
                putExtra("thumbnail", cachedArticle.thumbnail)
            }
            startActivity(intent)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OfflineActivity)
            adapter = articleAdapter
        }
    }

    private fun loadCachedArticles() {
        lifecycleScope.launch {
            val articles = AppDatabase.getDatabase(applicationContext).articleDao().getAllCachedArticles()
            swipeRefreshLayout.isRefreshing = false
            if (articles.isEmpty()) {
                noArticlesText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                noArticlesText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                articleAdapter.submitList(articles)
            }
        }
    }
}
