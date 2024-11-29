package com.example.mycapstone.ui.lesson

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycapstone.R
import com.example.mycapstone.adapter.MaterialAdapter
import com.example.mycapstone.data.MaterialItem

class LessonFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: MaterialAdapter
    private val items = mutableListOf(
        MaterialItem(R.drawable.star_wars_logo, "Learn basic A-F", "Beginner | 6 Lessons", false, "rH5n2Q2RZrM"),
        MaterialItem(R.drawable.logosignify, "Learn basic G-L", "Beginner | 6 Lessons", false, "2jYa6Wcy53k"),
        MaterialItem(R.drawable.ic_profile_placeholder, "Learn basic M-R", "Intermediate | 6 Lessons", false, "sample3"),
        MaterialItem(R.drawable.ic_profile_placeholder, "Learn basic S-Z", "Advanced | 6 Lessons", false, "sample4")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lesson, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("VideoCompletionPrefs", 0)

        val recyclerView = view.findViewById<RecyclerView>(R.id.materialRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MaterialAdapter(items, ::onItemClick)
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        refreshCompletionStatuses()
    }

    private fun refreshCompletionStatuses() {
        for (item in items) {
            item.isCompleted = sharedPreferences.getBoolean(item.videoUrl, false)
        }
        adapter.notifyDataSetChanged()
    }

    private fun onItemClick(item: MaterialItem) {
        val action = LessonFragmentDirections.actionLessonFragmentToVideoFragment(item.videoUrl)
        findNavController().navigate(action)
    }
}
