package com.example.mycapstone.ui.profile.aboutapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import com.example.mycapstone.databinding.FragmentAboutAppBinding

class AboutAppFragment : Fragment() {

    private var _binding: FragmentAboutAppBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAboutAppBinding.inflate(inflater, container, false)

        // Set up the UI (text, app version, developer info, etc.)
        setupUI()

        // Set up the back button click listener
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed() // Calls the activity's onBackPressed() to go back
        }

        return binding.root
    }

    private fun setupUI() {
        // Set app description and other details here
        binding.aboutAppDescription.text = "This app is a great example of how to use Firebase, navigate through fragments, and much more!"
        binding.aboutAppVersion.text = "Version: 1.0.0"
        binding.aboutAppDeveloper.text = "Developer: Your Name"
        binding.aboutAppContact.text = "Contact: your.email@example.com"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
