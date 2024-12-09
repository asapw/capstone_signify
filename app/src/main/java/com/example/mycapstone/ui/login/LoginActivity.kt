package com.example.mycapstone.ui.login

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.mycapstone.MainActivity
import com.example.mycapstone.R
import com.example.mycapstone.databinding.ActivityLoginBinding
import com.example.mycapstone.ui.login.manager.SessionManager
import com.example.mycapstone.ui.signup.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager
    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoadingDialog()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideLoadingDialog()

                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""
                        sessionManager.saveLoginState(userId)
                        sessionManager.completeOnboarding() // Mark onboarding as complete
                        navigateToMainActivity()
                    } else {
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }

        }


        // btn Google Auth
        binding.btnGoogleLogin.setOnClickListener {
            // toDO()
        }
        // Register
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                R.anim.slide_in_right,  // Enter animation (from right to left)
                R.anim.slide_out_left   // Exit animation (left to right)
            )
            startActivity(intent, options.toBundle())
        }
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = Dialog(this).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.loading_dialog)
                setCancelable(false)


            }
        }
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
