package com.example.mycapstone.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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

        holder.title.text = item.title
        holder.subtitle.text = item.subTitle

        Glide.with(holder.image.context).load(item.photoUrl).into(holder.image)

        val isCompleted = item.isCompleted

        Log.d("MaterialAdapter", "Item: ${item.title}, Completed: $isCompleted")

        holder.statusText.text = if (isCompleted) {
            holder.itemView.context.getString(R.string.mark_as_completed)
        } else {
            holder.itemView.context.getString(R.string.mark_as_not_completed)
        }

        holder.statusText.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (isCompleted) R.color.primaryColor else R.color.red
            )
        )

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<LessonResponseItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
