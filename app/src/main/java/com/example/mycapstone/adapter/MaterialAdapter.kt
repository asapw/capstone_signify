package com.example.mycapstone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycapstone.R
import com.example.mycapstone.data.MaterialItem


class MaterialAdapter(
    private val items: List<MaterialItem>,
    private val onItemClick: (MaterialItem) -> Unit
) : RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_material, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val item = items[position]
        holder.itemTitle.text = item.title
        holder.itemSubtitle.text = item.subtitle
        holder.itemImage.setImageResource(item.imageResource)
        holder.statusText.apply {
            text = if (item.isCompleted) "Completed" else "Not Completed"
            setBackgroundColor(
                if (item.isCompleted)
                    context.getColor(R.color.bgBlue)
                else
                    context.getColor(R.color.primaryColorDark)
            )
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class MaterialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.item_image)
        val itemTitle: TextView = view.findViewById(R.id.item_title)
        val itemSubtitle: TextView = view.findViewById(R.id.item_subtitle)
        val statusText: TextView = view.findViewById(R.id.status_text)
    }
}


