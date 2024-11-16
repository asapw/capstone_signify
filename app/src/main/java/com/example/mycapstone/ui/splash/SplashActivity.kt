package com.example.mycapstone.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


import androidx.lifecycle.lifecycleScope
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.ui.onboarding.OnBoardingActivity
import com.example.mycapstone.utils.PrefManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashScreenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition{
            viewModel.isLoading.value
        }

        val prefManager = PrefManager(applicationContext)

        lifecycleScope.launch {
            delay(3000) // Splash screen ditampilkan selama 3 detik
            val isOnboardingCompleted = prefManager.isOnboardingCompleted.first()
            val nextActivity = if (isOnboardingCompleted) {
                LoginActivity::class.java
            } else {
                OnBoardingActivity::class.java
            }

            startActivity(Intent(this@SplashActivity, nextActivity))
            finish()
        }
    }
}
