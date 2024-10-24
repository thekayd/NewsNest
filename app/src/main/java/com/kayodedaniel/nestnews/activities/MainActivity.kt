package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.kayodedaniel.nestnews.ArticleDetailActivity
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.Utilities.ConnectivityReceiver
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.NetworkUtils
import com.kayodedaniel.nestnews.Utilities.PreferenceManager
import com.kayodedaniel.nestnews.api.NewsService
import com.kayodedaniel.nestnews.chatbotactivity.ChatBotActivity
import com.kayodedaniel.nestnews.data.NewsRepository
import com.kayodedaniel.nestnews.data.local.NewsDatabase
import com.kayodedaniel.nestnews.model.Article
import com.kayodedaniel.nestnews.ui.ArticleAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: AppCompatTextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var offlineButton: Button
    private lateinit var offlineMessage: View
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
        setupOfflineMode()
        checkConnectivity()
        fetchArticles()
        registerConnectivityReceiver()
        setupSearchView()
        setupBottomNavigation()
    }

    private fun registerConnectivityReceiver() {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(ConnectivityReceiver(), intentFilter)
    }


    private fun initializeViews() {
        preferenceManager = PreferenceManager(applicationContext)
        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        noResultsTextView = findViewById(R.id.noResultsTextView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        offlineButton = findViewById(R.id.offlineButton)
        offlineMessage = findViewById(R.id.offlineMessage)
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
            if (NetworkUtils.isNetworkAvailable(this)) {
                val intent = Intent(this, ArticleDetailActivity::class.java).apply {
                    putExtra("link", article.link)
                }
                startActivity(intent)
            } else {
                val intent = Intent(this, OfflineArticleDetailActivity::class.java).apply {
                    putExtra("link", article.link)
                    putExtra("title", article.title)
                    putExtra("description", article.description)
                }
                startActivity(intent)
            }
        }
        recyclerView.adapter = articleAdapter
    }

    private fun setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            if (NetworkUtils.isNetworkAvailable(this)) {
                fetchArticles()
            } else {
                swipeRefreshLayout.isRefreshing = false
                showError("No internet connection available")
            }
        }
    }

    private fun setupOfflineMode() {
        offlineButton.setOnClickListener {
            startActivity(Intent(this, OfflineArticlesActivity::class.java))
        }
    }

    private fun checkConnectivity() {
        val isOnline = NetworkUtils.isNetworkAvailable(this)
        updateUIForConnectivity(isOnline)
    }

    private fun updateUIForConnectivity(isOnline: Boolean) {
        offlineMessage.visibility = if (isOnline) View.GONE else View.VISIBLE
        offlineButton.visibility = if (isOnline) View.GONE else View.VISIBLE

        if (!isOnline) {
            showError("You are offline. Switch to offline mode to view cached articles.")
        }
    }

    private fun fetchArticles() {
        lifecycleScope.launch {
            try {
                if (!NetworkUtils.isNetworkAvailable(this@MainActivity)) {
                    updateUIForConnectivity(false)
                    val cachedArticles = newsRepository.getCachedArticles()
                    allArticles = cachedArticles
                    articleAdapter.submitList(cachedArticles)
                    updateUIVisibility(cachedArticles.isNotEmpty())
                    return@launch
                }

                val result = newsRepository.getArticles()
                result.onSuccess { articles ->
                    allArticles = articles
                    articleAdapter.submitList(articles)
                    updateUIVisibility(articles.isNotEmpty())
                    updateUIForConnectivity(true)
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
        recyclerView.visibility = if (hasArticles) View.VISIBLE else View.GONE
        noResultsTextView.visibility = if (hasArticles) View.GONE else View.VISIBLE
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
                else -> false
            }
        }
    }
}
