package com.example.mycapstone.ui.onboarding.screen

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.mycapstone.R
import androidx.viewpager2.widget.ViewPager2

class Screen1 : Fragment(R.layout.fragment_screen1) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnNext = view.findViewById<Button>(R.id.btnNext)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        btnNext.setOnClickListener {
            viewPager?.currentItem = 1
        }
    }
}
