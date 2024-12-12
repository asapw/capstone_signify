package com.example.mycapstone.api

import com.example.mycapstone.data.LessonResponseItem
import com.example.mycapstone.data.QuizResponseItem
import retrofit2.http.GET

interface ApiService {
    @GET("api/lessons")
    suspend fun getLessons(): List<LessonResponseItem>

    @GET("api/quizzes")
    suspend fun getquizzes(): List<QuizResponseItem>
}