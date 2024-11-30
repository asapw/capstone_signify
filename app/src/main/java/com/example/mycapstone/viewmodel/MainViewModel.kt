package com.example.mycapstone.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mycapstone.ui.login.manager.SessionManager
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    companion object {
        private const val TAG = "MainViewModel"
    }

    // Function to fetch the username using the userId from the SessionManager
    fun fetchUserName() {
        // Retrieve the userId from SessionManager
        val userId = sessionManager.getUserId()

        if (userId != null) {
            // Fetch user data from Firestore using the userId
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        _userName.value = document.getString("name") ?: "User"
                    } else {
                        Log.d(TAG, "No user document found.")
                        _userName.value = "User"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching user data: ", exception)
                    _userName.value = "User"
                }
        } else {
            _userName.value = "User"  // Default value if no userId is found
        }
    }
}
