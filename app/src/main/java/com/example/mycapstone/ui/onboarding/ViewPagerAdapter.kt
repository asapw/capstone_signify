package com.example.mycapstone.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mycapstone.ui.onboarding.screen.Screen1
import com.example.mycapstone.ui.onboarding.screen.Screen2
import com.example.mycapstone.ui.onboarding.screen.Screen3

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Screen1()  // First onboarding screen
            1 -> Screen2()  // Second onboarding screen
            2 -> Screen3()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
