package com.example.mycapstone.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycapstone.R
import com.example.mycapstone.adapter.QuizAdapter
import com.example.mycapstone.api.ApiConfig
import com.example.mycapstone.api.ApiService
import com.example.mycapstone.data.QuizResponseItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizFragment : Fragment() {

    private lateinit var quizList: RecyclerView
    private lateinit var adapter: QuizAdapter
    private val quizItems = mutableListOf<QuizResponseItem>()
    private val completedQuizIds = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)

        // Initialize RecyclerView
        quizList = view.findViewById(R.id.quiz_list)
        quizList.layoutManager = LinearLayoutManager(requireContext())
        adapter = QuizAdapter(quizItems, completedQuizIds) { quizItem ->
            // Navigate to QuestionFragment with quiz details
            navigateToQuestionFragment(quizItem)
        }
        quizList.adapter = adapter

        // Fetch completed quizzes and quizzes from API
        fetchCompletedQuizzes()

        return view
    }

    private fun fetchCompletedQuizzes() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)

            userDocRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val completedQuizzes = document.get("completedQuizzes") as? List<String> ?: emptyList()
                    completedQuizIds.clear()
                    completedQuizIds.addAll(completedQuizzes)

                    // Once completed quizzes are fetched, fetch quizzes from the API
                    fetchQuizzes()
                } else {
                    Toast.makeText(requireContext(), "No user data found", Toast.LENGTH_SHORT).show()
                    fetchQuizzes() // Fallback: Fetch quizzes even if user data is unavailable
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
                fetchQuizzes() // Fallback
            }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            fetchQuizzes() // Fallback
        }
    }

    private fun fetchQuizzes() {
        lifecycleScope.launch {
            try {
                // Create API service instance
                val apiService = ApiConfig.createService(ApiService::class.java)

                // Fetch quizzes in the background
                val quizzes = withContext(Dispatchers.IO) { apiService.getquizzes() }

                // Update UI with fetched quiz items
                quizItems.clear()
                quizItems.addAll(quizzes)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                // Handle errors gracefully
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to load quizzes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToQuestionFragment(quizItem: QuizResponseItem) {
        val correctOption = when (quizItem.trueOption) {
            "aOption" -> quizItem.aOption
            "bOption" -> quizItem.bOption
            "cOption" -> quizItem.cOption
            "dOption" -> quizItem.dOption
            else -> "Option A" // Default if somehow incorrect data
        }

        val action = QuizFragmentDirections
            .actionQuizFragmentToQuestionFragment(
                quizItem.id ?: "unknown",
                quizItem.checkQuestion ?: false,
                quizItem.qustion ?: "Default Question",
                quizItem.aOption ?: "Option A",
                quizItem.bOption ?: "Option B",
                quizItem.cOption ?: "Option C",
                quizItem.dOption ?: "Option D",
                correctOption ?: "Option A",  // Passing the correct answer
                quizItem.imageQuestion ?: "" // Provide a default value if null
            )

        findNavController().navigate(action)
    }




}
