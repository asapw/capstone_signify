package com.example.mycapstone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mycapstone.ui.login.manager.SessionManager

// Factory class to provide ViewModel with dependencies
class MainViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(sessionManager) as T  // Safe casting to T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
