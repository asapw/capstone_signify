package com.example.mycapstone.ui.profile.myaccount

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
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
    private var loadingDialog: Dialog? = null

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = Dialog(requireContext()).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.loading_dialog)
                setCancelable(false)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
            }
        }
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                selectedImageUri = data?.data

                if (selectedImageUri != null) {
                    Glide.with(this)
                        .load(selectedImageUri)
                        .into(binding.profileImage)
                } else {
                    Log.e(logTag, "Image selection failed")
                    context?.let {
                        Toast.makeText(it, "Failed to select image", Toast.LENGTH_SHORT).show()
                    }
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

        binding.profileImage.setOnClickListener {
            selectImage()
        }

        binding.btnUpdateProfile.setOnClickListener {
            showLoadingDialog()

            Handler(Looper.getMainLooper()).postDelayed({
                if (selectedImageUri != null) {
                    uploadImageToFirebase()
                }
                updateUserData()

                hideLoadingDialog()
            }, 4500)
        }

        loadUserData()
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user == null) {
            Log.e(logTag, "User not logged in")
            context?.let {
                Toast.makeText(it, "User not logged in", Toast.LENGTH_SHORT).show()
            }
            return
        }

        showLoadingDialog()

        firestore.collection("users").document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (isAdded) {
                    document?.let {
                        binding.etName.setText(it.getString("name"))
                        binding.etEmail.setText(it.getString("email"))
                        binding.etPhone.setText(it.getString("phone"))
                        binding.etBirthdate.setText(it.getString("birthdate"))
                        binding.etCity.setText(it.getString("city"))

                        val profileImageUrl = it.getString("profileImageUrl") ?: ""
                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .into(binding.profileImage)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                if (isAdded) {
                    Log.e(logTag, "Error loading user data", e)
                    context?.let {
                        Toast.makeText(it, "Failed to load user data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnCompleteListener {
                if (isAdded) {
                    hideLoadingDialog()
                }
            }
    }

    private fun uploadImageToFirebase() {
        selectedImageUri?.let { uri ->
            val user = auth.currentUser
            if (user == null) {
                Log.e(logTag, "User not logged in")
                return
            }

            firestore.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (isAdded) {
                        val currentImageUrl = document?.getString("profileImageUrl")
                        currentImageUrl?.let { url ->
                            if (url.isNotEmpty()) {
                                val oldImageRef = storage.getReferenceFromUrl(url)
                                oldImageRef.delete().addOnSuccessListener {
                                    Log.d(logTag, "Old image deleted.")
                                }.addOnFailureListener { e ->
                                    Log.e(logTag, "Failed to delete old image", e)
                                }
                            }
                        }

                        val storageRef = storage.reference.child("profile_images/${user.uid}/${UUID.randomUUID()}")
                        storageRef.putFile(uri)
                            .addOnSuccessListener {
                                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                    if (isAdded) {
                                        saveImageToFirestore(downloadUri.toString())
                                    }
                                }
                            }
                            .addOnFailureListener {
                                if (isAdded) {
                                    Log.e(logTag, "Image upload failed", it)
                                    context?.let { ctx ->
                                        Toast.makeText(ctx, "Failed to upload image", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    }
                }
        }
    }

    private fun saveImageToFirestore(imageUrl: String) {
        val user = auth.currentUser
        user?.let {
            firestore.collection("users").document(it.uid)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener {
                    if (isAdded) {
                        context?.let { ctx ->
                            Toast.makeText(ctx, "Profile image updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener {
                    if (isAdded) {
                        Log.e(logTag, "Failed to update Firestore", it)
                        context?.let { ctx ->
                            Toast.makeText(ctx, "Failed to update profile image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun updateUserData() {
        val user = auth.currentUser
        user?.let {
            val updatedData = mapOf(
                "name" to binding.etName.text.toString(),
                "email" to binding.etEmail.text.toString(),
                "phone" to binding.etPhone.text.toString(),
                "birthdate" to binding.etBirthdate.text.toString(),
                "city" to binding.etCity.text.toString()
            )

            firestore.collection("users").document(it.uid)
                .update(updatedData)
                .addOnSuccessListener {
                    if (isAdded) {
                        context?.let { ctx ->
                            Toast.makeText(ctx, "Profile updated", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener {
                    if (isAdded) {
                        Log.e(logTag, "Error updating profile", it)
                        context?.let { ctx ->
                            Toast.makeText(ctx, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
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
