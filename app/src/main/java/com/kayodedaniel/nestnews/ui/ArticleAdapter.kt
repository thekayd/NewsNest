package com.kayodedaniel.nestnews.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kayodedaniel.nestnews.databinding.ItemArticleBinding
import com.kayodedaniel.nestnews.model.Article
import com.squareup.picasso.Picasso

// Adapter class for handling a list of articles in the RecyclerView
class ArticleAdapter(private val onClick: (Article) -> Unit) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    // List to hold article data that will be displayed
    private val articles = mutableListOf<Article>()

    // Method to update the list of articles and refresh the RecyclerView
    fun submitList(newArticles: List<Article>) {
        articles.clear() // Clears the existing articles
        articles.addAll(newArticles) // Adds new articles to the list
        notifyDataSetChanged() // Notifies the adapter to refresh the data
    }

    // Inflates the layout for individual list items (articles) and creates the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        // Inflates the layout for each article item
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding) // Returns a new ViewHolder
    }

    // Binds the data from the articles list to the ViewHolder at the given position
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position]) // Pass the current article to be bound to the ViewHolder
    }

    // Returns the number of articles in the list (used by RecyclerView to determine how many items to display)
    override fun getItemCount(): Int = articles.size

    // Inner class for holding and binding views for individual article items
    inner class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        // Method to bind article data to the views in the layout
        fun bind(article: Article) {
            binding.titleTextView.text = article.title // Sets the article title in the TextView
            binding.descriptionTextView.text = article.description // Sets the article description
            // Loads the article's thumbnail image using Picasso and display it in the ImageView
            Picasso.get().load(article.thumbnail).into(binding.imageView)
            // Sets an onClick listener on the root view to handle item clicks and pass the clicked article
            binding.root.setOnClickListener {
                onClick(article)
            }
        }
    }
}
