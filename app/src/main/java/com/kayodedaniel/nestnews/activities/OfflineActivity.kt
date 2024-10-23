
package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kayodedaniel.nestnews.ArticleDetailActivity
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.data.local.NewsDatabase
import com.kayodedaniel.nestnews.model.Article
import com.kayodedaniel.nestnews.ui.ArticleAdapter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.google.android.material.snackbar.Snackbar
import com.kayodedaniel.nestnews.data.toArticle

class OfflineActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: androidx.appcompat.widget.AppCompatTextView
    private var allArticles: List<Article> = listOf()
    private lateinit var newsDatabase: NewsDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline)

        initializeViews()
        setupAdapter()
        loadOfflineArticles()
        setupSearchView()
        setupBottomNavigation()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        noResultsTextView = findViewById(R.id.noResultsTextView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        newsDatabase = NewsDatabase.getInstance(applicationContext)
    }

    private fun setupAdapter() {
        articleAdapter = ArticleAdapter { article ->
            // Check if the article link is valid before starting the activity
            if (article.link.isNotEmpty()) {
                val intent = Intent(this, ArticleDetailActivity::class.java).apply {
                    putExtra("link", article.link)
                    putExtra("isOffline", true)
                }
                startActivity(intent)
            } else {
                showError("Unable to load article content.")
            }
        }
        recyclerView.adapter = articleAdapter
    }

    private fun loadOfflineArticles() {
        lifecycleScope.launch {
            try {
                val articles = newsDatabase.articleDao().getAllArticles().map { it.toArticle() }
                // Only keep articles with non-empty links
                allArticles = articles.filter { it.link.isNotEmpty() }
                articleAdapter.submitList(allArticles)
                updateUIVisibility(allArticles.isNotEmpty())
            } catch (e: Exception) {
                showError("Error loading offline articles")
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG).show()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterArticles(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterArticles(newText)
                return true
            }
        })
    }

    private fun filterArticles(query: String?) {
        val filteredList = if (query.isNullOrBlank()) {
            allArticles
        } else {
            allArticles.filter { article ->
                article.title.contains(query, ignoreCase = true) ||
                        article.description.contains(query, ignoreCase = true)
            }
        }
        articleAdapter.submitList(filteredList)
        updateUIVisibility(filteredList.isNotEmpty())
    }

    private fun updateUIVisibility(hasArticles: Boolean) {
        if (hasArticles) {
            recyclerView.visibility = View.VISIBLE
            noResultsTextView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            noResultsTextView.visibility = View.VISIBLE
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_message -> {
                    startActivity(Intent(this, MessageHomeActivity::class.java))
                    true
                }
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
