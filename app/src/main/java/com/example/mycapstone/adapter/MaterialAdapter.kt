package com.example.mycapstone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mycapstone.R
import com.example.mycapstone.data.LessonResponseItem

class MaterialAdapter(
    private val items: MutableList<LessonResponseItem>,
    private val onClick: (LessonResponseItem) -> Unit
) : RecyclerView.Adapter<MaterialAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.item_title)
        val subtitle: TextView = view.findViewById(R.id.item_subtitle)
        val image: ImageView = view.findViewById(R.id.item_image)
        val statusText: TextView = view.findViewById(R.id.status_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_material, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Set title and subtitle
        holder.title.text = item.title
        holder.subtitle.text = item.subTitle

        // Load image with Glide
        Glide.with(holder.image.context).load(item.photoUrl).into(holder.image)

        // Check completion status from SharedPreferences (or item.completed flag)
        val sharedPreferences = holder.itemView.context.getSharedPreferences("VideoCompletionPrefs", Context.MODE_PRIVATE)
        val isCompleted = sharedPreferences.getBoolean(item.ytUrl, false)

        // Update status text based on completion status
        if (isCompleted) {
            holder.statusText.text = holder.itemView.context.getString(R.string.mark_as_completed)
            holder.statusText.setTextColor(holder.itemView.context.getColor(R.color.primaryColor)) // Completed color
        } else {
            holder.statusText.text = holder.itemView.context.getString(R.string.mark_as_not_completed)
            holder.statusText.setTextColor(holder.itemView.context.getColor(R.color.red)) // Not completed color
        }

        // Set click listeners
        holder.itemView.setOnClickListener {
            onClick(item)
        }

        holder.statusText.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount() = items.size

    // Update the adapter data and notify the change
    fun updateItems(newItems: List<LessonResponseItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
