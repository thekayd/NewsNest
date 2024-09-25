package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kayodedaniel.nestnews.R
import com.kayodedaniel.nestnews.adapters.RecentConversationsAdapter
import com.kayodedaniel.nestnews.databinding.ActivityMessageHomeBinding
import com.kayodedaniel.nestnews.listeners.ConversionListener
import com.kayodedaniel.nestnews.models.ChatMessage
import com.kayodedaniel.nestnews.models.User
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager
import java.util.*

class MessageHomeActivity : BaseActivity(), ConversionListener {

    private lateinit var binding: ActivityMessageHomeBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var conversations: MutableList<ChatMessage>
    private lateinit var conversationsAdapter: RecentConversationsAdapter
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityMessageHomeBinding.inflate(layoutInflater)

        // Set content view to the root view of the binding
        setContentView(binding.root)

        preferenceManager = PreferenceManager(applicationContext)
        init()
        loadUserDetails()
        getToken()
        setListeners()
        listenConversations()
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_categories -> {
                    // Navigate to Settings
                    val intent = Intent(this, CategoryActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_settings -> {
                    // Navigate to Settings
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    private fun init() {
        conversations = ArrayList()
        conversationsAdapter = RecentConversationsAdapter(conversations, this)
        binding.conversationsRecyclerView.adapter = conversationsAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun loadUserDetails() {
        binding.textName.text = preferenceManager.getString(Constants.KEY_NAME)
        val bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }

    private fun setListeners() {
        binding.imageSignOut.setOnClickListener { signOut() }
        binding.fabNewChat.setOnClickListener {
            startActivity(Intent(applicationContext, UsersActivity::class.java))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) {
            return@EventListener
        }
        if (value != null) {
            for (documentChange in value.documentChanges) {
                when (documentChange.type) {
                    DocumentChange.Type.ADDED -> {
                        val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        val receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        val chatMessage = ChatMessage()
                        chatMessage.senderId = senderId
                        chatMessage.receiverId = receiverId
                        if (preferenceManager.getString(Constants.KEY_USER_ID) == senderId) {
                            chatMessage.conversionImage =
                                documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE)
                            chatMessage.conversionName =
                                documentChange.document.getString(Constants.KEY_RECEIVER_NAME)
                            chatMessage.conversionId =
                                documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        } else {
                            chatMessage.conversionImage =
                                documentChange.document.getString(Constants.KEY_SENDER_IMAGE)
                            chatMessage.conversionName =
                                documentChange.document.getString(Constants.KEY_SENDER_NAME)
                            chatMessage.conversionId =
                                documentChange.document.getString(Constants.KEY_SENDER_ID)
                        }
                        chatMessage.message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                        chatMessage.dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                        conversations.add(chatMessage)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        for (i in conversations.indices) {
                            val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                            val receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                            if (conversations[i].senderId == senderId && conversations[i].receiverId == receiverId) {
                                conversations[i].message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                                conversations[i].dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                                break
                            }
                        }
                    }
                    else -> {}
                }
            }
            // Fix: Ensure null safety in date comparison
            conversations.sortWith { obj1, obj2 ->
                val date1 = obj1.dateObject ?: Date(0) // Default to earliest possible date if null
                val date2 = obj2.dateObject ?: Date(0)
                date2.compareTo(date1) // Sort in descending order
            }
            conversationsAdapter.notifyDataSetChanged()
            binding.conversationsRecyclerView.smoothScrollToPosition(0)
            binding.conversationsRecyclerView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token -> updateToken(token) }
    }

    private fun updateToken(token: String) {
        val documentReference = preferenceManager.getString(Constants.KEY_USER_ID)?.let {
            database.collection(Constants.KEY_COLLECTION_USERS)
                .document(it)
        }
        if (documentReference != null) {
            documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener { showToast("Unable to update token") }
        }
    }

    private fun signOut() {
        showToast("Signing Out...")
        val documentReference = preferenceManager.getString(Constants.KEY_USER_ID)?.let {
            database.collection(Constants.KEY_COLLECTION_USERS)
                .document(it)
        }
        val updates = HashMap<String, Any>()
        updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
        if (documentReference != null) {
            documentReference.update(updates)
                .addOnSuccessListener {
                    preferenceManager.clear()
                    startActivity(Intent(applicationContext, SignInActivity::class.java))
                    finish()
                }
                .addOnFailureListener { showToast("Unable to Sign Out") }
        }
    }

    override fun onConversationClicked(user: User) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
    }
}
