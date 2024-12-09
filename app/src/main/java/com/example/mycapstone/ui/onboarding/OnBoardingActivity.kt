package com.example.mycapstone.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.mycapstone.R
import com.example.mycapstone.databinding.ActivityOnBoardingBinding
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.ui.login.manager.SessionManager

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.viewPager.adapter = ViewPagerAdapter(this)
        binding.dotsIndicator.attachTo(binding.viewPager)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Show/hide button according to the page
                if (position == 2) {  // Last screen (Screen 3)
                    binding.btnNext.text = "Start Now"
                } else {
                    binding.btnNext.text = "Next"
                }
            }
        })

        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem == 2) { // Last screen (Screen 3)
                // Mark onboarding as complete
                sessionManager.completeOnboarding()
                // Navigate to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                // Move to the next screen
                binding.viewPager.currentItem = currentItem + 1
            }
        }
    }
}