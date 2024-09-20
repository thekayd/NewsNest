package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager
import com.kayodedaniel.nestnews.databinding.ActivitySettingsBinding
import android.util.Base64

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

        // Load and display user details immediately when activity starts
        loadUserDetails()

        // Set the click listener for the Edit Profile text view
        binding.textViewEditProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
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
}
