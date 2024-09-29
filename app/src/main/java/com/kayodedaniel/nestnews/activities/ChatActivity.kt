package com.kayodedaniel.nestnews.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
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

class ChatActivity : BaseActivity() {

    private lateinit var binding: ActivityChatBinding//binding for layout views
    private lateinit var receiverUser: User//user recieving messages
    private lateinit var chatMessages: MutableList<ChatMessage>//list to hold chat messages
    private lateinit var chatAdapter: ChatAdapter//adapter for displaying chat messages
    private lateinit var preferenceManager: PreferenceManager//preference manager for user settings
    private lateinit var database: FirebaseFirestore//database instance
    //variables for managing conversation state
    private var conversionId: String? = null
    private var isReceiverAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //calling functions
        setListeners()
        loadReceiverDetails()
        init()
        listenMessages()
    }
    // for the avaibility of the recieving user and show or hide the availibility of the user shown by "Active" label
    private fun listenAvailabilityOfReceiver() {
        receiverUser.id?.let {
            database.collection(Constants.KEY_COLLECTION_USERS)
                .document(it)
                .addSnapshotListener(this) { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (value != null) {
                        val availability = value.getLong(Constants.KEY_AVAILABILITY)?.toInt()
                        if (availability != null) {
                            isReceiverAvailable = availability == 1
                        }
                        if (isReceiverAvailable) {
                            binding.textAvailability.visibility = View.VISIBLE
                        } else {
                            binding.textAvailability.visibility = View.GONE
                        }
                    }
                }
        }
    }
    //initilize chat components and adpater
    private fun init() {
        preferenceManager = PreferenceManager(applicationContext)
        chatMessages = ArrayList()
        //set up the chat adapter with the recievers image and current user id
        receiverUser.image?.let {
            chatAdapter = ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(it),
                preferenceManager.getString(Constants.KEY_USER_ID) ?: ""
            )
            binding.chatRecyclerView.adapter = chatAdapter
        }

        database = FirebaseFirestore.getInstance()//initiliaze the firestore database
    }
    //function for sending a chat message
    private fun sendMessage() {
        val message = HashMap<String, Any?>()
        message[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_USER_ID)
        message[Constants.KEY_RECEIVER_ID] = receiverUser.id
        message[Constants.KEY_MESSAGE] = binding.inputMessage.text.toString()
        message[Constants.KEY_TIMESTAMP] = Date()
        //adding the message to the chat collection
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        //check if the conversion exists or create a new one
        if (conversionId != null) {
            updateConversion(binding.inputMessage.text.toString())
        } else {
            val conversion = HashMap<String, Any?>()
            conversion[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_USER_ID)
            conversion[Constants.KEY_SENDER_NAME] = preferenceManager.getString(Constants.KEY_NAME)
            conversion[Constants.KEY_SENDER_IMAGE] = preferenceManager.getString(Constants.KEY_IMAGE)
            conversion[Constants.KEY_RECEIVER_ID] = receiverUser.id
            conversion[Constants.KEY_RECEIVER_NAME] = receiverUser.name
            conversion[Constants.KEY_RECEIVER_IMAGE] = receiverUser.image
            conversion[Constants.KEY_LAST_MESSAGE] = binding.inputMessage.text.toString()
            conversion[Constants.KEY_TIMESTAMP] = Date()
            addConversion(conversion)
        }
        binding.inputMessage.text = null
    }


    //listen for incoming messages from both users
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
    //event listener for message changes
    private val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error != null) return@EventListener

        if (value != null) {
            val count = chatMessages.size
            for (documentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val chatMessage = ChatMessage().apply {
                        senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        message = documentChange.document.getString(Constants.KEY_MESSAGE)
                        dateTime = getReadableDateTime(documentChange.document.getDate(Constants.KEY_TIMESTAMP) ?: Date())
                        dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP) ?: Date()
                    }
                    chatMessages.add(chatMessage)//add the new message to the list
                }
            }
            chatMessages.sortWith { obj1, obj2 -> obj1.dateObject?.compareTo(obj2.dateObject) ?: 0 }//sort messages by date and notify adapter of changes
            if (count == 0) {
                chatAdapter.notifyDataSetChanged()
            } else {
                chatAdapter.notifyItemRangeChanged(chatMessages.size, chatMessages.size)
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
            }
            binding.chatRecyclerView.visibility = View.VISIBLE //show chat view
        }
        binding.progressBar.visibility = View.GONE
        if (conversionId == null) checkForConversion() //check for exsiting conversation
    }
    //decode base64 encoded image string into bitmpa for display purpose
    private fun getBitmapFromEncodedString(encodedImage: String): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
    //function to load the reciever user from the intent
    private fun loadReceiverDetails() {
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User
        binding.textName.text = receiverUser.name
    }
    //setting the click listeners for all UI elements
    private fun setListeners() {
        binding.imageBack.setOnClickListener { onBackPressed() }
        binding.layoutSend.setOnClickListener { sendMessage() }
    }
    // Format date to readable string
    private fun getReadableDateTime(date: Date): String {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }
    // Add a new conversion entry in Firestore
    private fun addConversion(conversion: HashMap<String, Any?>) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .add(conversion)
            .addOnSuccessListener { documentReference -> conversionId = documentReference.id }
    }
    // Update an existing conversion in Firestore
    private fun updateConversion(message: String) {
        val documentReference: DocumentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId!!)
        documentReference.update(
            Constants.KEY_LAST_MESSAGE, message,
            Constants.KEY_TIMESTAMP, Date()
        )
    }
    // Check for existing conversion based on chat messages
    private fun checkForConversion() {
        if (chatMessages.isNotEmpty()) {
            receiverUser.id?.let {
                checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID) ?: "",
                    it
                )
            }
            receiverUser.id?.let {
                checkForConversionRemotely(
                    it,
                    preferenceManager.getString(Constants.KEY_USER_ID) ?: ""
                )
            }
        }
    }
    // Check for existing conversation in Firestore
    private fun checkForConversionRemotely(senderId: String, receiverId: String) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
            .get()
            .addOnCompleteListener(conversionOnCompleteListener)
    }
    // Handle the completion of conversation checks
    private val conversionOnCompleteListener = OnCompleteListener<QuerySnapshot> { task ->
        if (task.isSuccessful && task.result != null && task.result!!.documents.isNotEmpty()) {
            val documentSnapshot = task.result!!.documents[0]
            conversionId = documentSnapshot.id
        }
    }
    // When the activity is paused, save the availability status
    override fun onResume() {
        super.onResume()
        listenAvailabilityOfReceiver()
    }
}
