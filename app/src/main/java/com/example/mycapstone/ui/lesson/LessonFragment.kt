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
import com.example.mycapstone.R
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

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        val bottomNavigationHeight = requireActivity().findViewById<View>(R.id.bottom_navigation)?.height ?: 0
        val params = binding.materialRecyclerView.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = bottomNavigationHeight
        binding.materialRecyclerView.layoutParams = params

        lessonViewModel.lessons.observe(viewLifecycleOwner) { lessonList ->
            fetchCompletedLessons { completedLessons ->
                lessonList.forEach { lesson ->
                    lesson.isCompleted = completedLessons.contains(lesson.id)
                    Log.d("LessonFragment", "Lesson: ${lesson.title}, isCompleted: ${lesson.isCompleted}")
                }
                adapter.updateItems(lessonList)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = MaterialAdapter(items, ::onItemClick)
        binding.materialRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.materialRecyclerView.adapter = adapter
    }

    private fun onItemClick(item: LessonResponseItem) {
        if (item.ytUrl.isNullOrEmpty()) {
            Toast.makeText(context, "Video URL is missing for ${item.title}.", Toast.LENGTH_SHORT).show()
            return
        }

        val lessonId = item.id
        if (lessonId.isNullOrEmpty()) {
            Toast.makeText(context, "Lesson ID is missing for ${item.title}.", Toast.LENGTH_SHORT).show()
            return
        }

        markLessonAsCompleted(lessonId)
        val action = LessonFragmentDirections.actionLessonFragmentToVideoFragment(item.ytUrl)
        findNavController().navigate(action)
    }

    private fun markLessonAsCompleted(lessonId: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("LessonFragment", "No authenticated user")
            return
        }

        val userDocRef = db.collection("users").document(currentUser.uid)

        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val completedLessons =
                    (document.get("completedLessons") as? List<String>)?.toMutableList() ?: mutableListOf()

                if (!completedLessons.contains(lessonId)) {
                    completedLessons.add(lessonId)
                    userDocRef.update("completedLessons", completedLessons)
                        .addOnSuccessListener {
                            Log.d("LessonFragment", "Lesson $lessonId marked as completed")
                        }
                        .addOnFailureListener { e ->
                            Log.e("LessonFragment", "Failed to update completed lessons", e)
                        }
                } else {
                    Log.d("LessonFragment", "Lesson $lessonId is already marked as completed")
                }
            } else {
                Log.e("LessonFragment", "User document does not exist")
            }
        }.addOnFailureListener { e ->
            Log.e("LessonFragment", "Error fetching user document", e)
        }
    }

    private fun fetchCompletedLessons(callback: (List<String>) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("LessonFragment", "No authenticated user")
            callback(emptyList())
            return
        }

        val userDocRef = db.collection("users").document(currentUser.uid)
        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val completedLessons = document.get("completedLessons") as? List<String> ?: emptyList()
                Log.d("LessonFragment", "Fetched completedLessons: $completedLessons")
                callback(completedLessons)
            } else {
                Log.e("LessonFragment", "User document does not exist")
                callback(emptyList())
            }
        }.addOnFailureListener { e ->
            Log.e("LessonFragment", "Error fetching user document", e)
            callback(emptyList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
