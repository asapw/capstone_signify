package com.example.mycapstone.ui.onboarding.screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.mycapstone.R
import com.example.mycapstone.ui.login.LoginActivity

class Screen2: Fragment(R.layout.fragment_screen2) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val finishOnboardingButton = view.findViewById<Button>(R.id.finishOnboardingButton)
        finishOnboardingButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

        }

    }
}