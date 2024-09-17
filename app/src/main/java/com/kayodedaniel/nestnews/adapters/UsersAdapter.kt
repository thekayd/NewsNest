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
import com.kayodedaniel.nestnews.models.User

class UsersAdapter(private val users: List<User>) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_container_user,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.setUserData(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemContainerUserBinding = ItemContainerUserBinding.bind(itemView)

        fun setUserData(user: User) {
            binding.textName.text = user.name
            binding.textEmail.text = user.email

            // Check if the image is null or empty before attempting to decode
            if (user.image != null && user.image!!.isNotEmpty()) {
                binding.imageProfile.setImageBitmap(getUserImage(user.image!!))
            } else {
                // Set a default image or placeholder if user.image is null
                binding.imageProfile.setImageResource(R.drawable.ic_launcher_background)
            }
        }
    }

    private fun getUserImage(encodedImage: String): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}
