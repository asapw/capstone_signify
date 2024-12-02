package com.example.mycapstone.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.mycapstone.MainActivity
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.ui.onboarding.OnBoardingActivity
import com.example.mycapstone.ui.login.manager.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        val sessionManager = SessionManager(applicationContext)
        val firebaseAuth = FirebaseAuth.getInstance()

        lifecycleScope.launch {
            delay(2000) // Show splash screen for 2 seconds

            val isOnboardingCompleted = sessionManager.isOnboardingCompleted()
            val isUserLoggedIn = firebaseAuth.currentUser != null

            Log.d("SplashActivity", "Onboarding Completed: $isOnboardingCompleted, User Logged In: $isUserLoggedIn")

            // Decide next activity based on conditions
            val nextActivity = when {
                !isOnboardingCompleted -> OnBoardingActivity::class.java
                isUserLoggedIn -> MainActivity::class.java
                else -> LoginActivity::class.java
            }

            startActivity(Intent(this@SplashActivity, nextActivity))
            finish()
        }
    }
}