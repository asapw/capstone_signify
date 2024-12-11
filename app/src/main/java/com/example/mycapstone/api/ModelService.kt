package com.example.mycapstone.api

import com.example.mycapstone.data.ModelResponse
import com.example.mycapstone.data.AutoCorrectResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ModelService{

    @Multipart
    @POST("predict")
    suspend fun detectSignLanguange (@Part file: MultipartBody.Part) : Response<ModelResponse>

    @POST("autocorrect")
    suspend fun autoCorrect(@Query("message") message: String) : Response<AutoCorrectResponse>
}