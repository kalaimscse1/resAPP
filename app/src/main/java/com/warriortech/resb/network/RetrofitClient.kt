package com.warriortech.resb.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object for creating and managing the Retrofit client
 */
object RetrofitClient {
    private const val BASE_URL = "http://110.172.164.71:5050/api/" // Replace with your actual API URL
    // Create OkHttpClient with logging and timeout settings

    val apiService: ApiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

}