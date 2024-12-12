package com.example.mycapstone.ui.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycapstone.api.ApiConfig
import com.example.mycapstone.api.ApiService
import com.example.mycapstone.data.QuizResponseItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class QuizViewModel : ViewModel() {

    private val _quizItems = MutableLiveData<List<QuizResponseItem>>()
    val quizItems: LiveData<List<QuizResponseItem>> get() = _quizItems

    private val _completedQuizIds = MutableLiveData<List<String>>()
    val completedQuizIds: LiveData<List<String>> get() = _completedQuizIds

    private val database = FirebaseFirestore.getInstance()

    fun fetchCompletedQuizzes() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = database.collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { document ->
                val completedQuizzes = document.get("completedQuizzes") as? List<String> ?: emptyList()
                _completedQuizIds.postValue(completedQuizzes)
                fetchQuizzes()
            }.addOnFailureListener {
                fetchQuizzes()
            }
        } else {
            fetchQuizzes()
        }
    }

    private fun fetchQuizzes() {
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.createService(ApiService::class.java)
                val quizzes = withContext(Dispatchers.IO) { apiService.getquizzes() }
                _quizItems.postValue(quizzes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}