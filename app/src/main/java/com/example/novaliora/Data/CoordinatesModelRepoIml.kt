package com.example.novaliora.Data

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File


class CoordinatesModelRepoImpl(
    val applicationContext: Context
) : CoordinatesModelRepo {
    fun uriToHex(context: Context, uri: Uri): String {
        val resolver = context.contentResolver
        val inputStream = resolver.openInputStream(uri) ?: return ""
        val bytes = inputStream.readBytes()
        return bytes.joinToString("") { "%02x".format(it) } // Mỗi byte thành 2 ký tự hex
    }


    override suspend fun getCoordinatesModel(requestModel: RequestModel): Response<CoordinatesModel> {
        return withContext(Dispatchers.IO) {
            val hexImage = uriToHex(applicationContext, requestModel.uri)
            Log.d("TAG", "Hex image length = ${hexImage.length}")

            CoordinatesModelApi.instance.getCoordinatesModel(
                text = requestModel.text.toRequestBody(MultipartBody.FORM),
                width = requestModel.width.toRequestBody(MultipartBody.FORM),
                height = requestModel.height.toRequestBody(MultipartBody.FORM),
                imageHex = hexImage.toRequestBody(MultipartBody.FORM)
            )
        }
    }


}

fun getTempFile(context: Context, uri: Uri): File? {
    try {
        val resolver = context.contentResolver
        val tempFile = File(context.cacheDir, "${System.currentTimeMillis()}_.jpg")
        val inputStream = resolver.openInputStream(uri) ?: return null
        val outputStream = tempFile.outputStream()
        val buffer = ByteArray(4 * 1024) // Adjust buffer size as needed
        while (true) {
            val read = inputStream.read(buffer)
            if (read == -1) break
            outputStream.write(buffer, 0, read)
        }
        outputStream.flush()
        return tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}