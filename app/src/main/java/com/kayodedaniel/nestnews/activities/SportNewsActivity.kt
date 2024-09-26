package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kayodedaniel.nestnews.ArticleDetailActivity
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.api.NewsService
import com.kayodedaniel.nestnews.databinding.ActivitySportNewsBinding
import com.kayodedaniel.nestnews.model.NewsResponse
import com.kayodedaniel.nestnews.ui.ArticleAdapter
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class SportNewsActivity : AppCompatActivity() {

    // Declares variables for view binding and the article adapter
    private lateinit var binding: ActivitySportNewsBinding
    private lateinit var articleAdapter: ArticleAdapter

    // This function initializes the SportNewsActivity when the screen is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySportNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sets up the RecyclerView with a LinearLayoutManager to display the articles in a vertical list
        binding.sportsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initializes the ArticleAdapter and define the click listener for each article
        // When an article is clicked, the app opens ArticleDetailActivity with the article's link passed as an extra
        articleAdapter = ArticleAdapter { article ->
            val intent = Intent(this, ArticleDetailActivity::class.java).apply {
                putExtra("link", article.link)
            }
            startActivity(intent)
        }
        // Attaches the adapter to the RecyclerView to display the articles
        binding.sportsRecyclerView.adapter = articleAdapter

        // Fetch articles specifically for Sports news
        fetchSportsArticles()

        // Handles bottom navigation bar item selections
        // Based on the item clicked, the app navigates to different activities
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_categories -> {
                    val intent = Intent(this, CategoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_message -> {
                    val intent = Intent(this, MessageHomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    // Function to fetch Sports news articles from the API
    private fun fetchSportsArticles() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://opsc7312.nerfdesigns.com/") // Base URL of the API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Creates an instance of the NewsService interface to define the API methods
        val newsService = retrofit.create(NewsService::class.java)

        // Fetch sports news specifically by category
        // Make an asynchronous call to fetch sports articles from the API
        newsService.getSportsArticles().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.articles?.let { articles ->
                        // Submits the fetched articles to the adapter for display in the RecyclerView
                        articleAdapter.submitList(articles)
                    }
                }
            }

            // Handles a failure in the network request
            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Toast.makeText(this@SportNewsActivity, "Error Can't load Articles!", Toast.LENGTH_SHORT).show();
            }
        })
    }
}
