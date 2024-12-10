package com.example.mycapstone.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mycapstone.data.Article
import com.example.mycapstone.databinding.NewsSingleItemBinding

class NewsAdapter(private val newsList: List<Article>): RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    class ViewHolder(private val binding: NewsSingleItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(news: Article){
            Glide.with(itemView.context)
                .load(news.urlToImage)
                .into(binding.newsImage)

            binding.newsTitle.text = news.title
            binding.newsDescription.text = news.description
            binding.newsPublishedDate.text = news.publishedAt
            binding.newsButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.url))
                itemView.context.startActivity(intent)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NewsSingleItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = newsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(newsList[position])
    }
}