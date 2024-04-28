package com.ifs21035.lostfounds.data.remote.retrofit

import com.ifs21035.lostfounds.data.remote.response.LostFoundLoginResponse
import com.ifs21035.lostfounds.data.remote.response.LostFoundUserResponse
import com.ifs21035.lostfounds.data.remote.response.LostFoundAddResponse
import com.ifs21035.lostfounds.data.remote.response.LostFoundDetailResponse
import com.ifs21035.lostfounds.data.remote.response.LostFoundResponse
import com.ifs21035.lostfounds.data.remote.response.LostFoundsResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface IApiService {
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): LostFoundResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LostFoundLoginResponse

    @GET("users/me")
    suspend fun getMe(): LostFoundUserResponse

    @FormUrlEncoded
    @POST("lost-founds")
    suspend fun postLostFound(
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("status") status: String
    ): LostFoundAddResponse

    @Multipart
    @POST("lost-founds/{id}/cover")
    suspend fun addCoverLostFound(
        @Path("id") lostFoundId: Int,
        @Part cover: MultipartBody.Part
    ): LostFoundResponse

    @Multipart
    @POST("users/photo")
    suspend fun addPhotoProfile(
        @Part cover:MultipartBody.Part
    ): LostFoundResponse

    @FormUrlEncoded
    @PUT("lost-founds/{id}")
    suspend fun putLostFound(
        @Path("id") lostFoundId: Int,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("status") status: String,
        @Field("is_completed") isCompleted: Int,
    ): LostFoundResponse

    @GET("lost-founds")
    suspend fun getLostFounds(
        @Query("is_completed") isCompleted: Int?,
        @Query("is_me") isMe: Int?,
        @Query("status") status: String?
    ): LostFoundsResponse

    @GET("lost-founds/{id}")
    suspend fun getLostFound(
        @Path("id") lostFoundId: Int,
    ): LostFoundDetailResponse

    @DELETE("lost-founds/{id}")
    suspend fun deleteLostFound(
        @Path("id") lostFoundId: Int,
    ): LostFoundResponse
}