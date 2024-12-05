package com.example.mycapstone.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycapstone.api.ApiConfig
import com.example.mycapstone.api.ApiService
import com.example.mycapstone.data.LessonResponseItem
import com.example.mycapstone.repository.LessonRepository
import kotlinx.coroutines.launch

class LessonViewModel(
    private val repository: LessonRepository = LessonRepository(ApiConfig.createService(ApiService::class.java))
) : ViewModel() {

    private val _lessons = MutableLiveData<List<LessonResponseItem>>()
    val lessons: LiveData<List<LessonResponseItem>> get() = _lessons

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        fetchLessons()
    }

    private fun fetchLessons() {
        viewModelScope.launch {
            try {
                val lessonList = repository.fetchLessons()
                _lessons.postValue(lessonList)
            } catch (e: Exception) {
                _error.postValue("Failed to fetch lessons: ${e.localizedMessage}")
            }
        }
    }
}
