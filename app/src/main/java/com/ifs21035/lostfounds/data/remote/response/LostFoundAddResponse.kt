package com.ifs21035.lostfounds.data.remote.response

import com.google.gson.annotations.SerializedName

data class LostFoundAddResponse(

	@field:SerializedName("data")
	val data: DataAddLostFoundResponse,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DataAddLostFoundResponse(

	@field:SerializedName("lost_found_id")
	val lostFoundId: Int
)
