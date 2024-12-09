package com.example.mycapstone.ui.quiz.question

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mycapstone.R
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
        quizId = args.quizId
        checkQuestion = args.checkQuestion

        // Handle Submit Button Click
        binding.submitButton.setOnClickListener {
            val selectedId = binding.optionGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedOption = binding.root.findViewById<RadioButton>(selectedId).text.toString()
                if (selectedOption == correctOption) {
                    showCorrectIndicator()
                    if (checkQuestion == false) {
                        markQuizAsCompleted()
                    }
                    Handler().postDelayed({
                        findNavController().navigateUp()
                    }, 2000) // 2 seconds delay
                } else {
                    showIncorrectIndicator()
                    Toast.makeText(requireContext(), "Wrong Answer!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun showCorrectIndicator() {
        // Show the green "Correct!" text
        binding.correctIndicator.visibility = View.VISIBLE

        // Hide the incorrect indicator if it's visible
        binding.incorrectIndicator.visibility = View.GONE

        // Play the "clink" sound
        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.clink)
        mediaPlayer.start()

        // Trigger vibration for correct answer
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            val vibrationEffect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)
        }
    }

    private fun showIncorrectIndicator() {
        // Show the red "Incorrect!" text
        binding.incorrectIndicator.visibility = View.VISIBLE

        // Hide the correct indicator if it's visible
        binding.correctIndicator.visibility = View.GONE

        // Play the "wrong" sound
        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.wrong) // Ensure "wrong.mp3" is in res/raw
        mediaPlayer.start()

        // Trigger vibration for incorrect answer (different pattern or effect)
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            val vibrationEffect = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)
        }
    }

    private fun markQuizAsCompleted() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = database.collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val completedQuizzes = (document.get("completedQuizzes") as? List<*>)?.toMutableList() ?: mutableListOf()
                    if (!completedQuizzes.contains(quizId)) {
                        completedQuizzes.add(quizId!!)
                        userDocRef.update("completedQuizzes", completedQuizzes)
                            .addOnSuccessListener {
                                Log.d("Quiz", "Quiz successfully marked as completed.")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Quiz", "Failed to update completed quizzes", exception)
                                Toast.makeText(requireContext(), "Failed to mark quiz", Toast.LENGTH_SHORT).show()
                            }
                    }
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
