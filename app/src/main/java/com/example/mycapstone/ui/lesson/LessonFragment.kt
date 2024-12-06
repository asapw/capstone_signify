package com.example.mycapstone.ui.lesson

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycapstone.adapter.MaterialAdapter
import com.example.mycapstone.data.LessonResponseItem
import com.example.mycapstone.databinding.FragmentLessonBinding
import com.example.mycapstone.viewmodel.LessonViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

        // Check completed state from SharedPreferences
        lessonViewModel.lessons.observe(viewLifecycleOwner) { lessonList ->
            val sharedPreferences = requireActivity().getSharedPreferences("VideoCompletionPrefs", 0)

            lessonList.forEach { lesson ->
                lesson.isCompleted = sharedPreferences.getBoolean(lesson.ytUrl ?: "", false)
                Log.d("LessonFragment", "Lesson: ${lesson.title}, isCompleted: ${lesson.isCompleted}")
            }

            adapter.updateItems(lessonList)
        }


    }

    private fun onItemClick(item: LessonResponseItem) {
        if (item.ytUrl.isNullOrEmpty()) {
            Toast.makeText(context, "Video URL is missing for ${item.title}.", Toast.LENGTH_SHORT).show()
            return
        }

        // Ensure the lesson ID is not null
        item.id?.let { lessonId ->
            markLessonAsCompleted(lessonId) // Only call if lessonId is not null

            val action = LessonFragmentDirections.actionLessonFragmentToVideoFragment(item.ytUrl)
            findNavController().navigate(action)
        } ?: run {
            // Handle case where the ID is null
            Toast.makeText(context, "Lesson ID is missing for ${item.title}.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun markLessonAsCompleted(lessonId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId)

            userDocRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val completedLessons = (document.get("completedLessons") as? List<*>)?.toMutableList() ?: mutableListOf()

                    // Debugging: Check if lesson already exists
                    Log.d("LessonFragment", "Existing completed lessons: $completedLessons")

                    if (!completedLessons.contains(lessonId)) {
                        completedLessons.add(lessonId)
                        userDocRef.update("completedLessons", completedLessons)
                        Log.d("LessonFragment", "Added lessonId: $lessonId to completedLessons")
                    } else {
                        Log.d("LessonFragment", "LessonId: $lessonId is already marked as completed")
                    }
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
