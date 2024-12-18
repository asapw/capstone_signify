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
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = sessionManager.getUserId()

        val navHostFragment = supportFragmentManager.findFragmentById(binding.navContainer.id) as NavHostFragment
        navController = navHostFragment.navController

        navController.currentBackStackEntry?.savedStateHandle?.set("userId", userId)

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

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

        binding.fabCamera.setOnClickListener {
            navController.navigate(R.id.nav_camera)
        }

        // Listen for navigation changes to hide/show the FloatingActionButton
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_camera,R.id.nav_my_account,R.id.nav_about_app, R.id.nav_help_support ,R.id.resultsFragment-> {
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

