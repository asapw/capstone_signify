package com.example.mycapstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycapstone.R
import com.example.mycapstone.data.QuizResponseItem

class QuizAdapter(
    private val quizItems: List<QuizResponseItem>,
    private val onItemClick: (QuizResponseItem) -> Unit
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val quizTitle: TextView = itemView.findViewById(R.id.quiz_title)
        val quizIcon: ImageView = itemView.findViewById(R.id.quiz_icon)

        fun bind(quizItem: QuizResponseItem) {
            quizTitle.text = quizItem.title ?: "Untitled Quiz"
            quizIcon.setImageResource(R.drawable.ic_play) // Placeholder icon

            // Set click listener for the item
            itemView.setOnClickListener {
                onItemClick(quizItem)
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
}
