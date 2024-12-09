package com.example.mycapstone.api

import com.example.mycapstone.data.ModelResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ModelService{

    @Multipart
    @POST("predict")
    suspend fun detectSignLanguange (@Part file: MultipartBody.Part) : Response<ModelResponse>
}