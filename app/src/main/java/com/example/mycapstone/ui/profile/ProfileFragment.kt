package com.example.mycapstone.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mycapstone.R
import com.example.mycapstone.databinding.FragmentProfileBinding
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.ui.login.manager.SessionManager
import com.example.mycapstone.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        sessionManager = SessionManager(requireContext())

        profileViewModel.profileData.observe(viewLifecycleOwner) { profileData ->
            binding.profileName.text = profileData.name
            binding.profileEmail.text = profileData.email
            Glide.with(this)
                .load(profileData.profileImageUrl)
                .placeholder(R.drawable.default_profile_image)
                .error(R.drawable.default_profile_image)
                .into(binding.profileImage)
        }

        profileViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.profileImageProgressBar.visibility = View.VISIBLE
                binding.profileNameProgressBar.visibility = View.VISIBLE
            } else {
                binding.profileImageProgressBar.visibility = View.GONE
                binding.profileNameProgressBar.visibility = View.GONE
            }
        }

        setupUI()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        profileViewModel.fetchUserData()
    }

    private fun setupUI() {
        binding.logoutButton.setOnClickListener {
            profileViewModel.signOut(sessionManager)
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.myAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myAccountFragment)
        }

        binding.helpSupportButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_helpSupportFragment)
        }

        binding.aboutAppButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_aboutAppFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
