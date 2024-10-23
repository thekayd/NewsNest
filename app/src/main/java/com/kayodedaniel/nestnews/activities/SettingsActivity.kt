package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.content.res.Configuration
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
import com.kayodedaniel.nestnews.chatbotactivity.ChatBotActivity
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferenceManager: PreferenceManager
    private var currentMode: Int = AppCompatDelegate.MODE_NIGHT_NO // Default to Light mode
    private var isAfrikaans: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding for the settings activity
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize preference manager
        preferenceManager = PreferenceManager(applicationContext)

        // Load the saved language preference
        isAfrikaans = preferenceManager.getBoolean(Constants.KEY_IS_AFRIKAANS, false)
        changeLanguage(isAfrikaans) // Set the language at the start

        // Get the current theme preference and apply it
        currentMode = if (preferenceManager.getBoolean(Constants.KEY_IS_DARK_MODE, false)) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(currentMode)

        // Load and display user details immediately when activity starts
        loadUserDetails()
        setListeners()
        initDarkModeSwitch()
        initLanguageSwitch()

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
                R.id.nav_chat_bot -> {
                    val intent = Intent(this, ChatBotActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
    private fun initLanguageSwitch() {
        // Set the switch state based on the saved preference
        isAfrikaans = preferenceManager.getBoolean(Constants.KEY_IS_AFRIKAANS, false)
        binding.languageSwitch.isChecked = isAfrikaans

        // Listen for switch toggle to change the language
        binding.languageSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != isAfrikaans) {
                isAfrikaans = isChecked
                // Update the language preference
                preferenceManager.putBoolean(Constants.KEY_IS_AFRIKAANS, isAfrikaans)

                // Apply language change without recreating immediately
                changeLanguageWithoutRecreate(isAfrikaans)
            } else {
                showToast("Language is already ${if (isChecked) "Afrikaans" else "English"}")
            }
        }
    }

    private fun changeLanguageWithoutRecreate(isAfrikaans: Boolean) {
        val locale = if (isAfrikaans) Locale("af") else Locale("en")
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Instead of immediate recreation, allow user to see the changes after a smooth activity restart
        recreateActivityForThemeChange()
    }
    private fun changeLanguage(isAfrikaans: Boolean) {
        val locale = if (isAfrikaans) Locale("af") else Locale("en")
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
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
        // Prevent multiple queued toasts by adding a limit check or using a different method
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
            // Save the email before clearing preferences
            val email = preferenceManager.getString(Constants.KEY_EMAIL)

            // Clear all preferences
            preferenceManager.clear()

            // Restore the email
            if (!email.isNullOrEmpty()) {
                preferenceManager.putString(Constants.KEY_EMAIL, email)
            }

            startActivity(Intent(applicationContext, SignInActivity::class.java))
            finish()
        }.addOnFailureListener {
            showToast("Unable to sign out")
        }
    }

    private fun initDarkModeSwitch() {
        // Set the switch state based on the saved preference
        val isDarkMode = currentMode == AppCompatDelegate.MODE_NIGHT_YES
        binding.DMSwitch.isChecked = isDarkMode

        // Listen for switch toggle to change the theme
        binding.DMSwitch.setOnCheckedChangeListener { _, isChecked ->
            val newMode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            if (newMode != currentMode) {
                currentMode = newMode
                // Update the theme preference
                preferenceManager.putBoolean(Constants.KEY_IS_DARK_MODE, isChecked)

                // Restart the activity to apply the new theme
                recreateActivityForThemeChange()
            } else {
                showToast("Theme is already ${if (isChecked) "Dark" else "Light"}")
            }
        }
    }

    private fun recreateActivityForThemeChange() {
        // Save the current language setting
        val isAfrikaans = preferenceManager.getBoolean(Constants.KEY_IS_AFRIKAANS, false)

        // Save the current dark mode setting
        val isDarkMode = preferenceManager.getBoolean(Constants.KEY_IS_DARK_MODE, false)

        // Set language based on saved preference
        val locale = if (isAfrikaans) Locale("af") else Locale("en")
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Set dark mode based on saved preference
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        // Recreate the activity with a smooth transition
        finish()
        startActivity(Intent(this, SettingsActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
