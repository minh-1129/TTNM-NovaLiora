package com.example.novaliora.Data

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class CoordinatesModelRepoImpl(
    val applicationContext: Context
) : CoordinatesModelRepo {

    // Chuyển Uri ảnh thành chuỗi hex
    fun uriToHex(context: Context, uri: Uri): String {
        val resolver = context.contentResolver
        val inputStream = resolver.openInputStream(uri) ?: return ""
        val bytes = inputStream.readBytes()
        return bytes.joinToString("") { "%02x".format(it) }
    }

    override suspend fun getCoordinatesModel(requestModel: RequestModel): Response<CoordinatesModel> {
        return withContext(Dispatchers.IO) {
            val hexImage = uriToHex(applicationContext, requestModel.uri)
            Log.d("TAG", "Hex image length = ${hexImage.length}")

            // Tạo request body JSON
            val jsonRequest = CoordinatesRequest(
                prompt = requestModel.text,
                image_bytes = hexImage
            )

            // Gửi lên server
            CoordinatesModelApi.instance.getCoordinatesModel(jsonRequest)
        }
    }
}
