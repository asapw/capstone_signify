package com.example.mycapstone.ui.learn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycapstone.R
import com.example.mycapstone.adapter.QuizAdapter

class LearnFragment : Fragment() {

    private lateinit var quizList: RecyclerView
    private val quizItems = listOf(
        "Guess the letter",
        "Mimicking movements",
        "Word order",
        "Choose the right movement",
        "Complete the phrase",
        "Motion error detection",
        "Translate words to movements",
        "Match letters with gestures",
        "Speed test - guess the words fast",
        "Final quiz"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_learn, container, false)
        quizList = view.findViewById(R.id.quiz_list)
        quizList.layoutManager = LinearLayoutManager(requireContext())
        quizList.adapter = QuizAdapter(quizItems)
        return view
    }
}
