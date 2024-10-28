package com.kayodedaniel.nestnews.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kayodedaniel.nestnews.R
import com.squareup.picasso.Picasso
import android.widget.ImageView
import android.widget.TextView

class OfflineArticleDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_article_detail)

        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val thumbnail = intent.getStringExtra("thumbnail") ?: ""

        findViewById<TextView>(R.id.titleTextView).text = title
        findViewById<TextView>(R.id.descriptionTextView).text = description

        val imageView = findViewById<ImageView>(R.id.thumbnailImageView)
        Picasso.get()
            .load(thumbnail)
            .placeholder(R.drawable.news_nest)
            .error(R.drawable.news_nest)
            .into(imageView)
    }
}