package com.ifs21035.lostfounds.data.remote.response

import com.google.gson.annotations.SerializedName

data class LostFoundResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)
