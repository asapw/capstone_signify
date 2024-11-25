package com.example.mycapstone.ui.onboarding.screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mycapstone.R
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.ui.signup.RegisterActivity

class Screen2 : Fragment(R.layout.fragment_screen2) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Login Button
        val btnLogin = view.findViewById<View>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        // Signup Button
        val btnSignup = view.findViewById<View>(R.id.btnSignup)
        btnSignup.setOnClickListener {
            val intent = Intent(requireContext(), RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
