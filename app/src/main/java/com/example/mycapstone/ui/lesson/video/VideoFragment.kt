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

    private val args: VideoFragmentArgs by navArgs()

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
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return if (url != null && (url.contains("youtube.com") || url.contains("youtu.be"))) {
                    false
                } else {
                    true
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                injectJsToDetectVideoCompletion(webView)
            }
        }

        val videoUrl = args.ytUrl
        println("Loading video: $videoUrl")

        val secureUrl = when {
            videoUrl.contains("youtube.com/shorts/") -> videoUrl.replace("youtube.com/shorts/", "youtube.com/embed/")
            videoUrl.contains("youtube.com/watch?v=") -> videoUrl.replace("youtube.com/watch?v=", "youtube.com/embed/")
            videoUrl.contains("youtu.be/") -> videoUrl.replace("youtu.be/", "youtube.com/embed/")
            else -> videoUrl
        }

        webView.loadUrl(secureUrl)

        webView.addJavascriptInterface(object {
            @android.webkit.JavascriptInterface
            @Suppress("unused")
            fun onVideoCompleted() {
                val completedVideoUrl = args.ytUrl
                println("Video completed: $completedVideoUrl")
                markAsCompleted(completedVideoUrl)
            }
        }, "Android")

        return view
    }

    private fun injectJsToDetectVideoCompletion(webView: WebView) {
        val js = """
            var video = document.querySelector('video');
            if (video) {
                video.onended = function() {
                    Android.onVideoCompleted();
                }
            }
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    private fun markAsCompleted(videoUrl: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("VideoCompletionPrefs", 0)
        with(sharedPreferences.edit()) {
            putBoolean(videoUrl, true)
            apply()
        }

        val action = VideoFragmentDirections.actionVideoFragmentToLessonFragment()
        findNavController().navigate(action)
    }
}
