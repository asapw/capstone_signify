package com.example.mycapstone.ui.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycapstone.BuildConfig
import com.example.mycapstone.api.NewsConfig
import com.example.mycapstone.data.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _profileImageUrl = MutableLiveData<String>()
    val profileImageUrl: LiveData<String> get() = _profileImageUrl

    private val _lessonProgress = MutableLiveData<Int>()
    val lessonProgress: LiveData<Int> get() = _lessonProgress

    private val _quizProgress = MutableLiveData<Int>()
    val quizProgress: LiveData<Int> get() = _quizProgress

    private val _news = MutableLiveData<List<Article>>()
    val news: LiveData<List<Article>> = _news

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val totalLessons = 4 // Replace with actual count
    private val totalQuizzes = 4 // Replace with actual count

    fun fetchUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        _username.value = document.getString("name") ?: "User"
                        _email.value = document.getString("email") ?: "example@example.com"
                        _profileImageUrl.value = document.getString("profileImageUrl") ?: ""
                    } else {
                        _username.value = "User"
                        _email.value = "example@example.com"
                        _profileImageUrl.value = ""
                    }
                }
                .addOnFailureListener {
                    _username.value = "User"
                    _email.value = "example@example.com"
                    _profileImageUrl.value = ""
                }
        } else {
            _username.value = "User"
            _email.value = "example@example.com"
            _profileImageUrl.value = ""
        }
    }

    fun fetchUserProgress() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val completedLessons = (document.get("completedLessons") as? List<String>) ?: emptyList()
                        val completedLessonCount = completedLessons.size
                        _lessonProgress.value = if (totalLessons > 0) {
                            (completedLessonCount * 100) / totalLessons
                        } else {
                            0
                        }

                        val completedQuizzes = (document.get("completedQuizzes") as? List<String>) ?: emptyList()
                        val completedQuizCount = completedQuizzes.size
                        _quizProgress.value = if (totalQuizzes > 0) {
                            (completedQuizCount * 100) / totalQuizzes
                        } else {
                            0
                        }
                    }
                }
                .addOnFailureListener {
                    _lessonProgress.value = 0
                    _quizProgress.value = 0
                }
        } else {
            _lessonProgress.value = 0
            _quizProgress.value = 0
        }
    }


    fun fetchNews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = NewsConfig.getModelService().getNews("hand sign", BuildConfig.NEWS_API_KEY)
                _news.value = response.articles // Assuming your API returns articles
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching news: ${e.message}")
            }
        }
    }
}