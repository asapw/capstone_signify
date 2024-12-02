package com.example.mycapstone.ui.onboarding.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mycapstone.R
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.ui.login.manager.SessionManager


class Screen3 : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_screen3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SessionManager
        val sessionManager = SessionManager(requireContext())

        // Login Button
        val btnLogin = view.findViewById<View>(R.id.btnStart)
        btnLogin.setOnClickListener {
            // Mark onboarding as complete
            sessionManager.completeOnboarding()
            Log.d("Screen3", "Onboarding completed, flag set in SessionManager")


            // Navigate to LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Ensure onboarding activity is finished
        }
    }

}