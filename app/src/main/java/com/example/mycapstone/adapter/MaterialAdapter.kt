package com.example.mycapstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        val button: TextView = view.findViewById(R.id.status_text)
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

        holder.button.text = if (item.completed == true) {
            holder.button.context.getString(R.string.mark_as_completed)
        } else {
            holder.button.context.getString(R.string.mark_as_not_completed)
        }

        holder.button.setOnClickListener {
            onClick(item)
        }

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
