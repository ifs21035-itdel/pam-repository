package com.ifs21035.lostfounds.data.repository

import com.google.gson.Gson
import com.ifs21035.lostfounds.data.remote.MyResult
import com.ifs21035.lostfounds.data.remote.response.LostFoundResponse
import com.ifs21035.lostfounds.data.remote.retrofit.IApiService
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.HttpException

class LostFoundRepository private constructor(
    private val apiService: IApiService,
) {
    fun postLostFound(
        title: String,
        description: String,
        status: String,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.postLostFound(title, description, status).data
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LostFoundResponse::class.java)
                        .message
                )
            )
        }
    }
    fun putLostFound(
        lostFoundId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.putLostFound(
                        lostFoundId,
                        title,
                        description,
                        status,
                        if (isCompleted) 1 else 0
                    )
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LostFoundResponse::class.java)
                        .message
                )
            )
        }
    }
    fun getLostFounds(
        isCompleted: Int?,
        isMe: Int?,
        status: String?
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.getLostFounds(isCompleted, isMe, status)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LostFoundResponse::class.java)
                        .message
                )
            )
        }
    }

    fun getLostFound(
        lostFoundId: Int,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.getLostFound(lostFoundId)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LostFoundResponse::class.java)
                        .message
                )
            )
        }
    }
    fun deleteLostFound(
        lostFoundId: Int,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.deleteLostFound(lostFoundId)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LostFoundResponse::class.java)
                        .message
                )
            )
        }
    }

    fun addCoverLostFound(
        lostFoundId: Int,
        cover: MultipartBody.Part,
    )= flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.addCoverLostFound(lostFoundId, cover)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, LostFoundResponse::class.java)
                        .message
                )
            )
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LostFoundRepository? = null
        fun getInstance(
            apiService: IApiService,
        ): LostFoundRepository {
            synchronized(LostFoundRepository::class.java) {
                INSTANCE = LostFoundRepository(
                    apiService
                )
            }
            return INSTANCE as LostFoundRepository
        }
    }
}

