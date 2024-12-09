package com.example.mycapstone.ui.lesson.video

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.Player
import com.example.mycapstone.databinding.FragmentVideoBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mycapstone.R

class VideoFragment : Fragment() {

    private val args: VideoFragmentArgs by navArgs()
    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!
    private var player: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)

        // Set up the video player
        setupVideoPlayer(args.ytUrl)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adjust the bottom margin of the PlayerView to account for the BottomNavigationView
        val bottomNavigationHeight = requireActivity().findViewById<View>(R.id.bottom_navigation)?.height ?: 0
        val params = binding.videoView.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = bottomNavigationHeight
        binding.videoView.layoutParams = params
    }

    private fun setupVideoPlayer(videoUrl: String) {
        // Initialize ExoPlayer
        player = ExoPlayer.Builder(requireContext()).build()
        binding.videoView.player = player

        // Create a MediaItem
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true

        // Listen for video completion
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    onVideoCompleted(videoUrl)
                }
            }
        })
    }

    private fun onVideoCompleted(videoUrl: String) {
        // Mark the video as completed in SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("VideoCompletionPrefs", 0)
        with(sharedPreferences.edit()) {
            putBoolean(videoUrl, true)
            apply()
        }

        // Navigate to the next fragment or back
        val action = VideoFragmentDirections.actionVideoFragmentToLessonFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Release the player when the view is destroyed
        player?.release()
        player = null
        _binding = null
    }
}
