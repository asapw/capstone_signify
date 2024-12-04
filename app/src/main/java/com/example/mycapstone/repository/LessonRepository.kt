package com.example.mycapstone.repository

import com.example.mycapstone.api.ApiService
import com.example.mycapstone.data.LessonResponseItem

class LessonRepository(private val apiService: ApiService) {

    suspend fun fetchLessons(): List<LessonResponseItem> {
        return try {
            apiService.getLessons() // Directly fetch the list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Return an empty list on failure
        }
    }
}
