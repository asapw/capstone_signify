package com.example.mycapstone.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycapstone.R
import com.example.mycapstone.adapter.QuizAdapter
import com.example.mycapstone.data.QuizResponseItem


class QuizFragment : Fragment() {

    private lateinit var viewModel: QuizViewModel
    private lateinit var quizList: RecyclerView
    private lateinit var adapter: QuizAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)

        viewModel = ViewModelProvider(this)[QuizViewModel::class.java]

        quizList = view.findViewById(R.id.quiz_list)
        quizList.layoutManager = LinearLayoutManager(requireContext())
        adapter = QuizAdapter(emptyList(), emptyList()) { quizItem ->
            navigateToQuestionFragment(quizItem)
        }
        quizList.adapter = adapter

        setupObservers()
        viewModel.fetchCompletedQuizzes()

        return view
    }

    private fun setupObservers() {
        viewModel.quizItems.observe(viewLifecycleOwner) { quizzes ->
            adapter.updateQuizzes(quizzes, viewModel.completedQuizIds.value ?: emptyList())
        }

        viewModel.completedQuizIds.observe(viewLifecycleOwner) { completedIds ->
            adapter.updateQuizzes(viewModel.quizItems.value ?: emptyList(), completedIds)
        }
    }

    private fun navigateToQuestionFragment(quizItem: QuizResponseItem) {
        val correctOption = when (quizItem.trueOption) {
            "aOption" -> quizItem.aOption
            "bOption" -> quizItem.bOption
            "cOption" -> quizItem.cOption
            "dOption" -> quizItem.dOption
            else -> null
        }

        if (correctOption != null) {
            val action = QuizFragmentDirections.actionQuizFragmentToQuestionFragment(
                quizId = quizItem.id ?: "unknown",
                checkQuestion = quizItem.checkQuestion ?: false,
                question = quizItem.qustion ?: "Default Question",
                optionA = quizItem.aOption ?: "Option A",
                optionB = quizItem.bOption ?: "Option B",
                optionC = quizItem.cOption ?: "Option C",
                optionD = quizItem.dOption ?: "Option D",
                correctOption = correctOption,
                imageQuestion = quizItem.imageQuestion ?: ""
            )
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "Quiz data is incomplete.", Toast.LENGTH_SHORT).show()
        }
    }

}
