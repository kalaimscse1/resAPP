package com.warriortech.resb.repository

import com.warriortech.resb.model.ApiResponse
import com.warriortech.resb.model.AuthResponse
import com.warriortech.resb.model.LoginRequest
import com.warriortech.resb.model.Staff
import com.warriortech.resb.network.ApiClient
import com.warriortech.resb.network.ApiEndpoints
import io.ktor.client.call.*

class AuthRepository(private val apiClient: ApiClient) {
    
    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = apiClient.post(ApiEndpoints.LOGIN, request)
            val apiResponse: ApiResponse<AuthResponse> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                Result.success(apiResponse.data)
            } else {
                Result.failure(Exception(apiResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout(): Result<Boolean> {
        return try {
            val response = apiClient.post(ApiEndpoints.LOGOUT)
            val apiResponse: ApiResponse<Any> = response.body()
            if (apiResponse.success) {
                Result.success(true)
            } else {
                Result.failure(Exception(apiResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentStaff(): Result<Staff> {
        return try {
            val response = apiClient.get("${ApiEndpoints.STAFF}/current")
            val apiResponse: ApiResponse<Staff> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                Result.success(apiResponse.data)
            } else {
                Result.failure(Exception(apiResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
