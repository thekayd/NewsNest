package com.kayodedaniel.nestnews.activities

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ActivityScenario
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityUITest {

    @Test
    fun testActivityLaunch() {
        // Launch the MainActivity using ActivityScenario
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Ensure the activity is not null
                assert(activity != null)
            }
        }
    }
}
