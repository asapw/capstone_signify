package com.example.mycapstone.ui.onboarding.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mycapstone.R
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.ui.login.manager.SessionManager
import com.example.mycapstone.ui.signup.RegisterActivity

class Screen2 : Fragment(R.layout.fragment_screen2) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SessionManager
        val sessionManager = SessionManager(requireContext())

        // Login Button
        val btnLogin = view.findViewById<View>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            // Mark onboarding as complete
            sessionManager.completeOnboarding()
            Log.d("Screen2", "Onboarding completed, flag set in SessionManager")


            // Navigate to LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Ensure onboarding activity is finished
        }

        // Signup Button
        val btnSignup = view.findViewById<View>(R.id.btnSignup)
        btnSignup.setOnClickListener {
            // Mark onboarding as complete
            sessionManager.completeOnboarding()
            Log.d("Screen2", "Onboarding completed, flag set in SessionManager")


            // Navigate to RegisterActivity
            val intent = Intent(requireContext(), RegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Ensure onboarding activity is finished
        }
    }
}
