package com.kayodedaniel.nestnews.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.adapters.ChatAdapter
import com.kayodedaniel.nestnews.databinding.ActivityChatBinding
import com.kayodedaniel.nestnews.models.ChatMessage
import com.kayodedaniel.nestnews.models.User
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var receiverUser: User
    private lateinit var chatMessages: MutableList<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        loadReceiverDetails()
        init()
        listenMessages()
    }

    private fun init() {
        preferenceManager = PreferenceManager(applicationContext)
        chatMessages = ArrayList()
        chatAdapter = ChatAdapter(
            chatMessages,
            getBitmapFromEncodedString(receiverUser.image!!),
            preferenceManager.getString(Constants.KEY_USER_ID)!!
        )
        binding.chatRecyclerView.adapter = chatAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun sendMessage() {
        val message: MutableMap<String, Any> = HashMap()
        message[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_USER_ID)!!
        message[Constants.KEY_RECEIVER_ID] = receiverUser.id!!
        message[Constants.KEY_MESSAGE] = binding.inputMessage.text.toString()
        message[Constants.KEY_TIMESTAMP] = Date()
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        binding.inputMessage.text = null
    }

    private fun listenMessages() {
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) {
            return@EventListener
        }
        if (value != null) {
            val count = chatMessages.size
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val chatMessage = ChatMessage()
                    chatMessage.senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                    chatMessage.receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                    chatMessage.message = documentChange.document.getString(Constants.KEY_MESSAGE)
                    chatMessage.dateTime = getReadableDateTime(documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!)
                    chatMessage.dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                    chatMessages.add(chatMessage)
                }
            }
            chatMessages.sortWith { obj1, obj2 -> obj1.dateObject!!.compareTo(obj2.dateObject) }
            if (count == 0) {
                chatAdapter.notifyDataSetChanged()
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size, chatMessages.size)
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
            }
            binding.chatRecyclerView.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun getBitmapFromEncodedString(encodedImage: String): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun loadReceiverDetails() {
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User
        binding.textName.text = receiverUser.name
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener { onBackPressed() }
        binding.layoutSend.setOnClickListener { sendMessage() }
    }

    private fun getReadableDateTime(date: Date): String {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }
}
