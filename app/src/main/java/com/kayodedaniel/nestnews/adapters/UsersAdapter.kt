package com.kayodedaniel.nestnews.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.databinding.ItemContainerUserBinding
import com.kayodedaniel.nestnews.listeners.UserListener
import com.kayodedaniel.nestnews.models.User

class UsersAdapter(private var users: List<User>, private val userListener: UserListener) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    // Inflates the user item layout when creating the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_container_user,
                parent,
                false
            )
        )
    }

    // Binds the user data to the ViewHolder for each user
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.setUserData(users[position])
    }

    // Returns the number of users in the list
    override fun getItemCount(): Int {
        return users.size
    }

    // ViewHolder class to handle and display user information
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemContainerUserBinding = ItemContainerUserBinding.bind(itemView)

        // Sets the user data (name, email, and profile image) in the layout
        fun setUserData(user: User) {
            binding.textName.text = user.name
            binding.textEmail.text = user.email

            // Decodes the profile image from Base64 or sets a default image if it's null/empty
            if (user.image != null && user.image!!.isNotEmpty()) {
                binding.imageProfile.setImageBitmap(getUserImage(user.image!!))
            } else {
                binding.imageProfile.setImageResource(R.drawable.ic_launcher_background) // Default image
            }

            // Sets click listener for user interaction
            binding.root.setOnClickListener {
                userListener.onUserClicked(user)
            }
        }
    }

    // Helper function to decode Base64-encoded user images
    private fun getUserImage(encodedImage: String): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}
