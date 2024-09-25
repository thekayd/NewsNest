package com.kayodedaniel.nestnews.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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
            // Save user acceptance to shared preferences or database
            // Then redirect to the next activity or previous screen
            finish()  // This can be changed to navigate to another activity
        }
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
                stringBuilder.append("\n") // Add a newline after each line
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }
}