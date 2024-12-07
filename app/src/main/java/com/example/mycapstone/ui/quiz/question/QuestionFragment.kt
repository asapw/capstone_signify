package com.example.mycapstone.ui.quiz.question

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mycapstone.databinding.FragmentQuestionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    private var correctOption: String? = null
    private var quizId: String? = null
    private var checkQuestion: Boolean? = null

    private val database = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)

        // Retrieve arguments using Safe Args
        val args = QuestionFragmentArgs.fromBundle(requireArguments())

        // Bind the question and options (A, B, C, D) to the UI
        binding.questionTitle.text = args.question
        binding.optionA.text = args.optionA
        binding.optionB.text = args.optionB
        binding.optionC.text = args.optionC
        binding.optionD.text = args.optionD

        // Set the correct answer, quizId, and checkQuestion
        correctOption = args.correctOption
        quizId = args.quizId  // Now quizId is passed as an argument
        checkQuestion = args.checkQuestion // Now checkQuestion is passed as an argument

        // Handle Submit Button Click
        binding.submitButton.setOnClickListener {
            val selectedId = binding.optionGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedOption = binding.root.findViewById<RadioButton>(selectedId).text.toString()
                if (selectedOption == correctOption) {
                    Toast.makeText(requireContext(), "Correct!", Toast.LENGTH_SHORT).show()

                    // Send quiz completion info to Firebase if it's not already marked as completed
                    if (checkQuestion == false) {
                        markQuizAsCompleted()
                    }

                    // Delay before navigating back to the QuizFragment
                    Handler().postDelayed({
                        findNavController().navigateUp()  // Navigate back to the QuizFragment
                    }, 1000)  // 1 second delay before navigating back
                } else {
                    Toast.makeText(requireContext(), "Wrong Answer!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun markQuizAsCompleted() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = database.collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    // Get completed quizzes list and ensure it's a mutable list
                    val completedQuizzes = (document.get("completedQuizzes") as? List<*>)?.toMutableList() ?: mutableListOf()

                    // If quiz is not already completed
                    if (!completedQuizzes.contains(quizId)) {
                        completedQuizzes.add(quizId!!)  // Add current quizId to the list

                        // Update completedQuizzes field in Firestore
                        userDocRef.update("completedQuizzes", completedQuizzes)
                            .addOnSuccessListener {
                                Log.d("Quiz", "Quiz successfully marked as completed.")
                                Toast.makeText(requireContext(), "Quiz marked as completed", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Quiz", "Failed to update completed quizzes", exception)
                                Toast.makeText(requireContext(), "Failed to mark quiz", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Log.d("Quiz", "Quiz already completed.")
                    }
                } else {
                    Log.e("Quiz", "User document not found.")
                }
            }
                .addOnFailureListener { exception ->
                    Log.e("Quiz", "Failed to fetch user document", exception)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
