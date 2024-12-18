package com.example.mycapstone.ui.profile.helpsupport

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mycapstone.databinding.FragmentHelpSupportBinding

class HelpSupportFragment : Fragment() {

    private var _binding: FragmentHelpSupportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHelpSupportBinding.inflate(inflater, container, false)

        setupUI()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.contactSupport.setOnClickListener {
            openEmailClient()
        }

        binding.contactWhatsApp.setOnClickListener {
            openWhatsApp()
        }

        return binding.root
    }

    private fun setupUI() {
        binding.helpSupportDescription.text = "If you have any questions or need assistance, please contact support."
        binding.contactSupport.text = "Contact Support: a195b4ky4107@bangkit.academy"
        binding.helpFAQ.text = "FAQ: Visit our website for frequently asked questions."
    }

    private fun openEmailClient() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:a195b4ky4107@bangkit.academy"))
        startActivity(Intent.createChooser(emailIntent, "Send Email"))
    }

    private fun openWhatsApp() {
        val whatsappIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/0895335191326"))
        startActivity(whatsappIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
