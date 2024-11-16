package com.example.mycapstone.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mycapstone.ui.onboarding.screen.Screen1
import com.example.mycapstone.ui.onboarding.screen.Screen2

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    // Number of pages in the ViewPager
    override fun getItemCount(): Int {
        return 2  // Only two screens
    }

    // Create the fragment for the given position
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Screen1()  // First onboarding screen
            1 -> Screen2()  // Second onboarding screen
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
