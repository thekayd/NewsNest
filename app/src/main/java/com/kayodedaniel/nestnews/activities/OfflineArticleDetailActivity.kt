package com.kayodedaniel.nestnews.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.kayodedaniel.nestnews.R

class OfflineArticleDetailActivity : AppCompatActivity() {
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_article_detail)

        initializeViews()
        setupToolbar()
        displayArticleContent()
    }

    private fun initializeViews() {
        titleTextView = findViewById(R.id.titleTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        toolbar = findViewById(R.id.toolbar)
    }

    private fun setupToolbar() {
        toolbar.setTitle(R.string.article_detail)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun displayArticleContent() {
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""

        titleTextView.text = title
        descriptionTextView.text = description
    }
}