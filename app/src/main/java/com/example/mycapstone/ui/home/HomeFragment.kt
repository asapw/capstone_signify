package com.example.mycapstone.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mycapstone.R
import com.example.mycapstone.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Check Firebase initialization (Optional but recommended)
        if (!::auth.isInitialized) {
            FirebaseApp.initializeApp(requireContext())
        }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        fetchUserData()
        fetchUserProgress()

        return binding.root
    }

    // Fetch user data
    private fun fetchUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("name") ?: getString(R.string.default_username)
                        val email = document.getString("email") ?: getString(R.string.default_email)
                        val profileImageUrl = document.getString("profileImageUrl") ?: ""

                        // Update UI on the main thread
                        binding.username.text = getString(R.string.greeting, username)
                        binding.email.text = email

                        // Load profile picture
                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .error(R.drawable.ic_profile_placeholder)
                                .circleCrop()
                                .into(binding.profilePicture)
                        } else {
                            binding.profilePicture.setImageResource(R.drawable.ic_profile_placeholder)
                        }
                    } else {
                        Log.e("HomeFragment", "No user data found")
                        setDefaultUserData()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("HomeFragment", "Error fetching user data", exception)
                    setDefaultUserData()
                }
        } else {
            Log.e("HomeFragment", "No authenticated user")
            setDefaultUserData()
        }
    }

    // Set default user data in case of error
    private fun setDefaultUserData() {
        binding.username.text = getString(R.string.greeting, getString(R.string.default_username))
        binding.email.text = getString(R.string.default_email)
        binding.profilePicture.setImageResource(R.drawable.ic_profile_placeholder)
    }

    // Fetch user progress
    private fun fetchUserProgress() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Safely fetch completed lessons
                        val completedLessons = (document.get("completedLessons") as? List<String>) ?: emptyList()

                        // Debugging: Log completed lessons
                        Log.d("HomeFragment", "Completed lessons: $completedLessons")

                        // Example: Total lessons count
                        val totalLessons = 4 // Replace with actual lesson count

                        // Check if all lessons are marked as incomplete
                        val completedCount = completedLessons.size
                        Log.d("HomeFragment", "Completed count: $completedCount, Total: $totalLessons")

                        // Calculate progress
                        val progress = if (totalLessons > 0) {
                            (completedCount.toFloat() / totalLessons) * 100
                        } else {
                            0f
                        }

                        // Update UI on the main thread
                        binding.lessonProgressBar.progress = progress.toInt()
                        binding.lessonProgressValue.text = getString(
                            R.string.progress_value,
                            completedCount,
                            totalLessons
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("HomeFragment", "Error fetching progress", exception)
                    Toast.makeText(requireContext(), R.string.error_fetch_progress, Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
