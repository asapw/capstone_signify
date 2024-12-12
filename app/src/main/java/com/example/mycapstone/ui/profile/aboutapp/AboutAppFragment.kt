package com.example.mycapstone.ui.profile.aboutapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mycapstone.R
import com.example.mycapstone.databinding.FragmentAboutAppBinding

class AboutAppFragment : Fragment() {

    private var _binding: FragmentAboutAppBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutAppBinding.inflate(inflater, container, false)

        setupUI()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    private fun setupUI() {
        binding.aboutAppDescription.text =
            "Signify App: Revolutionizing mobile applications with cutting-edge technologies."
        binding.aboutAppVersion.text = "Version: 1.0.0"

        val teamMembers = listOf(
            Triple("Septian Wijaya", "Mobile Development", R.drawable.septian_w),
            Triple("Daniel Alexander", "Mobile Development", R.drawable.daniel_a),
            Triple("Agnes Valerie Khoe", "Machine Learning", R.drawable.agnes_k),
            Triple("Natasha Anabela", "Machine Learning", R.drawable.natasha_a),
            Triple("Nicholas Febrian Liswanto", "Machine Learning", R.drawable.nico),
            Triple("Akmal Muzakki Bakir", "Cloud Computing", R.drawable.akmal_m),
            Triple("Septian Hari Sabarno", "Cloud Computing", R.drawable.septian_h)
        )

        for ((name, role, imageRes) in teamMembers) {
            val teamMemberView = createTeamMemberView(name, role, imageRes)
            binding.teamContainer.addView(teamMemberView)
        }
    }

    private fun createTeamMemberView(name: String, role: String, imageRes: Int): View {
        val view = layoutInflater.inflate(R.layout.item_team_member, null)

        val profileImage = view.findViewById<ImageView>(R.id.profileImage)
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val roleTextView = view.findViewById<TextView>(R.id.roleTextView)

        profileImage.setImageResource(imageRes)
        nameTextView.text = name
        roleTextView.text = role

        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
