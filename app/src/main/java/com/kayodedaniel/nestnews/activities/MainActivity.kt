package com.kayodedaniel.nestnews.activities

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
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
import com.kayodedaniel.nestnews.data.AppDatabase
import com.kayodedaniel.nestnews.data.CachedArticle
import com.kayodedaniel.nestnews.model.Article
import com.kayodedaniel.nestnews.model.NewsResponse
import com.kayodedaniel.nestnews.ui.ArticleAdapter
import com.kayodedaniel.nestnews.utils.NetworkUtils
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: androidx.appcompat.widget.AppCompatTextView
    private var allArticles: List<Article> = listOf()
    private lateinit var database: AppDatabase
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val channelId = "article_notifications"
    private val notificationId = 101
    private lateinit var notificationManager: NotificationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create notification channel for Android 8.0 and above
        createNotificationChannel()
        // Initialize NotificationManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        preferenceManager = PreferenceManager(applicationContext)

        recyclerView = findViewById(R.id.recyclerView)
        searchView = findViewById(R.id.searchView)
        noResultsTextView = findViewById(R.id.noResultsTextView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        database = AppDatabase.getDatabase(applicationContext)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)


        val isDarkMode = preferenceManager.getBoolean(Constants.KEY_IS_DARK_MODE, false)
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        })

        articleAdapter = ArticleAdapter { article ->
            val intent = Intent(this, ArticleDetailActivity::class.java).apply {
                putExtra("link", article.link)
            }
            startActivity(intent)
        }
        recyclerView.adapter = articleAdapter
        checkNotificationSettings()
        setupSwipeRefresh()
        fetchArticles()
        setupSearchView()
        setupBottomNavigation()
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            checkNetworkAndRefresh()
        }
    }

    private fun checkNetworkAndRefresh() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            // If network is not available, transition to offline mode
            startActivity(Intent(this, OfflineActivity::class.java))
            finish()
        } else {
            // If network is available, fetch fresh articles
            fetchArticles()
        }
    }
    private fun fetchArticles() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://opsc7312.nerfdesigns.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val newsService = retrofit.create(NewsService::class.java)

        newsService.getArticles().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    response.body()?.articles?.let { articles ->
                        allArticles = articles
                        articleAdapter.submitList(articles)
                        updateUIVisibility(articles.isNotEmpty())

                        if (articles.isNotEmpty()) {
                            sendNotification("New Articles Available", "Tap to view the latest news.")
                            scheduleNotification()
                        }
                        lifecycleScope.launch {
                            articles.forEach { article ->
                                database.articleDao().insertArticle(
                                    CachedArticle(
                                        link = article.link,
                                        title = article.title,
                                        description = article.description,
                                        thumbnail = article.thumbnail
                                    )
                                )
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                if (!NetworkUtils.isNetworkAvailable(this@MainActivity)) {
                    // If offline, redirect to offline activity
                    startActivity(Intent(this@MainActivity, OfflineActivity::class.java))
                    finish()
                } else{
                    updateUIVisibility(false)
                }
            }
        })
    }
    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true // No need to check on lower versions
        }
    }

    // Function to check if notifications are enabled on the device
    private fun checkNotificationSettings() {
        val notificationManager = NotificationManagerCompat.from(this)
        // If notifications are not enabled, prompt the user to enable them
        if (!notificationManager.areNotificationsEnabled()) {
            showNotificationPromptDialog()
        }
    }
    // Function to show a dialog prompting the user to enable notifications
    private fun showNotificationPromptDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Enable Notifications")
            .setMessage("Stay updated with the latest news! Please enable notifications for the best experience.")
            .setPositiveButton("Enable") { _, _ ->
                openNotificationSettings()// Open notification settings when "Enable" is clicked
            }
            .setNegativeButton("Not Now") { dialog, _ ->
                dialog.dismiss()// Dismiss the dialog if "Not Now" is clicked
            }
            .setCancelable(false)// Prevent dialog from being canceled by tapping outside
            .show()
    }
    // Function to open the app's notification settings
    private fun openNotificationSettings() {
        val intent = Intent().apply {
            // For devices with Android Oreo or above, use the new settings action
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            } else {
                action = "android.settings.APP_NOTIFICATION_SETTINGS"
                putExtra("app_package", packageName)
                putExtra("app_uid", applicationInfo.uid)
            }
        }
        startActivity(intent) // Launch the settings intent
    }


    // Function to create a notification channel
    private fun createNotificationChannel() {
        // Check if the API version is at least Android 8.0 (Oreo) to create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Article Updates"
            val descriptionText = "Notifications for new articles"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    // Activity result launcher to request notification permission
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            sendNotification("Your Title", "Your Message")
        } else {

        }
    }
    // Function to check and request notification permission
    private fun checkAndRequestNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            sendNotification("Your Title", "Your Message")
        } else {
            // Request permission
            notificationPermissionLauncher.launch(POST_NOTIFICATIONS)
        }
    }

    // Function to schedule a notification using AlarmManager
    private fun scheduleNotification() {
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("notification_title", "News Update")
            putExtra("notification_message", "Check out the latest news articles!")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + (60 * 60 * 1000) // 1 hour


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                // Prompt user to enable exact alarms in settings
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    // Function to send a notification to the user
    private fun sendNotification(title: String, message: String) {
        // Check for notification permission
        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Create an intent that opens MainActivity
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            // Create a PendingIntent that will launch the MainActivity
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_icon)// Notification icon
                .setContentTitle(title)// Title of the notification
                .setContentText(message)// Notification message
                .setPriority(NotificationCompat.PRIORITY_HIGH)// Notification priority
                .setAutoCancel(true)// Dismiss notification when tapped
                // Launch MainAcitivy when tapped
                .setContentIntent(pendingIntent)
            // Notify the user
            notificationManager.notify(notificationId, builder.build())
        } else {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(POST_NOTIFICATIONS),
                1001
            )
        }
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
    override fun onResume() {
        super.onResume()
        if (!NetworkUtils.isNetworkAvailable(this)) {
            startActivity(Intent(this, OfflineActivity::class.java))
            finish()
        }
    }
    override fun onPause() {
        super.onPause()
        // Schedule notifications when the app goes to background
        scheduleNotification()
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