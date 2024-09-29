package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.kayodedaniel.nestnews.databinding.ActivityProfileBinding
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

class ProfileActivity : AppCompatActivity() {

    // View binding for the activity layout
    private lateinit var binding: ActivityProfileBinding
    // Holds the base64-encoded image string
    private var encodedImage: String? = null
    // Manages user preferences (e.g., storing user data locally)
    private lateinit var preferenceManager: PreferenceManager
    // Stores the user ID of the currently logged-in user
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize PreferenceManager and get the user ID
        preferenceManager = PreferenceManager(applicationContext)
        userId = preferenceManager.getString(Constants.KEY_USER_ID).toString()

        // Load user profile data from Firestore
        loadUserProfile()

        // Set click listeners for buttons and other UI elements
        setListeners()
    }

    // Set click listeners for buttons (update, image selection, back button)
    private fun setListeners() {
        // Listener for the update profile button
        binding.btnUpdateAccount.setOnClickListener {
            if (isValidProfileDetails()) { // Validate profile details
                updateProfile() // Update profile if valid
            }
        }

        // Listener for the image edit button (opens image picker)
        binding.ProfileImageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent) // Launch image picker
        }

        // Listener for the back button (navigates to SettingsActivity)
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    // Load the current user's profile data from Firestore and display it
    private fun loadUserProfile() {
        val database = FirebaseFirestore.getInstance() // Firestore instance
        database.collection(Constants.KEY_COLLECTION_USERS)
            .document(userId) // Access the document for the current user
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) { // Check if the document exists
                    // Set the user's name and email fields
                    binding.editName.setText(documentSnapshot.getString(Constants.KEY_NAME))
                    binding.editEmail.setText(documentSnapshot.getString(Constants.KEY_EMAIL))

                    // Get and display the user's profile image, if available
                    val image = documentSnapshot.getString(Constants.KEY_IMAGE)
                    if (image != null && image.isNotEmpty()) {
                        val decodedImage = Base64.decode(image, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
                        binding.ProfileImageEdit.setImageBitmap(bitmap) // Set profile image
                        encodedImage = image // Store the encoded image string
                    }
                }
            }
    }

    // Update the user's profile data in Firestore
    private fun updateProfile() {
        loading(true) // Show loading indicator

        // Create a HashMap containing the updated profile data
        val database = FirebaseFirestore.getInstance()
        val updates = hashMapOf<String, Any>(
            Constants.KEY_NAME to binding.editName.text.toString(),
            Constants.KEY_EMAIL to binding.editEmail.text.toString(),
            Constants.KEY_PASSWORD to binding.editConfirmPassword.text.toString()
        )

        // Add the image to the update if it exists
        if (!encodedImage.isNullOrEmpty()) {
            updates[Constants.KEY_IMAGE] = encodedImage!!
        }

        // Update the user's profile in Firestore
        database.collection(Constants.KEY_COLLECTION_USERS)
            .document(userId)
            .update(updates)
            .addOnSuccessListener {
                loading(false) // Hide loading indicator
                // Update the local preferences with the new profile data
                preferenceManager.putString(Constants.KEY_NAME, binding.editName.text.toString())
                preferenceManager.putString(Constants.KEY_EMAIL, binding.editEmail.text.toString())
                if (encodedImage != null) {
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage!!)
                }
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                loading(false) // Hide loading indicator on failure
                showToast("Failed to update profile: ${e.message}")
            }
    }

    // Image picker result handler
    private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Check if the image picker returned a valid result
        if (result.resultCode == RESULT_OK && result.data != null) {
            val imageUri: Uri? = result.data?.data
            try {
                // Open an input stream to read the selected image
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.ProfileImageEdit.setImageBitmap(bitmap) // Set the image in the profile view
                encodedImage = encodeImage(bitmap) // Encode the image to base64 string
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    // Encode a Bitmap image into a base64 string for storage
    private fun encodeImage(bitmap: Bitmap): String {
        // Scale down the image for preview
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)

        // Compress the image and convert it to a byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()

        // Encode the byte array to a base64 string
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    // Validate the user's profile details before updating
    private fun isValidProfileDetails(): Boolean {
        return when {
            // Check if the name field is empty
            binding.editName.text.toString().trim().isEmpty() -> {
                showToast("Enter Name") // Show error message
                false
            }
            // Check if the email field is empty
            binding.editEmail.text.toString().trim().isEmpty() -> {
                showToast("Enter Email") // Show error message
                false
            }
            // Validate the email format using a regex pattern
            !Patterns.EMAIL_ADDRESS.matcher(binding.editEmail.text.toString()).matches() -> {
                showToast("Enter Valid Email") // Show error message
                false
            }
            else -> true // If all fields are valid, return true
        }
    }

    // Show or hide the loading indicator
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            // If loading, hide the update button and show the progress bar
            binding.btnUpdateAccount.visibility = View.INVISIBLE
            binding.ProgressBar.visibility = View.VISIBLE
        } else {
            // If not loading, hide the progress bar and show the update button
            binding.ProgressBar.visibility = View.INVISIBLE
            binding.btnUpdateAccount.visibility = View.VISIBLE
        }
    }

    // Show a toast message with the given text
    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
