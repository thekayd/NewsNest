package com.kayodedaniel.nestnews.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.data.CachedArticle
import com.squareup.picasso.Picasso

class OfflineArticleAdapter(private val onClick: (CachedArticle) -> Unit) :
    RecyclerView.Adapter<OfflineArticleAdapter.ViewHolder>() {

    private var articles = listOf<CachedArticle>()

    fun submitList(newArticles: List<CachedArticle>) {
        articles = newArticles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount() = articles.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(article: CachedArticle) {
            titleTextView.text = article.title
            descriptionTextView.text = article.description
            Picasso.get()
                .load(article.thumbnail)
                .placeholder(R.drawable.news_nest)
                .error(R.drawable.news_nest)
                .into(imageView)

            itemView.setOnClickListener { onClick(article) }
        }
    }
}