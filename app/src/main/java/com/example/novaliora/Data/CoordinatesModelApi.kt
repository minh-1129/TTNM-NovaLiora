package com.example.novaliora.Data

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// Data class đại diện cho body gửi đi
data class CoordinatesRequest(
    val prompt: String?,
    val image_bytes: String
)

// Interface API
interface CoordinatesModelApi {

    @POST("/predict")
    suspend fun getCoordinatesModel(
        @Body request: CoordinatesRequest
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
                .baseUrl("https://8000-01jtn310t84krx8bwe80784905.cloudspaces.litng.ai")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(CoordinatesModelApi::class.java)
        }
    }
}
