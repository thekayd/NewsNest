package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.kayodedaniel.nestnews.databinding.ActivitySignInBinding
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding // Binding for the sign-in layout
    private lateinit var preferenceManager: PreferenceManager // For managing user preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(applicationContext) // Initialize preference manager

        // Check if the user is already signed in
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            // If signed in, go to MainActivity
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the SignInActivity
        }

        binding = ActivitySignInBinding.inflate(layoutInflater) // Inflate the sign-in layout
        setContentView(binding.root) // Set the content view
        setListeners() // Set click listeners for buttons
    }

    private fun setListeners() {
        // Click listener for creating a new account
        binding.textCreateNewAccount.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java)) // Start SignUpActivity
        }

        // Click listener for the sign-in button
        binding.buttonSignIn.setOnClickListener {
            if (isValidSignInDetails()) { // Check if sign-in details are valid
                signIn() // Call the sign-in function
            }
        }
    }

    private fun signIn() {
        loading(true) // Show loading progress
        val database = FirebaseFirestore.getInstance() // Get Firestore instance

        // Query the database to find the user with the provided email and password
        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD, binding.InputPassword.text.toString())
            .get()
            .addOnCompleteListener { task -> // Handle the result of the query
                if (task.isSuccessful && task.result != null && task.result.documents.size > 0) {
                    // If the query is successful and a user is found
                    val documentSnapshot: DocumentSnapshot = task.result.documents[0] // Get the user document
                    // Save user data to preferences
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.id)
                    preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME) ?: "")
                    preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL) ?: "")
                    preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE) ?: "")

                    // Start MainActivity after successful sign-in
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // Clear activity stack
                    startActivity(intent)
                } else {
                    loading(false) // Hide loading progress
                    showToast("Unable To Sign In") // Show error message
                }
            }
    }

    private fun loading(isLoading: Boolean) {
        // Show or hide loading progress based on isLoading
        if (isLoading) {
            binding.buttonSignIn.visibility = View.INVISIBLE // Hide sign-in button
            binding.ProgressBar.visibility = View.VISIBLE // Show progress bar
        } else {
            binding.ProgressBar.visibility = View.INVISIBLE // Hide progress bar
            binding.buttonSignIn.visibility = View.VISIBLE // Show sign-in button
        }
    }

    private fun showToast(message: String) {
        // Show a toast message
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidSignInDetails(): Boolean {
        // Check if all sign-in details are valid
        return when {
            binding.inputEmail.text.toString().trim().isEmpty() -> {
                showToast("Enter Email") // Show error if email is empty
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches() -> {
                showToast("Enter Valid Email") // Show error if email is not valid
                false
            }
            binding.InputPassword.text.toString().trim().isEmpty() -> {
                showToast("Enter Password") // Show error if password is empty
                false
            }
            else -> true // All details are valid
        }
    }
}
