package com.example.mycapstone.data

import com.google.gson.annotations.SerializedName

data class LessonResponseItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("subTitle")
	val subTitle: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("completed")
	val completed: Boolean? = null,

	@field:SerializedName("title")
	val title: String? = null
)
