package com.kayodedaniel.nestnews.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kayodedaniel.nestnews.databinding.ItemContainerRecentConversionBinding
import com.kayodedaniel.nestnews.listeners.ConversionListener
import com.kayodedaniel.nestnews.models.ChatMessage
import com.kayodedaniel.nestnews.models.User

class RecentConversationsAdapter(
    private val chatMessages: List<ChatMessage>,
    private val conversionListener: ConversionListener
) : RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>() {

    // Creates the view for each recent conversation item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(
            ItemContainerRecentConversionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    // Binds the data of the chat message to the view
    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.setData(chatMessages[position])
    }

    override fun getItemCount(): Int {
        return chatMessages.size // Returns the number of recent conversations
    }

    // ViewHolder class to hold and manage the view for each conversation
    inner class ConversionViewHolder(val binding: ItemContainerRecentConversionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Sets data for each chat message in the list, including conversion image, name, and recent message
        fun setData(chatMessage: ChatMessage) {
            binding.imageProfile.setImageBitmap(chatMessage.conversionImage?.let {
                getConversionImage(it) // Decodes and sets the image
            })
            binding.textName.text = chatMessage.conversionName
            binding.textRecentMessage.text = chatMessage.message
            binding.root.setOnClickListener {
                // Creates a User object from the chat message data and triggers the conversation listener
                val user = User().apply {
                    id = chatMessage.conversionId
                    name = chatMessage.conversionName
                    image = chatMessage.conversionImage
                }
                conversionListener.onConversationClicked(user)
            }
        }
    }

    // Helper function to decode the Base64-encoded profile image
    private fun getConversionImage(encodedImage: String): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}
