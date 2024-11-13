package com.example.mycapstone

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()

            // Show ProgressBar
            progressBar.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // Hide ProgressBar
                    progressBar.visibility = View.GONE

                    if (task.isSuccessful) {
                        // Login success
                        val user = auth.currentUser
                        Toast.makeText(this, "Welcome, ${user?.email}", Toast.LENGTH_SHORT).show()

                        // Navigate to MainActivity after successful login
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Close LoginActivity to prevent going back to it
                    } else {
                        // Login failed, show error
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        findViewById<Button>(R.id.registerNowButton).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Check if the user is already signed in when the activity starts
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload() // Call a method to update the UI if the user is signed in
        }
    }

    private fun reload() {
        // Navigate to MainActivity if user is already logged in
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity to prevent going back to it
    }
}
