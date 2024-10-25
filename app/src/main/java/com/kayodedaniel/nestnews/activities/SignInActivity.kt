package com.kayodedaniel.nestnews.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.kayodedaniel.nestnews.databinding.ActivitySignInBinding
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding // Binding for the sign-in layout
    private lateinit var preferenceManager: PreferenceManager // For managing user preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(applicationContext)
        FirebaseApp.initializeApp(this)
        // Check if the user is already signed in
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            // If signed in, go to MainActivity
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the SignInActivity
        }

        binding = ActivitySignInBinding.inflate(layoutInflater) // Inflate the sign-in layout
        setContentView(binding.root) // Set the content view
        setListeners() // Set click listeners for buttons
        val email = preferenceManager.getString(Constants.KEY_EMAIL)
        if (!email.isNullOrEmpty()) {
            binding.buttonUseFingerprint.isEnabled = true
            binding.buttonUseFingerprint.visibility = View.VISIBLE
        } else {
            binding.buttonUseFingerprint.isEnabled = false
            binding.buttonUseFingerprint.visibility = View.GONE
        }

        // Remove the automatic biometric prompt display from here
        // Initialize BiometricManager to check if biometrics are available
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Biometric is available, but don't show the prompt automatically
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                showToast("No biometric hardware available on this device.")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                showToast("Biometric hardware is currently unavailable.")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                showToast("No biometrics enrolled. Please set up fingerprint authentication.")
            }
        }
    }


    private fun showBiometricPrompt() {
        // Create a BiometricPrompt instance
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                showToast("Authentication error: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                showToast("Authentication succeeded!")
                // Now fetch user data based on fingerprint ID
                fetchUserDataByFingerprint()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                showToast("Authentication failed. Try again.")
            }
        })

        // Create a prompt info for the biometric prompt dialog
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Sign In with Fingerprint")
            .setDescription("Use your fingerprint to sign in")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        // Show the biometric prompt
        biometricPrompt.authenticate(promptInfo)
    }

    private fun fetchUserDataByFingerprint() {
        val email = preferenceManager.getString(Constants.KEY_EMAIL)
        if (email.isNullOrEmpty()) {
            // If email is not found in preferences, try to fetch it from Firestore
            val database = FirebaseFirestore.getInstance()
            database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_FINGERPRINT_ID, "fingerprint_key_$email")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val user = documents.documents[0]
                        val fetchedEmail = user.getString(Constants.KEY_EMAIL)
                        if (!fetchedEmail.isNullOrEmpty()) {
                            preferenceManager.putString(Constants.KEY_EMAIL, fetchedEmail)
                            proceedWithFingerprintSignIn(fetchedEmail)
                        } else {
                            showToast("No email associated with this fingerprint. Please sign in with email.")
                        }
                    } else {
                        showToast("No account found for this fingerprint. Please sign in with email.")
                    }
                }
                .addOnFailureListener {
                    showToast("Error fetching user data. Please sign in with email.")
                }
        } else {
            proceedWithFingerprintSignIn(email)
        }
    }

    private fun proceedWithFingerprintSignIn(email: String) {
        val fingerprintId = "fingerprint_key_$email"

        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_FINGERPRINT_ID, fingerprintId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null && !task.result.isEmpty) {
                    val documentSnapshot = task.result.documents[0]
                    saveUserData(documentSnapshot)

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    showToast("No account found for this fingerprint. Please sign in with email.")
                }
            }
    }

    private fun setListeners() {
        // Click listener for creating a new account
        binding.textCreateNewAccount.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java)) // Start SignUpActivity
        }
        binding.buttonUseFingerprint.setOnClickListener {
            showBiometricPrompt()
        }

        // Click listener for the sign-in button
        binding.buttonSignIn.setOnClickListener {
            if (isValidSignInDetails()) { // Check if sign-in details are valid
                signIn() // Call the sign-in function
            }
        }
    }

    private fun signIn() {
        loading(true)
        val database = FirebaseFirestore.getInstance()

        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD, binding.InputPassword.text.toString())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null && task.result.documents.isNotEmpty()) {
                    val documentSnapshot: DocumentSnapshot = task.result.documents[0]
                    saveUserData(documentSnapshot)
                    preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.text.toString())

                    // Generate FCM token
                    // Get the FCM token
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("FCM", "Fetching FCM token failed", task.exception)
                            return@addOnCompleteListener
                        }

                        // Get new FCM registration token
                        val token = task.result
                        Log.d("FCM Token", token ?: "Token is null")

                        // Start MainActivity after successful sign-in
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                } else {
                    loading(false)
                    showToast("Unable To Sign In")
                }
            }
    }



    private fun saveUserData(documentSnapshot: DocumentSnapshot) {
        // Save user data to preferences
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.id)
        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME) ?: "")
        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL) ?: "")
        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE) ?: "")
    }

    private fun loading(isLoading: Boolean) {
        // Show or hide loading progress based on isLoading
        if (isLoading) {
            binding.buttonSignIn.visibility = View.INVISIBLE // Hide sign-in button
            binding.ProgressBar.visibility = View.VISIBLE // Show progress bar
        } else {
            binding.ProgressBar.visibility = View.INVISIBLE // Hide progress bar
            binding.buttonSignIn.visibility = View.VISIBLE // Show sign-in button
        }
    }

    private fun showToast(message: String) {
        // Show a toast message
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidSignInDetails(): Boolean {
        // Check if all sign-in details are valid
        return when {
            binding.inputEmail.text.toString().trim().isEmpty() -> {
                showToast("Enter Email") // Show error if email is empty
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches() -> {
                showToast("Enter Valid Email") // Show error if email is not valid
                false
            }
            binding.InputPassword.text.toString().trim().isEmpty() -> {
                showToast("Enter Password") // Show error if password is empty
                false
            }
            else -> true // All details are valid
        }
    }
}
