package com.example.mycapstone.data

data class MaterialItem(
    val imageResource: Int,
    val title: String,
    val subtitle: String,
    var isCompleted: Boolean,
    val videoUrl: String
)
