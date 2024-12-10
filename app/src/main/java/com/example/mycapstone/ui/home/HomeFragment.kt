package com.example.mycapstone.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mycapstone.R
import com.example.mycapstone.adapter.NewsAdapter
import com.example.mycapstone.data.Article
import com.example.mycapstone.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NewsAdapter
    private val newsList = mutableListOf<Article>()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NewsAdapter(newsList)
        setupNewsRV()

        homeViewModel.news.observe(viewLifecycleOwner) { articles ->
            newsList.clear()
            newsList.addAll(articles)
            adapter.notifyDataSetChanged()
        }

        // Observe LiveData from ViewModel
        homeViewModel.username.observe(viewLifecycleOwner, Observer { username ->
            binding.username.text = getString(R.string.greeting, username)
        })

        homeViewModel.email.observe(viewLifecycleOwner, Observer { email ->
            binding.email.text = email
        })

        homeViewModel.profileImageUrl.observe(viewLifecycleOwner, Observer { profileImageUrl ->
            if (profileImageUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .circleCrop()
                    .into(binding.profilePicture)
            } else {
                binding.profilePicture.setImageResource(R.drawable.default_profile_image)
            }
        })

        homeViewModel.lessonProgress.observe(viewLifecycleOwner, Observer { progress ->
            binding.lessonProgressBar.progress = progress
            binding.lessonProgressValue.text = getString(
                R.string.progress_value,
                (progress * 4) / 100, // Replace 4 with total lesson count
                4
            )
        })

        homeViewModel.quizProgress.observe(viewLifecycleOwner, Observer { progress ->
            binding.quizProgressBar.progress = progress
            binding.quizProgressValue.text = getString(
                R.string.progress_value,
                (progress * 4) / 100, // Replace 4 with total quiz count
                4
            )
        })

        // Fetch data
        homeViewModel.fetchUserData()
        homeViewModel.fetchUserProgress()

        // Navigate to scan
        binding.btnScan.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_camera)
        }
    }

    private fun setupNewsRV() {
        binding.newsRv.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.newsRv.adapter = adapter
        homeViewModel.fetchNews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
