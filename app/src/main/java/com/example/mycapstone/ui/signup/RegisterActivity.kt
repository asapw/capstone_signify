package com.example.mycapstone.ui.signup

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.mycapstone.R
import com.example.mycapstone.ui.login.LoginActivity
import com.example.mycapstone.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private val db = FirebaseFirestore.getInstance()
    private var loadingDialog: Dialog? = null

    companion object {
        private const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            val email = binding.registerEmailEditText.text.toString().trim()
            val password = binding.registerPasswordEditText.text.toString().trim()
            val name = binding.registerNameEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.registerPasswordEditText.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            showLoadingDialog()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideLoadingDialog()
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        saveUserData(user, name, email)
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }


        binding.tvLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                R.anim.slide_in_right,  // Enter animation (from right to left)
                R.anim.slide_out_left   // Exit animation (left to right)
            )
            startActivity(intent, options.toBundle())
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
            "profileImageUrl" to "",
            "phone" to "",
            "birthdate" to "",
            "city" to ""
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
}