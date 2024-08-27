package com.kayodedaniel.nestnews.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kayodedaniel.nestnews.databinding.ItemArticleBinding
import com.kayodedaniel.nestnews.model.Article
import com.squareup.picasso.Picasso

class ArticleAdapter(private val onClick: (Article) -> Unit) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private val articles = mutableListOf<Article>()

    fun submitList(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            binding.titleTextView.text = article.title
            binding.descriptionTextView.text = article.description
            Picasso.get().load(article.thumbnail).into(binding.imageView)
            binding.root.setOnClickListener {
                onClick(article)
            }
        }
    }
}
