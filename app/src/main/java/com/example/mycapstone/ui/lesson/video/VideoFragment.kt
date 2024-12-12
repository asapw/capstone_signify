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
import com.google.android.material.snackbar.Snackbar

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

        setupVideoPlayer(args.ytUrl)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showWarningMessage()

        val bottomNavigationHeight = requireActivity().findViewById<View>(R.id.bottom_navigation)?.height ?: 0
        val params = binding.videoView.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = bottomNavigationHeight
        binding.videoView.layoutParams = params
    }

    private fun setupVideoPlayer(videoUrl: String) {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.videoView.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true

        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    onVideoCompleted(videoUrl)
                }
            }
        })
    }

    private fun onVideoCompleted(videoUrl: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("VideoCompletionPrefs", 0)
        with(sharedPreferences.edit()) {
            putBoolean(videoUrl, true)
            apply()
        }

        val action = VideoFragmentDirections.actionVideoFragmentToLessonFragment()
        findNavController().navigate(action)
    }

    private fun showWarningMessage() {
        Snackbar.make(binding.root, "You need to watch the video until the end to complete it!", Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
        _binding = null
    }
}
