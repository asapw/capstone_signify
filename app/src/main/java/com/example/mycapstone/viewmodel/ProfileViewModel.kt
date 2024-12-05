package com.example.mycapstone.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mycapstone.ui.login.manager.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _profileData = MutableLiveData<ProfileData>()
    val profileData: LiveData<ProfileData> get() = _profileData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchUserData() {
        _isLoading.value = true
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val email = currentUser.email ?: ""  // Get email from FirebaseAuth

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    _isLoading.value = false
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "User"
                        val profileImageUrl = document.getString("profileImageUrl") ?: ""
                        _profileData.value = ProfileData(name, profileImageUrl, email)
                    } else {
                        Log.e("ProfileViewModel", "No user data found")
                        _profileData.value = ProfileData("User", "", email)
                    }
                }
                .addOnFailureListener { exception ->
                    _isLoading.value = false
                    Log.e("ProfileViewModel", "Error fetching user data", exception)
                    _profileData.value = ProfileData("User", "", email)
                }
        } else {
            Log.e("ProfileViewModel", "No authenticated user found")
            _isLoading.value = false
            _profileData.value = ProfileData("User", "", "")
        }
    }

    fun signOut(sessionManager: SessionManager) {
        FirebaseAuth.getInstance().signOut()
        sessionManager.clearSession()
    }
}

data class ProfileData(
    val name: String,
    val profileImageUrl: String,
    val email: String
)
