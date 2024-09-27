package com.kayodedaniel.nestnews.activities

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var preferenceManager: PreferenceManager

    @Before
    fun setup() {
        // Initialize the PreferenceManager
        preferenceManager = PreferenceManager(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun testDarkModePreference() {
        // Set dark mode preference to true
        preferenceManager.putBoolean(Constants.KEY_IS_DARK_MODE, true)

        // Retrieve and assert the preference
        val isDarkMode = preferenceManager.getBoolean(Constants.KEY_IS_DARK_MODE, false)
        assertEquals(true, isDarkMode)
    }
}
