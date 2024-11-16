package com.example.mycapstone.ui.onboarding

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mycapstone.R
import com.example.mycapstone.databinding.ActivityOnBoardingBinding

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = ViewPagerAdapter(this)
        binding.dotsIndicator.attachTo(binding.viewPager)
    }
}