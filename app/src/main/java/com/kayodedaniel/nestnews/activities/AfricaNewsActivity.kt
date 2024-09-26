package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kayodedaniel.nestnews.ArticleDetailActivity
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.api.NewsService
import com.kayodedaniel.nestnews.databinding.ActivityAfricaNewsBinding
import com.kayodedaniel.nestnews.model.NewsResponse
import com.kayodedaniel.nestnews.ui.ArticleAdapter
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class AfricaNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAfricaNewsBinding
    private lateinit var articleAdapter: ArticleAdapter

    //this function creates the instances of the Africa News Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAfricaNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inflates the activity's layout using view binding
        binding.AfricaRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initializes the ArticleAdapter and define the click listener for each article
        // When an article is clicked, the app opens ArticleDetailActivity with the article's link passed as an extra
        articleAdapter = ArticleAdapter { article ->
            val intent = Intent(this, ArticleDetailActivity::class.java).apply {
                putExtra("link", article.link)
            }
            startActivity(intent)
        }
        // Attaches the adapter to the RecyclerView to display the articles
        binding.AfricaRecyclerView.adapter = articleAdapter

        // Fetches articles specifically for Africa news
        fetchAfricaNewsArticles()

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

    // Function to fetch African news articles from the API
    private fun fetchAfricaNewsArticles() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://opsc7312.nerfdesigns.com/") // Base URL of the API
            .addConverterFactory(GsonConverterFactory.create())// Convert JSON responses to Kotlin objects
            .build()

        // Creates an instance of the NewsService interface to define the API methods
        val newsService = retrofit.create(NewsService::class.java)

        // Fetches African news specifically by category
        // Makes an asynchronous call to fetch African articles from the API
        newsService.getAfricaArticles().enqueue(object : Callback<NewsResponse> {
            // Handles a successful response
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.articles?.let { articles ->
                        articleAdapter.submitList(articles)
                    }
                }
            }

            // Handles a failure in the network request
            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                // Handles failure
                Toast.makeText(this@AfricaNewsActivity, "Error Can't load Articles!", Toast.LENGTH_SHORT).show();
            }
        })
    }
}
