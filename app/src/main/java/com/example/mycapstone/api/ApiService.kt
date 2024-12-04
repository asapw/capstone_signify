package com.example.mycapstone.api

import com.example.mycapstone.data.LessonResponseItem
import retrofit2.http.GET

interface ApiService {
    @GET("api/lessons") // Correct endpoint with 'api' path
    suspend fun getLessons(): List<LessonResponseItem>
}