package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kayodedaniel.nestnews.ArticleDetailActivity
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager
import com.kayodedaniel.nestnews.api.NewsService
import com.kayodedaniel.nestnews.chatbotactivity.ChatBotActivity
import com.kayodedaniel.nestnews.data.NewsRepository
import com.kayodedaniel.nestnews.data.local.NewsDatabase
import com.kayodedaniel.nestnews.model.Article
import com.kayodedaniel.nestnews.ui.ArticleAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: androidx.appcompat.widget.AppCompatTextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var allArticles: List<Article> = listOf()
    private lateinit var newsRepository: NewsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initializeViews()
        setupDarkMode()
        setupRepository()
        setupAdapter()
        setupSwipeToRefresh()
        fetchArticles()
        setupSearchView()
        setupBottomNavigation()
    }


    private fun initializeViews() {
        preferenceManager = PreferenceManager(applicationContext)
        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        noResultsTextView = findViewById(R.id.noResultsTextView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupDarkMode() {
        val isDarkMode = preferenceManager.getBoolean(Constants.KEY_IS_DARK_MODE, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun setupRepository() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://opsc7312.nerfdesigns.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val newsService = retrofit.create(NewsService::class.java)
        val newsDatabase = NewsDatabase.getInstance(applicationContext)
        newsRepository = NewsRepository(applicationContext, newsService, newsDatabase)
    }

    private fun setupAdapter() {
        articleAdapter = ArticleAdapter { article ->
            val intent = Intent(this, ArticleDetailActivity::class.java).apply {
                putExtra("link", article.link)
            }
            startActivity(intent)
        }
        recyclerView.adapter = articleAdapter
    }

    private fun setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            fetchArticles()
        }
    }

    private fun fetchArticles() {
        lifecycleScope.launch {
            try {
                val result = newsRepository.getArticles()
                result.onSuccess { articles ->
                    allArticles = articles
                    articleAdapter.submitList(articles)
                    updateUIVisibility(articles.isNotEmpty())
                }.onFailure { exception ->
                    updateUIVisibility(false)
                    showError("Unable to load articles. Please try again later.")
                }
            } catch (e: Exception) {
                updateUIVisibility(false)
                showError("An error occurred while loading articles.")
            } finally {
                swipeRefreshLayout.isRefreshing = false
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
                R.id.navigation_home -> true
                R.id.navigation_categories -> {
                    startActivity(Intent(this, CategoryActivity::class.java))
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
                R.id.nav_chat_bot -> {
                    startActivity(Intent(this, ChatBotActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_chat_bot -> {
                    val intent = Intent(this, ChatBotActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }
}
