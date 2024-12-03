package com.example.mycapstone.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mycapstone.R
import com.example.mycapstone.databinding.FragmentProfileBinding
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.ui.login.manager.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Initialize sessionManager
        sessionManager = SessionManager(requireContext())
        auth = FirebaseAuth.getInstance()

        setupUI()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        refreshUserData() // Refresh user data when fragment resumes
    }

    private fun refreshUserData() {
        binding.profileImageProgressBar.visibility = View.VISIBLE

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "User"
                        val profileImageUrl = document.getString("profileImageUrl") ?: ""

                        binding.profileName.text = name

                        // Load profile image using Glide
                        Glide.with(this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.default_profile_image)
                            .error(R.drawable.default_profile_image)
                            .into(binding.profileImage)
                    } else {
                        Log.e("ProfileFragment", "No user data found")
                        binding.profileName.text = "User"
                        binding.profileImage.setImageResource(R.drawable.default_profile_image)
                    }
                    binding.profileImageProgressBar.visibility = View.GONE
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileFragment", "Error fetching user data", exception)
                    binding.profileName.text = "User"
                    binding.profileImage.setImageResource(R.drawable.default_profile_image)
                    binding.profileImageProgressBar.visibility = View.GONE
                }
        } else {
            Log.e("ProfileFragment", "No authenticated user found")
            binding.profileName.text = "User"
            binding.profileImage.setImageResource(R.drawable.default_profile_image)
            binding.profileImageProgressBar.visibility = View.GONE
        }
    }

    private fun setupUI() {
        binding.profileImageProgressBar.visibility = View.VISIBLE

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "User"
                        val profileImageUrl = document.getString("profileImageUrl") ?: ""

                        binding.profileName.text = name

                        // Load profile image using Glide
                        Glide.with(this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.default_profile_image)
                            .error(R.drawable.default_profile_image)
                            .into(binding.profileImage)
                    } else {
                        Log.e("ProfileFragment", "No user data found")
                        binding.profileName.text = "User"
                        binding.profileImage.setImageResource(R.drawable.default_profile_image)
                    }
                    binding.profileImageProgressBar.visibility = View.GONE
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileFragment", "Error fetching user data", exception)
                    binding.profileName.text = "User"
                    binding.profileImage.setImageResource(R.drawable.default_profile_image)
                    binding.profileImageProgressBar.visibility = View.GONE
                }
        } else {
            Log.e("ProfileFragment", "No authenticated user found")
            binding.profileName.text = "User"
            binding.profileImage.setImageResource(R.drawable.default_profile_image)
            binding.profileImageProgressBar.visibility = View.GONE
        }
        setupListeners()
    }

    private fun setupListeners() {
        binding.logoutButton.setOnClickListener {
            // Sign out the user
            FirebaseAuth.getInstance().signOut()

            // Clear session
            sessionManager.clearSession()

            // Navigate to LoginActivity
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.myAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myAccountFragment)
        }

        binding.helpSupportButton.setOnClickListener {
            // Open Help & Support section
        }

        binding.aboutAppButton.setOnClickListener {
            // Show information about the app
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
