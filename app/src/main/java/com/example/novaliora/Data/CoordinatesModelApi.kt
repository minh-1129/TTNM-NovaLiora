package com.example.novaliora.Data

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit


import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded

interface CoordinatesModelApi {

    @POST("/api/detect")
    @FormUrlEncoded
    suspend fun getCoordinatesModel(
        @Field("prompt") text: String?,
        @Field("width") width: String?,
        @Field("height") height: String?,
        @Field("image_hex") imageHex: String
    ): Response<CoordinatesModel>

    companion object {
        private val client: OkHttpClient =
            OkHttpClient
                .Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(240, TimeUnit.SECONDS)
                .readTimeout(240, TimeUnit.SECONDS)
                .build()

        val instance: CoordinatesModelApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://paligemma.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(CoordinatesModelApi::class.java)
        }
    }
}
