package com.warriortech.resb.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.warriortech.resb.network.SessionManager
import okhttp3.FormBody

/**
 * Singleton object for creating and managing the Retrofit client
 */
object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.6:5050/api/" // Replace with your actual API URL
    // Create OkHttpClient with logging and timeout settings

    val apiService: ApiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

}