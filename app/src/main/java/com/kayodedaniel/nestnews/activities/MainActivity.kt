package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kayodedaniel.nestnews.ArticleDetailActivity
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager
import com.kayodedaniel.nestnews.api.NewsService
import com.kayodedaniel.nestnews.model.NewsResponse
import com.kayodedaniel.nestnews.ui.ArticleAdapter
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView // RecyclerView to display the list of articles
    private lateinit var articleAdapter: ArticleAdapter // Adapter to manage article items in the RecyclerView
    private lateinit var preferenceManager: PreferenceManager // PreferenceManager for managing app preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Set the content view for the activity

        // Initialize the preference manager here
        preferenceManager = PreferenceManager(applicationContext)

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recyclerView) // Find the RecyclerView by its ID
        recyclerView.layoutManager = LinearLayoutManager(this) // Set LinearLayoutManager for the RecyclerView

        // Check the dark mode preference and set the theme
        val isDarkMode = preferenceManager.getBoolean(Constants.KEY_IS_DARK_MODE, false) // Get dark mode preference
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES // Enable dark mode
        } else {
            AppCompatDelegate.MODE_NIGHT_NO // Disable dark mode
        })

        // Initialize the article adapter
        articleAdapter = ArticleAdapter { article -> // Create an adapter with a click listener for articles
            val intent = Intent(this, ArticleDetailActivity::class.java).apply {
                putExtra("link", article.link) // Pass the article link to the detail activity
            }
            startActivity(intent) // Start the ArticleDetailActivity
        }
        recyclerView.adapter = articleAdapter // Set the adapter for the RecyclerView

        // Fetch articles from the API
        fetchArticles()

        // Set up the bottom navigation
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation) // Find the BottomNavigationView by its ID
        bottomNavigation.setOnNavigationItemSelectedListener { item -> // Set listener for navigation item selection
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already on Home, no action needed
                    true
                }
                R.id.navigation_categories -> {
                    val intent = Intent(this, CategoryActivity::class.java) // Create intent for CategoryActivity
                    startActivity(intent) // Start CategoryActivity
                    true
                }
                R.id.navigation_message -> {
                    val intent = Intent(this, MessageHomeActivity::class.java) // Create intent for MessageHomeActivity
                    startActivity(intent) // Start MessageHomeActivity
                    true
                }
                R.id.navigation_settings -> {
                    // Navigate to Settings
                    val intent = Intent(this, SettingsActivity::class.java) // Create intent for SettingsActivity
                    startActivity(intent) // Start SettingsActivity
                    true
                }
                else -> false // No action for other items
            }
        }
    }

    private fun fetchArticles() {
        // Create a Retrofit instance for making API calls
        val retrofit = Retrofit.Builder()
            .baseUrl("https://opsc7312.nerfdesigns.com/") // Base URL for the API
            .addConverterFactory(GsonConverterFactory.create()) // Converter for JSON responses
            .build()

        val newsService = retrofit.create(NewsService::class.java) // Create an instance of the NewsService API interface

        // Make a network call to fetch articles
        newsService.getArticles().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) { // Check if the response was successful
                    response.body()?.articles?.let { articles -> // Get articles from the response
                        articleAdapter.submitList(articles) // Submit articles to the adapter for display
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {

            }
        })
    }
}
