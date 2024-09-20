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

    private lateinit var binding: ActivityProfileBinding
    private var encodedImage: String? = null
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        userId = preferenceManager.getString(Constants.KEY_USER_ID).toString()

        // Fetch and display existing user data
        loadUserProfile()

        setListeners()
    }

    private fun setListeners() {
        binding.btnUpdateAccount.setOnClickListener {
            if (isValidProfileDetails()) {
                updateProfile()
            }
        }

        binding.ProfileImageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to load current user data from Firebase Firestore
    private fun loadUserProfile() {
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    binding.editName.setText(documentSnapshot.getString(Constants.KEY_NAME))
                    binding.editEmail.setText(documentSnapshot.getString(Constants.KEY_EMAIL))
                    val image = documentSnapshot.getString(Constants.KEY_IMAGE)
                    if (image != null && image.isNotEmpty()) {
                        val decodedImage = Base64.decode(image, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size)
                        binding.ProfileImageEdit.setImageBitmap(bitmap)
                        encodedImage = image
                    }
                }
            }
    }

    // Function to update profile data in Firebase Firestore
    private fun updateProfile() {
        loading(true)
        val database = FirebaseFirestore.getInstance()
        val updates = hashMapOf<String, Any>(
            Constants.KEY_NAME to binding.editName.text.toString(),
            Constants.KEY_EMAIL to binding.editEmail.text.toString()
        )
        if (!encodedImage.isNullOrEmpty()) {
            updates[Constants.KEY_IMAGE] = encodedImage!!
        }

        database.collection(Constants.KEY_COLLECTION_USERS)
            .document(userId)
            .update(updates)
            .addOnSuccessListener {
                loading(false)
                // Update local preferences
                preferenceManager.putString(Constants.KEY_NAME, binding.editName.text.toString())
                preferenceManager.putString(Constants.KEY_EMAIL, binding.editEmail.text.toString())
                if (encodedImage != null) {
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage!!)
                }
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                loading(false)
                showToast("Failed to update profile: ${e.message}")
            }
    }

    private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val imageUri: Uri? = result.data?.data
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.ProfileImageEdit.setImageBitmap(bitmap)
                encodedImage = encodeImage(bitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun isValidProfileDetails(): Boolean {
        return when {
            binding.editName.text.toString().trim().isEmpty() -> {
                showToast("Enter Name")
                false
            }
            binding.editEmail.text.toString().trim().isEmpty() -> {
                showToast("Enter Email")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(binding.editEmail.text.toString()).matches() -> {
                showToast("Enter Valid Email")
                false
            }
            else -> true
        }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnUpdateAccount.visibility = View.INVISIBLE
            binding.ProgressBar.visibility = View.VISIBLE
        } else {
            binding.ProgressBar.visibility = View.INVISIBLE
            binding.btnUpdateAccount.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
