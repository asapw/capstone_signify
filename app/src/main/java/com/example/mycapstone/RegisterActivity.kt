package com.example.mycapstone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    companion object {
        private const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)

        findViewById<Button>(R.id.registerButton).setOnClickListener {
            val email = findViewById<EditText>(R.id.registerEmailEditText).text.toString()
            val password = findViewById<EditText>(R.id.registerPasswordEditText).text.toString()

            // Show ProgressBar
            progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // Hide ProgressBar
                    progressBar.visibility = View.GONE

                    if (task.isSuccessful) {
                        // Registration success
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // Registration failed
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Registration failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
        }

        // Set up listener for "Login Now" button to go to LoginActivity
        findViewById<Button>(R.id.loginNowButton).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close RegisterActivity
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Registration successful, go to LoginActivity (no auto login to MainActivity)
            Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show()

            // Explicitly clear any existing session (force logout to prevent auto-login)
            auth.signOut()

            // Intent to go to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close RegisterActivity
        } else {
            Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}


