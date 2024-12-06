package com.example.mycapstone.ui.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycapstone.adapter.MaterialAdapter
import com.example.mycapstone.data.LessonResponseItem
import com.example.mycapstone.databinding.FragmentLessonBinding
import com.example.mycapstone.viewmodel.LessonViewModel

class LessonFragment : Fragment() {

    private var _binding: FragmentLessonBinding? = null
    private val binding get() = _binding!!
    private val lessonViewModel: LessonViewModel by viewModels()
    private lateinit var adapter: MaterialAdapter
    private val items = mutableListOf<LessonResponseItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and Adapter
        adapter = MaterialAdapter(items, ::onItemClick)
        binding.materialRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.materialRecyclerView.adapter = adapter

        // Observe lessons from the ViewModel and update the adapter
        lessonViewModel.lessons.observe(viewLifecycleOwner) { lessonList ->
            // Check ytUrl before passing it to the adapter
            lessonList.forEach {
                println("Lesson ID: ${it.id}, Title: ${it.title}, ytUrl: ${it.ytUrl}")
            }
            adapter.updateItems(lessonList)
        }
    }

    private fun onItemClick(item: LessonResponseItem) {
        if (item.ytUrl.isNullOrEmpty()) {
            println("Error: Video URL is null or empty for lesson: ${item.title}")
            Toast.makeText(context, "Video URL is missing for ${item.title}.", Toast.LENGTH_SHORT).show()
            return
        }
        println("Navigating to: ${item.ytUrl}")
        val action = LessonFragmentDirections.actionLessonFragmentToVideoFragment(item.ytUrl ?: "")
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
