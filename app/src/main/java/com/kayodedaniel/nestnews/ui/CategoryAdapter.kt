package com.kayodedaniel.nestnews.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kayodedaniel.nestnews.databinding.ItemCategoryBinding

// Adapter class for handling a list of categories in the RecyclerView
class CategoryAdapter(
    private val categories: List<String>, // List of categories to display
    private val onCategoryClick: (String) -> Unit // Callback function for when a category is clicked
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    // Inflates the layout for individual category items and creates the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        // Inflates the layout for each category item
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding) // Return a new ViewHolder
    }

    // Binds the category data to the ViewHolder at the given position
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position]) // Pass the current category to be bound to the ViewHolder
    }

    // Returns the number of categories in the list (used by RecyclerView to determine how many items to display)
    override fun getItemCount(): Int = categories.size

    // Inner class for holding and binding views for individual category items
    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        // Method to bind category data to the views in the layout
        fun bind(category: String) {
            binding.categoryTitle.text = category // Sets the category title in the TextView
            // Sets an onClick listener on the "Read More" button to handle item clicks and pass the clicked category
            binding.readMoreButton.setOnClickListener {
                onCategoryClick(category) // Triggers the callback with the selected category
            }
        }
    }
}
