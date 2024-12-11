package com.example.mycapstone.ui.quiz.question

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mycapstone.R
import com.example.mycapstone.databinding.FragmentQuestionBinding
import android.media.MediaPlayer

class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: QuestionViewModel
    private var quizId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(QuestionViewModel::class.java)

        val args = QuestionFragmentArgs.fromBundle(requireArguments())
        quizId = args.quizId

        setupUI(args)
        return binding.root
    }

    private fun setupUI(args: QuestionFragmentArgs) {
        binding.questionTitle.text = args.question
        binding.optionA.text = args.optionA
        binding.optionB.text = args.optionB
        binding.optionC.text = args.optionC
        binding.optionD.text = args.optionD

        // Load question image if available
        val imageUrl = args.imageQuestion
        if (!imageUrl.isNullOrEmpty()) {
            binding.questionImage.visibility = View.VISIBLE
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(binding.questionImage)
        } else {
            binding.questionImage.visibility = View.GONE
        }

        binding.submitButton.setOnClickListener {
            val selectedId = binding.optionGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedOption = binding.root.findViewById<RadioButton>(selectedId).text.toString()
                if (selectedOption == args.correctOption) {
                    showCorrectIndicator()
                    if (args.checkQuestion == false) {
                        viewModel.markQuizAsCompleted(quizId!!)
                    }
                    Handler().postDelayed({
                        findNavController().navigateUp()
                    }, 2000) // Delay to allow the user to see the feedback
                } else {
                    showIncorrectIndicator()
                    Toast.makeText(requireContext(), "Wrong Answer!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCorrectIndicator() {
        binding.correctIndicator.visibility = View.VISIBLE
        binding.incorrectIndicator.visibility = View.GONE

        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.clink)
        mediaPlayer.start()

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            val vibrationEffect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)
        }
    }

    private fun showIncorrectIndicator() {
        binding.correctIndicator.visibility = View.GONE
        binding.incorrectIndicator.visibility = View.VISIBLE

        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.wrong)
        mediaPlayer.start()

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            val vibrationEffect = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
