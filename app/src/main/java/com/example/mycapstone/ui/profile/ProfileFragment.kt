package com.example.mycapstone.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mycapstone.databinding.FragmentProfileBinding
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.ui.login.manager.SessionManager
import com.example.mycapstone.viewmodel.MainViewModel
import com.example.mycapstone.viewmodel.MainViewModelFactory
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

    private fun setupUI() {
        binding.progressBar.visibility = View.VISIBLE
        binding.profileContentLayout.visibility = View.GONE

        // Get the current user
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Fetch user data from Firestore
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "User"
                        binding.profileName.text = name
                    } else {
                        Log.e("ProfileFragment", "No user data found")
                        binding.profileName.text = "User"
                    }
                    binding.progressBar.visibility = View.GONE
                    binding.profileContentLayout.visibility = View.VISIBLE
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileFragment", "Error fetching user data", exception)
                    binding.profileName.text = "User"
                    binding.progressBar.visibility = View.GONE
                    binding.profileContentLayout.visibility = View.VISIBLE
                }
        } else {
            Log.e("ProfileFragment", "No authenticated user found")
            binding.profileName.text = "User"
            binding.progressBar.visibility = View.GONE
            binding.profileContentLayout.visibility = View.VISIBLE
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
            // Navigate to "My Account" section
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
