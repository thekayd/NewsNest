package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.kayodedaniel.nestnews.adapters.UsersAdapter
import com.kayodedaniel.nestnews.databinding.ActivityUsersBinding
import com.kayodedaniel.nestnews.listeners.UserListener
import com.kayodedaniel.nestnews.models.User
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager

// UsersActivity class implements UserListener interface to handle user clicks
class UsersActivity : BaseActivity(), UserListener {

    // View binding for the activity layout
    private lateinit var binding: ActivityUsersBinding
    // Manages user preferences (e.g., storing user data locally)
    private lateinit var preferenceManager: PreferenceManager

    // Called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize PreferenceManager
        preferenceManager = PreferenceManager(applicationContext)
        // Set listeners for UI elements
        setListeners()
        // Fetch the list of users
        getUsers()
    }

    // Set click listeners for UI elements
    private fun setListeners() {
        // Handle back button click to navigate back to the previous screen
        binding.imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    // Fetches users from Firestore and populates the RecyclerView
    private fun getUsers() {
        loading(true) // Show loading indicator

        // Firestore instance to fetch user data
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .get() // Get all documents in the users collection
            .addOnCompleteListener { task ->
                loading(false) // Hide loading indicator after the operation is complete

                // Get the current user's ID from preferences
                val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)

                // Check if the task was successful and if the result contains any data
                if (task.isSuccessful && task.result != null) {
                    val users: MutableList<User> = ArrayList() // List to hold the user data

                    // Loop through each document in the result
                    for (queryDocumentSnapshot: QueryDocumentSnapshot in task.result) {
                        // Skip the current user
                        if (currentUserId == queryDocumentSnapshot.id) {
                            continue
                        }

                        // Create a new User object and populate it with data from Firestore
                        val user = User().apply {
                            name = queryDocumentSnapshot.getString(Constants.KEY_NAME)
                            email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL)
                            image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE)
                            token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN)
                            id = queryDocumentSnapshot.id // Set the user's Firestore document ID
                        }

                        // Add the user to the list
                        users.add(user)
                    }

                    // Check if the users list is not empty
                    if (users.isNotEmpty()) {
                        // Create and set an adapter for the RecyclerView to display the users
                        val usersAdapter = UsersAdapter(users, this)
                        binding.usersRecyclerView.adapter = usersAdapter
                        binding.usersRecyclerView.visibility = View.VISIBLE // Show the RecyclerView
                    } else {
                        // Show an error message if no users are found
                        showErrorMessage()
                    }
                } else {
                    // Show an error message if the task failed
                    showErrorMessage()
                }
            }
    }

    // Display an error message if no users are available
    private fun showErrorMessage() {
        binding.textErrorMessage.text = String.format("%s", "No User Available")
        binding.textErrorMessage.visibility = View.VISIBLE // Show the error message
    }

    // Show or hide the loading indicator based on the isLoading flag
    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    // Handle user clicks on a specific user in the list
    override fun onUserClicked(user: User?) {
        // Start ChatActivity and pass the clicked user as an extra
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
        finish() // Close the current activity
    }

}
