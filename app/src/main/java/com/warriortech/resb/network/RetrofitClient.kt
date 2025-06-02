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
    private const val BASE_URL = "http://192.168.1.5:5050/api/" // Replace with your actual API URL
    // Create OkHttpClient with logging and timeout settings

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

//        .addInterceptor { chain ->
//            val original = chain.request()
//
//            // Add authorization header   if token exists
//            val requestBuilder = original.newBuilder()
//            SessionManager.getAuthToken()?.let {
//                requestBuilder.header("Authorization", "Bearer $it")
//            }
//
//            // Add company code header if it exists
//            SessionManager.getCompanyCode()?.let {
//                requestBuilder.header("X-Company-Code", it)
//            }
//
//            chain.proceed(requestBuilder.build())
//        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    val gson = GsonBuilder().setLenient().create()

    // Create Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val apiService: ApiService = Retrofit.Builder()
            .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    // Create API service
//    val apiService: ApiService = retrofit.create(ApiService::class.java)
}