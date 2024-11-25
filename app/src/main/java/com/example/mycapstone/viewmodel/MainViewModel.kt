package com.example.mycapstone.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    companion object {
        private const val TAG = "MainViewModel"
    }

    fun fetchUserName() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
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
            _userName.value = "User"
        }
    }
}
