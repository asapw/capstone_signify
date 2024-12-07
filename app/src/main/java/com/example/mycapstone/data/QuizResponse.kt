package com.example.mycapstone.data

import com.google.gson.annotations.SerializedName

data class QuizResponse(

	@field:SerializedName("QuizResponse")
	val quizResponse: List<QuizResponseItem?>? = null
)

data class QuizResponseItem(

	@field:SerializedName("imageQuestion")
	val imageQuestion: String? = null,

	@field:SerializedName("cOption")
	val cOption: String? = null,

	@field:SerializedName("checkQuestion")
	val checkQuestion: Boolean? = null,

	@field:SerializedName("qustion")
	val qustion: String? = null,

	@field:SerializedName("bOption")
	val bOption: String? = null,

	@field:SerializedName("dOption")
	val dOption: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("trueOption")
	val trueOption: String? = null,

	@field:SerializedName("aOption")
	val aOption: String? = null
)
