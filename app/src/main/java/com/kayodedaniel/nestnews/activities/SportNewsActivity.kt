package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
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

    private lateinit var binding: ActivitySportNewsBinding
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySportNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sportsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter and set click listener to open article detail
        articleAdapter = ArticleAdapter { article ->
            val intent = Intent(this, ArticleDetailActivity::class.java).apply {
                putExtra("link", article.link)
            }
            startActivity(intent)
        }
        binding.sportsRecyclerView.adapter = articleAdapter

        // Fetch sports articles
        fetchSportsArticles()

        // Bottom navigation handling
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

    private fun fetchSportsArticles() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://opsc7312.nerfdesigns.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val newsService = retrofit.create(NewsService::class.java)

        // Fetch sports news specifically by category
        newsService.getSportsArticles().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.articles?.let { articles ->
                        articleAdapter.submitList(articles)
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }
}
