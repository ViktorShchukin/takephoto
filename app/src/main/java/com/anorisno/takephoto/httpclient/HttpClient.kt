package com.anorisno.takephoto.httpclient

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException

class HttpClient {

    private val TAG = "HttpClient"

    private val client: OkHttpClient = OkHttpClient()

    private val BASE_PATH: String = "http://192.168.27.161:8080"
    private val SAVE_PHOTO_PATH = "/photo/save"

    private val MEDIA_TYPE_PNG = "image/png".toMediaType()

    fun sendFile(file: File) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("photo", "photo", file.asRequestBody(MEDIA_TYPE_PNG))
            .build()
        val request = Request.Builder()
            .url(BASE_PATH + SAVE_PHOTO_PATH)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "can't send file" , e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i(TAG, "successfully send file")
            }
        })
    }
}