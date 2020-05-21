package com.andreborud.thecoachtimer.data.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class Api {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private var client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Get function for simple post requests
    @Throws(IOException::class)
    fun post(url: String, json: String): String {
        val body: RequestBody = json.toRequestBody(jsonMediaType)
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        client.newCall(request).execute().use { response -> return response.body!!.string() }
    }

    // Get function for simple get requests
    @Throws(IOException::class)
    fun get(url: String): String {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).execute().use { response -> return response.body!!.string() }
    }

}