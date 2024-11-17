package com.example.mycapstone.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mycapstone.R
import com.example.mycapstone.databinding.FragmentProfileBinding
import com.example.mycapstone.ui.login.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Handle My Account click
        binding.myAccountButton.setOnClickListener {
            // Navigate to the My Account section
        }

        // Handle Logout click
        binding.logoutButton.setOnClickListener {
            // Perform logout and navigate to LoginActivity
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()  // Close ProfileFragment after logout
        }

        // Handle Help & Support click
        binding.helpSupportButton.setOnClickListener {
            // Open Help and Support page (you can open a new activity or fragment here)
        }

        // Handle About App click
        binding.aboutAppButton.setOnClickListener {
            // Show info about the app (could be a dialog or a new activity)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}