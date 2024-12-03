package com.example.mycapstone.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mycapstone.R
import com.example.mycapstone.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        fetchUserData()
        return binding.root
    }

    private fun fetchUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("name") ?: "User"
                        val email = document.getString("email") ?: "Not available"
                        val profileImageUrl = document.getString("profileImageUrl") ?: ""

                        binding.username.text = "Hello,\n$username"
                        binding.email.text = email

                        // Load profile picture
                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ic_profile_placeholder) // Placeholder image
                                .error(R.drawable.ic_profile_placeholder) // Error fallback image
                                .circleCrop()
                                .into(binding.profilePicture)
                        } else {
                            binding.profilePicture.setImageResource(R.drawable.ic_profile_placeholder)
                        }
                    } else {
                        Log.e("HomeFragment", "No user data found")
                        binding.username.text = "Hello,\nUser"
                        binding.email.text = "Not available"
                        binding.profilePicture.setImageResource(R.drawable.ic_profile_placeholder)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("HomeFragment", "Error fetching user data", exception)
                    binding.username.text = "Hello,\nUser"
                    binding.email.text = "Not available"
                    binding.profilePicture.setImageResource(R.drawable.ic_profile_placeholder)
                }
        } else {
            Log.e("HomeFragment", "No authenticated user")
            binding.username.text = "Hello,\nUser"
            binding.email.text = "Not available"
            binding.profilePicture.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
