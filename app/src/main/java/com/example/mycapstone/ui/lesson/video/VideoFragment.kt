package com.example.mycapstone.ui.lesson.video

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mycapstone.R

class VideoFragment : Fragment() {

    private val args: VideoFragmentArgs by navArgs()  // SafeArgs to get the videoUrl
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("VideoCompletionPrefs", 0)

        // Retrieve the video URL from SafeArgs
        val videoUrl = args.videoUrl

        val webView = view.findViewById<WebView>(R.id.youtubeWebView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // Load the video using YouTube embed URL format
        webView.loadUrl("https://www.youtube.com/embed/$videoUrl")

        // Setup button click listener
        view.findViewById<Button>(R.id.video_complete_button).setOnClickListener {
            markAsCompleted(videoUrl)
        }

        return view
    }

    private fun markAsCompleted(videoUrl: String) {
        // Mark as completed in SharedPreferences
        with(sharedPreferences.edit()) {
            putBoolean(videoUrl, true)  // Store completion status based on video URL
            apply()
        }

        // After completion, navigate back to the LessonFragment
        findNavController().navigateUp()  // This will navigate back to the previous fragment
    }
}
