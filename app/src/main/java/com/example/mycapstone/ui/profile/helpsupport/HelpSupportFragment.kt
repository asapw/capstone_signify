package com.example.mycapstone.ui.profile.helpsupport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mycapstone.databinding.FragmentHelpSupportBinding

class HelpSupportFragment : Fragment() {

    private var _binding: FragmentHelpSupportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHelpSupportBinding.inflate(inflater, container, false)

        // Set up the UI (help content, contact, etc.)
        setupUI()

        // Set up the back button click listener
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed() // Go back to the previous fragment/activity
        }

        return binding.root
    }

    private fun setupUI() {
        // Set help and support details
        binding.helpSupportDescription.text = "If you have any questions or need assistance, please contact support."
        binding.contactSupport.text = "Contact Support: support@example.com"
        binding.helpFAQ.text = "FAQ: Visit our website for frequently asked questions."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
