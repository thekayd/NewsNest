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

    private lateinit var binding: ActivityMessageHomeBinding // View binding for the activity layout
    private lateinit var preferenceManager: PreferenceManager // Preference manager for user settings
    private lateinit var conversations: MutableList<ChatMessage> // List to hold chat messages
    private lateinit var conversationsAdapter: RecentConversationsAdapter // Adapter for displaying conversations
    private lateinit var database: FirebaseFirestore // Firestore database instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityMessageHomeBinding.inflate(layoutInflater)

        // Set content view to the root view of the binding
        setContentView(binding.root)

        preferenceManager = PreferenceManager(applicationContext) // Initialize preference manager
        init() // Initialize components
        loadUserDetails() // Load user details from preferences
        getToken() // Get the FCM token for messaging
        setListeners() // Set up click listeners
        listenConversations() // Start listening for chat conversations
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation) // Bottom navigation view
        bottomNavigation.setOnNavigationItemSelectedListener { item -> // Set navigation item selected listener
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java) // Navigate to MainActivity
                    startActivity(intent)
                    true
                }

                R.id.navigation_categories -> {
                    // Navigate to CategoryActivity
                    val intent = Intent(this, CategoryActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_settings -> {
                    // Navigate to SettingsActivity
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false // Return false if no valid item selected
            }
        }
    }

    private fun init() {
        conversations = ArrayList() // Initialize conversations list
        conversationsAdapter = RecentConversationsAdapter(conversations, this) // Initialize adapter
        binding.conversationsRecyclerView.adapter = conversationsAdapter // Set adapter for RecyclerView
        database = FirebaseFirestore.getInstance() // Get Firestore instance
    }

    private fun loadUserDetails() {
        binding.textName.text = preferenceManager.getString(Constants.KEY_NAME) // Set user name
        val bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT) // Decode user image
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) // Convert byte array to bitmap
        binding.imageProfile.setImageBitmap(bitmap) // Set user image in ImageView
    }

    private fun setListeners() {
        binding.imageSignOut.setOnClickListener { signOut() } // Set sign out listener
        binding.fabNewChat.setOnClickListener { // Set new chat listener
            startActivity(Intent(applicationContext, UsersActivity::class.java)) // Navigate to UsersActivity
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() // Show toast message
    }

    private fun listenConversations() {
        // Listen for conversations where the current user is the sender
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)

        // Listen for conversations where the current user is the receiver
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
            .addSnapshotListener(eventListener)
    }

    private val eventListener = EventListener<QuerySnapshot> { value, error -> // Listener for Firestore updates
        if (error != null) { // Handle errors
            return@EventListener
        }
        if (value != null) { // If the snapshot is not null
            for (documentChange in value.documentChanges) { // Iterate through document changes
                when (documentChange.type) {
                    DocumentChange.Type.ADDED -> { // Handle added conversations
                        val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID) // Get sender ID
                        val receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID) // Get receiver ID
                        val chatMessage = ChatMessage() // Create new chat message object
                        chatMessage.senderId = senderId // Set sender ID
                        chatMessage.receiverId = receiverId // Set receiver ID
                        // Check if the current user is the sender
                        if (preferenceManager.getString(Constants.KEY_USER_ID) == senderId) {
                            chatMessage.conversionImage =
                                documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE) // Set receiver image
                            chatMessage.conversionName =
                                documentChange.document.getString(Constants.KEY_RECEIVER_NAME) // Set receiver name
                            chatMessage.conversionId =
                                documentChange.document.getString(Constants.KEY_RECEIVER_ID) // Set receiver ID
                        } else { // If the current user is the receiver
                            chatMessage.conversionImage =
                                documentChange.document.getString(Constants.KEY_SENDER_IMAGE) // Set sender image
                            chatMessage.conversionName =
                                documentChange.document.getString(Constants.KEY_SENDER_NAME) // Set sender name
                            chatMessage.conversionId =
                                documentChange.document.getString(Constants.KEY_SENDER_ID) // Set sender ID
                        }
                        chatMessage.message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE) // Set last message
                        chatMessage.dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP) // Set timestamp
                        conversations.add(chatMessage) // Add chat message to the list
                    }
                    DocumentChange.Type.MODIFIED -> { // Handle modified conversations
                        for (i in conversations.indices) { // Iterate through conversations
                            val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID) // Get sender ID
                            val receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID) // Get receiver ID
                            // Find the matching conversation
                            if (conversations[i].senderId == senderId && conversations[i].receiverId == receiverId) {
                                conversations[i].message = documentChange.document.getString(Constants.KEY_LAST_MESSAGE) // Update last message
                                conversations[i].dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP) // Update timestamp
                                break // Exit the loop once the conversation is found
                            }
                        }
                    }
                    else -> {} // Handle other cases (if needed)
                }
            }
            // Fix: Ensure null safety in date comparison
            conversations.sortWith { obj1, obj2 -> // Sort conversations by date
                val date1 = obj1.dateObject ?: Date(0) // Default to earliest possible date if null
                val date2 = obj2.dateObject ?: Date(0)
                date2.compareTo(date1) // Sort in descending order
            }
            conversationsAdapter.notifyDataSetChanged() // Notify adapter of data changes
            binding.conversationsRecyclerView.smoothScrollToPosition(0) // Scroll to top of the RecyclerView
            binding.conversationsRecyclerView.visibility = View.VISIBLE // Make RecyclerView visible
            binding.progressBar.visibility = View.GONE // Hide progress bar
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token -> updateToken(token) } // Get FCM token and update
    }

    private fun updateToken(token: String) {
        val documentReference = preferenceManager.getString(Constants.KEY_USER_ID)?.let {
            database.collection(Constants.KEY_COLLECTION_USERS)
                .document(it) // Get the document reference for the user
        }
        if (documentReference != null) {
            documentReference.update(Constants.KEY_FCM_TOKEN, token) // Update the FCM token in Firestore
                .addOnFailureListener { showToast("Unable to update token") } // Handle failure
        }
    }

    private fun signOut() {
        showToast("Signing Out...") // Show signing out message
        val documentReference = preferenceManager.getString(Constants.KEY_USER_ID)?.let {
            database.collection(Constants.KEY_COLLECTION_USERS)
                .document(it) // Get the document reference for the user
        }
        val updates = HashMap<String, Any>() // Create a map for updates
        updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete() // Prepare to delete FCM token
        if (documentReference != null) {
            documentReference.update(updates) // Update Firestore with the new data
                .addOnSuccessListener {
                    preferenceManager.clear() // Clear preferences
                    startActivity(Intent(applicationContext, SignInActivity::class.java)) // Navigate to SignInActivity
                    finish() // Finish current activity
                }
                .addOnFailureListener { showToast("Unable to Sign Out") } // Handle failure
        }
    }

    override fun onConversationClicked(user: User) {
        val intent = Intent(applicationContext, ChatActivity::class.java) // Create intent for ChatActivity
        intent.putExtra(Constants.KEY_USER, user) // Pass the clicked user data
        startActivity(intent) // Start ChatActivity
    }
}
