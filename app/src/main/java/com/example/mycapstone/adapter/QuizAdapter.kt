package com.example.mycapstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mycapstone.R
import com.example.mycapstone.data.QuizResponseItem

class QuizAdapter(
    private var quizItems: List<QuizResponseItem>,
    private var completedQuizzes: List<String>,
    private val onItemClick: (QuizResponseItem) -> Unit
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val quizTitle: TextView = itemView.findViewById(R.id.quiz_title)
        val quizIcon: ImageView = itemView.findViewById(R.id.quiz_icon)

        fun bind(quizItem: QuizResponseItem) {
            quizTitle.text = quizItem.title ?: "Untitled Quiz"
            val isCompleted = completedQuizzes.contains(quizItem.id) // Check if quiz is completed

            if (isCompleted) {
                // Change background and show as completed
                itemView.setBackgroundResource(R.drawable.completed_quiz_background) // Use custom background for completed quizzes
                quizIcon.setImageResource(R.drawable.ic_done) // Placeholder for a completed icon
                itemView.alpha = 0.5f // Dim for completed state

                // Set click listener for completed quizzes
                itemView.setOnClickListener {
                    Toast.makeText(
                        itemView.context,
                        "This quiz has already been completed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Default behavior for non-completed quizzes
                itemView.setBackgroundResource(R.drawable.rounded_black_background) // Default background
                quizIcon.setImageResource(R.drawable.ic_play) // Icon for uncompleted quizzes
                itemView.alpha = 1.0f // Normal visibility

                // Set click listener for non-completed quizzes
                itemView.setOnClickListener {
                    onItemClick(quizItem) // Trigger the click callback
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(quizItems[position])
    }

    override fun getItemCount(): Int = quizItems.size

    fun updateQuizzes(newQuizzes: List<QuizResponseItem>, completedQuizIds: List<String>) {
        quizItems = newQuizzes
        completedQuizzes = completedQuizIds
        notifyDataSetChanged()
    }
}
