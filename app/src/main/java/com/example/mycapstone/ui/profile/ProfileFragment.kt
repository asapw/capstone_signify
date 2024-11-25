package com.example.mycapstone.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mycapstone.databinding.FragmentProfileBinding
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Observe user name LiveData
        mainViewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.profileName.text = name
        }

        // Fetch the current user's name
        mainViewModel.fetchUserName()

        // Set up button listeners
        setupListeners()

        return root
    }

    private fun setupListeners() {
        binding.myAccountButton.setOnClickListener {
            // Navigate to the My Account section
        }

        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.helpSupportButton.setOnClickListener {
            // Open Help and Support page
        }

        binding.aboutAppButton.setOnClickListener {
            // Show info about the app
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
