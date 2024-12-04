package com.example.mycapstone.ui.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            // Directly update the items in the adapter
            adapter.updateItems(lessonList)
        }
    }

    private fun onItemClick(item: LessonResponseItem) {
        val action = LessonFragmentDirections.actionLessonFragmentToVideoFragment(item.id ?: "")
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
