package com.example.mycapstone.ui.lesson

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycapstone.R
import com.example.mycapstone.adapter.MaterialAdapter
import com.example.mycapstone.data.MaterialItem

class LessonFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lesson, container, false)

        // Find RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.materialRecyclerView)

        // Create sample data
        val items = listOf(
            MaterialItem(R.drawable.ic_profile_placeholder, "Learn basic A-F", "Beginner | 6 Lessons", false),
            MaterialItem(R.drawable.ic_profile_placeholder, "Learn basic G-L", "Beginner | 6 Lessons", true),
            MaterialItem(R.drawable.ic_profile_placeholder, "Learn basic M-R", "Intermediate | 6 Lessons", false),
            MaterialItem(R.drawable.ic_profile_placeholder, "Learn basic S-Z", "Advanced | 6 Lessons", false)
        )

        // Set up RecyclerView with LinearLayoutManager and adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = MaterialAdapter(items)

        return view
    }
}
