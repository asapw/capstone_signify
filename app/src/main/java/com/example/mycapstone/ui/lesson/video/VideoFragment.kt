package com.example.mycapstone.ui.lesson.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mycapstone.R

class VideoFragment : Fragment() {

    private val args: VideoFragmentArgs by navArgs() // SafeArgs to access `yt_url`

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)

        val webView = view.findViewById<WebView>(R.id.youtubeWebView)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            mediaPlaybackRequiresUserGesture = false
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                println("Page loaded: $url")
                injectJsToDetectVideoCompletion(webView)
            }
        }

        // Get and process the URL
        val videoUrl = args.ytUrl
        if (videoUrl.isNullOrEmpty()) {
            println("Error: Received empty or null video URL")
        } else {
            println("Loading video: $videoUrl")
        }

        val secureUrl = when {
            videoUrl.contains("youtube.com/shorts/") -> videoUrl.replace("youtube.com/shorts/", "youtube.com/embed/")
            videoUrl.contains("youtube.com/watch?v=") -> videoUrl.replace("youtube.com/watch?v=", "youtube.com/embed/")
            videoUrl.contains("youtu.be/") -> videoUrl.replace("youtu.be/", "youtube.com/embed/")
            else -> videoUrl
        }

        webView.loadUrl(secureUrl)

        // Set up Javascript Interface to interact with the video
        webView.addJavascriptInterface(object {
            @android.webkit.JavascriptInterface
            fun onVideoCompleted() {
                val videoUrl = args.ytUrl
                println("Video completed: $videoUrl")
                markAsCompleted(videoUrl)
            }
        }, "Android")

        return view
    }

    private fun injectJsToDetectVideoCompletion(webView: WebView) {
        val js = """
            var video = document.querySelector('video');
            if (video) {
                video.onended = function() {
                    // Notify Android when the video ends
                    Android.onVideoCompleted();
                }
            }
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    private fun markAsCompleted(videoUrl: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("VideoCompletionPrefs", 0)
        with(sharedPreferences.edit()) {
            putBoolean(videoUrl, true)  // Mark the video as completed
            apply()
        }
        findNavController().navigateUp()  // Navigate back to the previous fragment
    }

}
