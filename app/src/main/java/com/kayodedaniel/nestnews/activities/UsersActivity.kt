package com.kayodedaniel.nestnews.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.kayodedaniel.nestnews.adapters.UsersAdapter
import com.kayodedaniel.nestnews.databinding.ActivityUsersBinding
import com.kayodedaniel.nestnews.models.User
import com.kayodedaniel.nestnews.Utilities.Constants
import com.kayodedaniel.nestnews.Utilities.PreferenceManager

class UsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        setListeners()
        getUsers()
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun getUsers() {
        loading(true)
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener { task ->
                loading(false)
                val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
                if (task.isSuccessful && task.result != null) {
                    val users: MutableList<User> = ArrayList()
                    for (queryDocumentSnapshot: QueryDocumentSnapshot in task.result) {
                        if (currentUserId == queryDocumentSnapshot.id) {
                            continue
                        }
                        val user = User()
                        user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME)
                        user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL)
                        user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE)
                        user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN)
                        users.add(user)
                    }
                    if (users.size > 0) {
                        val usersAdapter = UsersAdapter(users)
                        binding.usersRecyclerView.adapter = usersAdapter
                        binding.usersRecyclerView.visibility = View.VISIBLE
                    } else {
                        showErrorMessage()
                    }
                } else {
                    showErrorMessage()
                }
            }
    }

    private fun showErrorMessage() {
        binding.textErrorMessage.text = String.format("%s", "No User Available")
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}
