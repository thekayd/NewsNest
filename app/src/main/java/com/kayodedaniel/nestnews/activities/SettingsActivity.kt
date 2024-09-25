package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager
import com.kayodedaniel.nestnews.databinding.ActivitySettingsBinding
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kayodedaniel.nestnews.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding for the settings activity
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize preference manager
        preferenceManager = PreferenceManager(applicationContext)

        // Ensuring that the theme is saved on start up based on the users preference
        val isDarkMode = preferenceManager.getBoolean(Constants.KEY_IS_DARK_MODE, false)
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        })

        // Load and display user details immediately when activity starts
        loadUserDetails()
        setListeners()
        initDarkModeSwitch() // Initialize Dark Mode switch

        // Set the click listener for the Edit Profile text view
        binding.textViewEditProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Terms and Conditions Click
        binding.textTermsAndConditions.setOnClickListener {
            val intent = Intent(this, TermsAndConditions::class.java)
            startActivity(intent)
        }

        // Bottom navigation listener
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
                R.id.navigation_message -> {
                    val intent = Intent(this, MessageHomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadUserDetails() {
        // Display the user's name and email
        binding.textName.text = preferenceManager.getString(Constants.KEY_NAME)
        binding.textEmailAdress.text = preferenceManager.getString(Constants.KEY_EMAIL)

        // Decode and display user's profile image (if it exists)
        val encodedImage = preferenceManager.getString(Constants.KEY_IMAGE)
        if (encodedImage != null) {
            val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            binding.imageProfile.setImageBitmap(bitmap)
        }
    }

    private fun setListeners() {
        // Logout listener
        binding.textViewLogout.setOnClickListener { signOut() }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun signOut() {
        showToast("Signing out...")
        val database = FirebaseFirestore.getInstance()
        val documentReference: DocumentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferenceManager.getString(Constants.KEY_USER_ID)!!)
        val updates = HashMap<String, Any>()
        updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
        documentReference.update(updates).addOnSuccessListener {
            preferenceManager.clear()
            startActivity(Intent(applicationContext, SignInActivity::class.java))
            finish()
        }.addOnFailureListener {
            showToast("Unable to sign out")
        }
    }

    private fun initDarkModeSwitch() {
        // Set the switch state based on the saved preference
        val isDarkMode = preferenceManager.getBoolean(Constants.KEY_IS_DARK_MODE, false)
        binding.DMSwitch.isChecked = isDarkMode

        // Listen for switch toggle to change the theme
        binding.DMSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Update the theme preference
            preferenceManager.putBoolean(Constants.KEY_IS_DARK_MODE, isChecked)

            // Set the night mode based on switch state without recreating the activity
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // Optionally, you can notify the user about the change
            showToast("Theme changed to ${if (isChecked) "Dark" else "Light"} mode")
        }
    }

}
