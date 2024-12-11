package com.example.mycapstone.ui.quiz.question

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuestionViewModel : ViewModel() {

    private val database = FirebaseFirestore.getInstance()

    fun markQuizAsCompleted(quizId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = database.collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { document ->
                val completedQuizzes = (document.get("completedQuizzes") as? List<*>)?.toMutableList() ?: mutableListOf()
                if (!completedQuizzes.contains(quizId)) {
                    completedQuizzes.add(quizId)
                    userDocRef.update("completedQuizzes", completedQuizzes)
                }
            }
        }
    }
}
