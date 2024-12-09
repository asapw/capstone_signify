package com.example.mycapstone.data

import com.google.gson.annotations.SerializedName

data class ModelResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("prediction")
    val prediction: String,
    @SerializedName("timestamp")
    val timestamp: Long
)


