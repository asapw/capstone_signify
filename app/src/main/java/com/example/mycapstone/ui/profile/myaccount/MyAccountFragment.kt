package com.example.mycapstone.ui.profile.myaccount

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mycapstone.R
import com.example.mycapstone.databinding.FragmentMyAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class MyAccountFragment : Fragment() {

    private var _binding: FragmentMyAccountBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val logTag = "MyAccountFragment"

    private var selectedImageUri: Uri? = null

    // Activity Result Launcher for image selection
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                selectedImageUri = data?.data

                // Display the selected image
                if (selectedImageUri != null) {
                    Glide.with(this)
                        .load(selectedImageUri)
                        .into(binding.profileImage)
                } else {
                    Log.e(logTag, "Image selection failed")
                    Toast.makeText(requireContext(), "Failed to select image", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAccountBinding.inflate(inflater, container, false)
        setupWindowInsets()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigationView = requireActivity().findViewById<View>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Start image selection when profile image is clicked
        binding.profileImage.setOnClickListener {
            selectImage()  // This opens the image picker
        }

        binding.btnUpdateProfile.setOnClickListener {
            // Update user data including uploading image if selected
            if (selectedImageUri != null) {
                uploadImageToFirebase() // Upload the image if it's selected
            }
            updateUserData()
        }

        loadUserData()
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user == null) {
            Log.e(logTag, "User not logged in")
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch user details from Firebase Authentication (optional)
        val nameFromAuth = user.displayName
        val emailFromAuth = user.email

        // Update the UI with Firebase Authentication data (fallback)
        binding.tvUserName.text = nameFromAuth ?: "Unknown Name"
        binding.tvUserEmail.text = emailFromAuth ?: "Unknown Email"

        // Fetch user details from Firestore
        firestore.collection("users").document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Update editable fields
                    binding.etName.setText(document.getString("name"))
                    binding.etEmail.setText(document.getString("email"))
                    binding.etPhone.setText(document.getString("phone"))
                    binding.etBirthdate.setText(document.getString("birthdate"))
                    binding.etCity.setText(document.getString("city"))

                    // Update static fields
                    binding.tvUserName.text = document.getString("name") ?: nameFromAuth ?: "Unknown Name"
                    binding.tvUserEmail.text = document.getString("email") ?: emailFromAuth ?: "Unknown Email"

                    // Load profile image if available
                    val profileImageUrl = document.getString("profileImageUrl") ?: ""
                    if (profileImageUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(binding.profileImage)
                    }
                } else {
                    Log.d(logTag, "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.e(logTag, "Error loading user data", e)
                Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun uploadImageToFirebase() {
        selectedImageUri?.let { uri ->
            val user = auth.currentUser
            if (user == null) {
                Log.e(logTag, "User not logged in")
                return
            }

            val storageRef = storage.reference.child("profile_images/${user.uid}/${UUID.randomUUID()}")
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    Log.d(logTag, "Image uploaded successfully.")
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        Log.d(logTag, "Image download URL: $downloadUri")
                        saveImageToFirestore(downloadUri.toString())

                        // Optional: Save the image as Base64 in Firestore (not recommended for large images)
                        encodeImageToBase64(uri)?.let { base64Image ->
                            saveBase64ImageToFirestore(base64Image)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e(logTag, "Image upload failed", it)
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        } ?: Log.e(logTag, "No image selected for upload")
    }

    private fun saveImageToFirestore(imageUrl: String) {
        val user = auth.currentUser
        if (user == null) {
            Log.e(logTag, "User not logged in")
            return
        }

        firestore.collection("users").document(user.uid)
            .update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile image updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.e(logTag, "Failed to update Firestore")
                Toast.makeText(requireContext(), "Failed to update profile image", Toast.LENGTH_SHORT).show()
            }
    }

    // Optional method for Base64 encoding and saving to Firestore (not recommended for large images)
    private fun encodeImageToBase64(uri: Uri): String? {
        try {
            val inputStream = context?.contentResolver?.openInputStream(uri)
            val byteArray = inputStream?.readBytes()
            return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e(logTag, "Error encoding image", e)
        }
        return null
    }

    private fun saveBase64ImageToFirestore(base64Image: String) {
        val user = auth.currentUser
        if (user == null) {
            Log.e(logTag, "User not logged in")
            return
        }

        firestore.collection("users").document(user.uid)
            .update("profileImageBase64", base64Image)
            .addOnSuccessListener {
                Log.d(logTag, "Base64 profile image saved to Firestore")
            }
            .addOnFailureListener {
                Log.e(logTag, "Failed to save Base64 image to Firestore", it)
            }
    }

    private fun updateUserData() {
        val user = auth.currentUser
        if (user == null) {
            Log.e(logTag, "User not logged in")
            return
        }

        val updatedData = mapOf(
            "name" to binding.etName.text.toString(),
            "email" to binding.etEmail.text.toString(),
            "phone" to binding.etPhone.text.toString(),
            "birthdate" to binding.etBirthdate.text.toString(),
            "city" to binding.etCity.text.toString()
        )

        firestore.collection("users").document(user.uid)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.e(logTag, "Error updating profile", it)
                Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bottomNavigationView = requireActivity().findViewById<View>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.VISIBLE
        _binding = null
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
