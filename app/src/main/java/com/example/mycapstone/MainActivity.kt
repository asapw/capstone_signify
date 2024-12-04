package com.example.mycapstone

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.mycapstone.databinding.ActivityMainBinding
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.ui.login.manager.SessionManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            navigateToLoginActivity()
            return // Stop further initialization
        }

        // Initialize binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get userId from session manager
        val userId = sessionManager.getUserId()

        // Set up NavController
        val navHostFragment = supportFragmentManager.findFragmentById(binding.navContainer.id) as NavHostFragment
        navController = navHostFragment.navController

        // Pass the userId to the ProfileFragment using the NavController
        navController.currentBackStackEntry?.savedStateHandle?.set("userId", userId)

        // Set up BottomNavigationView with NavController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        // Handle BottomNavigation item clicks
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.nav_home)
                    true
                }
                R.id.nav_lesson -> {
                    navController.navigate(R.id.nav_lesson)
                    true
                }
                R.id.nav_camera -> {
                    navController.navigate(R.id.nav_camera)
                    true
                }
                R.id.nav_quiz -> {
                    navController.navigate(R.id.nav_quiz)
                    true
                }
                R.id.nav_profile -> {
                    navController.navigate(R.id.nav_profile)
                    true
                }
                else -> false
            }
        }

        // Handle FloatingActionButton click
        binding.fabCamera.setOnClickListener {
            navController.navigate(R.id.nav_camera)
        }

        // Listen for navigation changes to hide/show the FloatingActionButton
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_camera -> {
                    binding.bottomNavigation.visibility = View.GONE
                    binding.fabCamera.hide()
                }
                else -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.fabCamera.show()
                }
            }
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

