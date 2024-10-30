package com.kayodedaniel.nestnews

import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.kayodedaniel.nestnews.databinding.ActivityArticleDetailBinding

// This Activity displays the details of an article, specifically showing the article's webpage using a WebView
class ArticleDetailActivity : AppCompatActivity() {

    // Binding object for accessing views in the activity layout
    private lateinit var binding: ActivityArticleDetailBinding

    // Called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflates the layout using View Binding
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root) // Sets the activity's layout to the root of the binding object

        // Retrieves the article link passed through the intent
        val articleLink = intent.getStringExtra("link")

        // Configures and load the WebView to display the article
        // Set up WebView
        binding.webView.apply {
            webViewClient = object : WebViewClient() {
                // Show the ProgressBar when page loading starts
                override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                }

                // Hide the ProgressBar when page loading finishes
                override fun onPageFinished(view: WebView, url: String) {
                    binding.progressBar.visibility = android.view.View.GONE
                }
            }
            settings.javaScriptEnabled = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            if (articleLink != null) {
                loadUrl(articleLink)
            }
        }
    }
}
