package com.example.mycapstone.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mycapstone.R
import com.example.mycapstone.databinding.FragmentProfileBinding
import com.example.mycapstone.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val TAG = "ProfileFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Fetch the current user's name from Firestore
        fetchUserName()

        // Handle My Account click
        binding.myAccountButton.setOnClickListener {
            // Navigate to the My Account section
        }

        // Handle Logout click
        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // Handle Help & Support click
        binding.helpSupportButton.setOnClickListener {
            // Open Help and Support page
        }

        // Handle About App click
        binding.aboutAppButton.setOnClickListener {
            // Show info about the app
        }

        return root
    }
//ntr buat mvm masukin mvm func ny
    private fun fetchUserName() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "User"
                        binding.profileName.text = name
                    } else {
                        Log.d(TAG, "No user document found.")
                        binding.profileName.text = "User"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching user data: ", exception)
                    binding.profileName.text = "User"
                }
        } else {
            binding.profileName.text = "User"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}