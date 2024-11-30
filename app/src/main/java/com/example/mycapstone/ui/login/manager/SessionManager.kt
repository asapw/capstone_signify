package com.example.mycapstone.ui.login.manager

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserSessionPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_IS_ONBOARDING_COMPLETED = "isOnboardingCompleted" // New Key
    }

    // Save login state and user ID
    fun saveLoginState(userId: String) {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, userId)
            apply()
        }
    }

    // Mark onboarding as completed
    fun completeOnboarding() {
        Log.d("SessionManager", "Marking onboarding as complete")
        with(sharedPreferences.edit()) {
            putBoolean(KEY_IS_ONBOARDING_COMPLETED, true)
            apply()
        }
    }

    fun isOnboardingCompleted(): Boolean {
        val isCompleted = sharedPreferences.getBoolean(KEY_IS_ONBOARDING_COMPLETED, false)
        Log.d("SessionManager", "isOnboardingCompleted: $isCompleted")
        return isCompleted
    }


    // Clear session data
    fun clearSession() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Get the logged-in user's ID
    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }
}
