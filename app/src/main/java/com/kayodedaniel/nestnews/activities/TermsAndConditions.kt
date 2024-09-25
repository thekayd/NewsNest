package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kayodedaniel.nestnews.R
import java.io.BufferedReader
import java.io.InputStreamReader


class TermsAndConditions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.terms_condtions)

        val termsTextView = findViewById<TextView>(R.id.text_terms_conditions)

        // Read the file from res/raw
        val termsText: String = readTextFileFromRawResource(R.raw.terms_conditions)
        termsTextView.text = termsText

        val acceptButton = findViewById<Button>(R.id.button_accept_terms)
        acceptButton.setOnClickListener {
            showToast("T&C's Accepted")
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
    // Function to read the file from the raw resource
    private fun readTextFileFromRawResource(resourceId: Int): String {
        val inputStream = resources.openRawResource(resourceId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        try {
            while ((reader.readLine().also { line = it }) != null) {
                stringBuilder.append(line)
                stringBuilder.append("\n")
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }
}