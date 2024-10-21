package com.kayodedaniel.nestnews.activities


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.kayodedaniel.nestnews.databinding.ActivitySignUpBinding
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import javax.crypto.KeyGenerator


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding //Binding for the layout
    private var encodedImage: String? = null // Varaible to hold the encoded image
    private lateinit var preferenceManager: PreferenceManager // For managing preferences
    private lateinit var biometricPrompt: BiometricPrompt // Biometric Prompt
    private lateinit var biometricManager: BiometricManager // Biometric Manager
    private lateinit var executor: Executor // For handling biometric tasks
    private var fingerprintId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext) //Initilize preferences
        setListeners() //Set listeners for buttons and clicks
        initializeBiometric()
    }

    private fun setListeners() {
        //Go back to previous screen when text is clicked
        binding.textSignIn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        //Sign u button click listener
        binding.buttonSignUp.setOnClickListener {
            if (isValidSignUpDetails()) { //If details are valid call the sign  up function
                signUp()
            }
        }
        binding.layoutImage.setOnClickListener {//click listener for image
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)//grant permission
            pickImage.launch(intent)
        }
        // Enable fingerprint authentication
        binding.buttonFingerprintAuth.setOnClickListener {
            authenticateFingerprint()
        }
    }
    private fun initializeBiometric() {
        biometricManager = BiometricManager.from(this)
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                showToast("Authentication Error: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                showToast("Authentication Succeeded")
                signUpWithFingerprint() // Proceed to sign up with fingerprint authentication
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                showToast("Authentication Failed")
            }
        })
    }
    private fun authenticateFingerprint() {
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt.authenticate(createBiometricPromptInfo())
        } else {
            showToast("Fingerprint authentication is not available")
        }
    }

    private fun createBiometricPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Fingerprint Authentication")
            .setSubtitle("Authenticate using your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()
    }

    private fun signUpWithFingerprint() {
        if (isValidSignUpDetails()) {
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                "fingerprint_key_${binding.inputEmail.text}",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .build()

            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()

            fingerprintId = "fingerprint_key_${binding.inputEmail.text}"

            signUp(fingerprintId)
        }
    }
    //show toast message
    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun signUp(fingerprintId: String? = null) {
        loading(true) // Show loading progress
        val database = FirebaseFirestore.getInstance() // Get Firestore instance
        val user = HashMap<String, Any>() // Create a user map to store data
        user[Constants.KEY_NAME] = binding.inputName.text.toString() // Get name
        user[Constants.KEY_EMAIL] = binding.inputEmail.text.toString() // Get email
        user[Constants.KEY_PASSWORD] = binding.InputPassword.text.toString() // Get password
        user[Constants.KEY_IMAGE] = encodedImage ?: "" // Get encoded image
        user[Constants.KEY_FINGERPRINT_ID] = fingerprintId ?: "" // Store fingerprint ID

        database.collection(Constants.KEY_COLLECTION_USERS) // Add user to Firestore database
            .add(user)
            .addOnSuccessListener { documentReference ->
                loading(false) // Hide loading progress
                // Save user data to preferences
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                preferenceManager.putString(Constants.KEY_USER_ID, documentReference.id)
                preferenceManager.putString(Constants.KEY_NAME, binding.inputName.text.toString())
                preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
                preferenceManager.putString(Constants.KEY_IMAGE, encodedImage ?: "")
                // Start main activity after successful sign up
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                loading(false)
                showToast(exception.message ?: "An error occurred") // Show error message
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
    //activity result launcher for picking images
    private val pickImage: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) { // Check if result is OK
            val imageUri: Uri? = result.data?.data // Get the image URI
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!) // Open input stream
                val bitmap = BitmapFactory.decodeStream(inputStream) // Decode the stream to bitmap
                binding.imageProfile.setImageBitmap(bitmap) // Set the bitmap to ImageView
                binding.textAddImage.visibility = View.GONE // Hide add image text
                encodedImage = encodeImage(bitmap) // Encode the image
            } catch (e: FileNotFoundException) {
                e.printStackTrace() // Print stack trace for errors
            }
        }
    }
    //check if all sign up details are valid and if not return error message
    private fun isValidSignUpDetails(): Boolean {
        return when {
            encodedImage == null -> {
                showToast("Select Profile Image")
                false
            }
            binding.inputName.text.toString().trim().isEmpty() -> {
                showToast("Enter Name")
                false
            }
            binding.inputEmail.text.toString().trim().isEmpty() -> {
                showToast("Enter Email")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches() -> {
                showToast("Enter Valid Email")
                false
            }
            binding.InputPassword.text.toString().trim().isEmpty() -> {
                showToast("Enter Password")
                false
            }
            binding.InputConfirmPassword.text.toString().trim().isEmpty() -> {
                showToast("Confirm Your Password")
                false
            }
            binding.InputPassword.text.toString() != binding.InputConfirmPassword.text.toString() -> {
                showToast("Password & Confirm Password Must Be The Same")
                false
            }
            else -> true
        }
    }
    //show or hide loading progress based on isLoading
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.buttonSignUp.visibility = View.INVISIBLE
            binding.ProgressBar.visibility = View.VISIBLE
        } else {
            binding.ProgressBar.visibility = View.INVISIBLE
            binding.buttonSignUp.visibility = View.VISIBLE
        }
    }
}
