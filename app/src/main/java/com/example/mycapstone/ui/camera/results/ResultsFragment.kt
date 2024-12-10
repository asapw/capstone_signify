package com.example.mycapstone.ui.camera.results

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mycapstone.R
import com.example.mycapstone.databinding.FragmentResultsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultsFragment : Fragment() {

    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val generatedText = arguments?.getString("generatedText")
        val timestamp = arguments?.getLong("timestamp")
        binding.textViewGeneratedText.text = generatedText
        if (timestamp != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedTimestamp = dateFormat.format(Date(timestamp))
            binding.textViewTimestamp.text = "Timestamp: $formattedTimestamp"
        } else {
            binding.textViewTimestamp.text = "Timestamp: N/A"
        }

        binding.imageViewBackArrow.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}