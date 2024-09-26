package com.kayodedaniel.nestnews.Utilities

import android.content.Context
import android.content.SharedPreferences

// This class manages preferences (persistent storage of simple data) using SharedPreferences
class PreferenceManager(context: Context) {

    // Initializing SharedPreferences using the provided context and the name defined in Constants
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)

    // Function to store a boolean value in SharedPreferences
    fun putBoolean(key: String, value: Boolean) {
        val editor = sharedPreferences.edit() // Create an editor for SharedPreferences
        editor.putBoolean(key, value) // Store the boolean value under the specified key
        editor.apply() // Apply the changes asynchronously
    }

    // Function to retrieve a boolean value from SharedPreferences; returns the default value if the key doesn't exist
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue) // Return the stored boolean or the default value
    }

    // Function to store a string value in SharedPreferences
    fun putString(key: String, value: String) {
        val editor = sharedPreferences.edit() // Create an editor for SharedPreferences
        editor.putString(key, value) // Store the string value under the specified key
        editor.apply() // Apply the changes asynchronously
    }

    // Function to retrieve a string value from SharedPreferences; returns null if the key doesn't exist
    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null) // Return the stored string or null if the key doesn't exist
    }

    // Function to clear all data stored in SharedPreferences
    fun clear() {
        val editor = sharedPreferences.edit() // Create an editor for SharedPreferences
        editor.clear() // Clear all entries from SharedPreferences
        editor.apply() // Apply the changes asynchronously
    }

}
