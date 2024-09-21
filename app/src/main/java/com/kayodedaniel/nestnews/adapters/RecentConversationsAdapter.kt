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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(
            ItemContainerRecentConversionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.setData(chatMessages[position])
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    inner class ConversionViewHolder(val binding: ItemContainerRecentConversionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(chatMessage: ChatMessage) {
            binding.imageProfile.setImageBitmap(chatMessage.conversionImage?.let {
                getConversionImage(
                    it
                )
            })
            binding.textName.text = chatMessage.conversionName
            binding.textRecentMessage.text = chatMessage.message
            binding.root.setOnClickListener {
                val user = User().apply {
                    id = chatMessage.conversionId
                    name = chatMessage.conversionName
                    image = chatMessage.conversionImage
                }
                conversionListener.onConversationClicked(user)
            }
        }
    }

    private fun getConversionImage(encodedImage: String): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}
