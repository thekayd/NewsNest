package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager
import com.kayodedaniel.nestnews.ui.CategoryAdapter

class CategoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        preferenceManager = PreferenceManager(applicationContext)

        val isDarkMode = preferenceManager.getBoolean(Constants.KEY_IS_DARK_MODE, false)
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        })

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val categories = listOf(
            "Top Stories", "South Africa", "Africa", "World", "Business", "Sport", "Opinion", "Tech"
        )

        categoryAdapter = CategoryAdapter(categories) { category ->
            if (category == "Sport") {
                val intent = Intent(this, SportNewsActivity::class.java)
                startActivity(intent)
            } else if(category == "Tech"){
                val intent = Intent(this, TechNewsActivity::class.java)
                startActivity(intent)
            }else if(category == "Business"){
                val intent = Intent(this, BusinessNewsActivity::class.java)
                startActivity(intent)
            }else if(category == "Opinion"){
                val intent = Intent(this, OpinionNewsActivity::class.java)
                startActivity(intent)
            }else if(category == "World"){
                val intent = Intent(this, WorldNewsActivity::class.java)
                startActivity(intent)
            }else if(category == "Africa"){
                val intent = Intent(this, AfricaNewsActivity::class.java)
                startActivity(intent)
            }else if(category == "South Africa"){
                val intent = Intent(this, SouthAfricaNewsActivity::class.java)
                startActivity(intent)
            }else if(category == "Top Stories"){
                val intent = Intent(this, TopStoriesNewsActivity::class.java)
                startActivity(intent)
            }
        }
        recyclerView.adapter = categoryAdapter
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_message -> {
                    val intent = Intent(this, MessageHomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    // Navigate to Settings
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
