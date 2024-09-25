package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.ui.CategoryAdapter

class CategoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

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
                val intent = Intent(this, BusinessActivity::class.java)
                startActivity(intent)
            }else if(category == "Opinion"){
                val intent = Intent(this, OpinionActivity::class.java)
                startActivity(intent)
            }else if(category == "World"){
                val intent = Intent(this, WorldNewsActivity::class.java)
                startActivity(intent)
            }else if(category == "Africa"){
                val intent = Intent(this, AfricaNewsActivity::class.java)
                startActivity(intent)
            }else if(category == "South Africa"){
                val intent = Intent(this, TechNewsActivity::class.java)
                startActivity(intent)
            }else if(category == "Top Stories"){
                val intent = Intent(this, TopStoriesNewsActivity::class.java)
                startActivity(intent)
            }
        }
        recyclerView.adapter = categoryAdapter
    }
}
