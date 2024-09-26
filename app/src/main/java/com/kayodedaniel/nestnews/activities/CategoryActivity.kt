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

    // Declares the RecyclerView for displaying the news categories
    private lateinit var recyclerView: RecyclerView
    // Declares the CategoryAdapter for populating the RecyclerView with category data
    private lateinit var categoryAdapter: CategoryAdapter
    // Declares the PreferenceManager for handling user preferences (e.g., dark mode settings)
    private lateinit var preferenceManager: PreferenceManager

    // Overrides the onCreate method to initialize the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Sets the content view to the activity_category layout file
        setContentView(R.layout.activity_category)

        // Initializes the PreferenceManager with the application context
        preferenceManager = PreferenceManager(applicationContext)

        // Checks if dark mode is enabled by retrieving the value from preferences
        val isDarkMode = preferenceManager.getBoolean(Constants.KEY_IS_DARK_MODE, false)
        // Sets the appropriate night mode based on the dark mode preference
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        })

        // Initializes the RecyclerView and set its layout manager to a vertical LinearLayoutManager
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Defines a list of news categories that will be displayed in the RecyclerView
        val categories = listOf(
            "Top Stories", "South Africa", "Africa", "World", "Business", "Sport", "Opinion", "Tech"
        )

        // Initializes the CategoryAdapter with the categories and a click listener
        categoryAdapter = CategoryAdapter(categories) { category ->
            // Based on the category clicked, launches the corresponding activity using Intents
            when (category) {
                "Sport" -> {
                    val intent = Intent(this, SportNewsActivity::class.java)
                    startActivity(intent) // Starts SportNewsActivity
                }
                "Tech" -> {
                    val intent = Intent(this, TechNewsActivity::class.java)
                    startActivity(intent) // Starts TechNewsActivity
                }
                "Business" -> {
                    val intent = Intent(this, BusinessNewsActivity::class.java)
                    startActivity(intent) // Starts BusinessNewsActivity
                }
                "Opinion" -> {
                    val intent = Intent(this, OpinionNewsActivity::class.java)
                    startActivity(intent) // Starts OpinionNewsActivity
                }
                "World" -> {
                    val intent = Intent(this, WorldNewsActivity::class.java)
                    startActivity(intent) // Starts WorldNewsActivity
                }
                "Africa" -> {
                    val intent = Intent(this, AfricaNewsActivity::class.java)
                    startActivity(intent) // Starts AfricaNewsActivity
                }
                "South Africa" -> {
                    val intent = Intent(this, SouthAfricaNewsActivity::class.java)
                    startActivity(intent) // Starts SouthAfricaNewsActivity
                }
                "Top Stories" -> {
                    val intent = Intent(this, TopStoriesNewsActivity::class.java)
                    startActivity(intent) // Starts TopStoriesNewsActivity
                }
            }
        }
        // Sets the adapter for the RecyclerView to the initialized CategoryAdapter
        recyclerView.adapter = categoryAdapter

        // Initializes the BottomNavigationView for navigating between different activities
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        // Sets up a listener to handle navigation item selections
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            // Checks which navigation item was clicked and perform the appropriate action
            when (item.itemId) {
                R.id.navigation_home -> {
                    // When the home navigation item is selected, this starts the MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_message -> {
                    // When the message navigation item is selected, this starts the MessageHomeActivity
                    val intent = Intent(this, MessageHomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_settings -> {
                    // When the settings navigation item is selected, this starts the SettingsActivity
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false // Returns false if none of the cases match
            }
        }
    }
}
