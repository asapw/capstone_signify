package com.example.mycapstone.data

import com.google.gson.annotations.SerializedName

data class ModelResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("detection")
    val detection: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("image")
    val image: String
)


