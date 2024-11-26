package com.example.mycapstone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.mycapstone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up NavController
        val navHostFragment = supportFragmentManager.findFragmentById(binding.navContainer.id) as NavHostFragment
        navController = navHostFragment.navController

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
                R.id.nav_learn -> {
                    navController.navigate(R.id.nav_learn)
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
            if (destination.id == R.id.nav_camera) {
                binding.fabCamera.hide() // Hide the FAB when on CameraFragment
            } else {
                binding.fabCamera.show() // Show the FAB on other fragments
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
