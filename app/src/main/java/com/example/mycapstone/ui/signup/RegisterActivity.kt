package com.example.mycapstone.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Register Button Click Listener
        binding.registerButton.setOnClickListener {
            val email = binding.registerEmailEditText.text.toString().trim()
            val password = binding.registerPasswordEditText.text.toString().trim()
            val name = binding.registerNameEditText.text.toString().trim()


            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show ProgressBar
            binding.progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        saveUserData(user, name, email)
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Login Now Button Click Listener
        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close RegisterActivity
        }
    }

    private fun saveUserData(user: FirebaseUser?, name: String, email: String) {
        if (user == null) {
            Toast.makeText(this, "User is null. Please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = user.uid
        val userData = mapOf(
            "name" to name,
            "email" to email,
        )

        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "User data added to Firestore.")
                updateUI(user)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user data to Firestore", e)
                Toast.makeText(this, "Failed to save user data. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show()
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}