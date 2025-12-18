package com.warriortech.resb.repository

import com.warriortech.resb.model.ApiResponse
import com.warriortech.resb.model.MenuCategory
import com.warriortech.resb.model.MenuItemRequest
import com.warriortech.resb.model.MenuItemResponse
import com.warriortech.resb.network.ApiClient
import com.warriortech.resb.network.ApiEndpoints
import io.ktor.client.call.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MenuRepository(private val apiClient: ApiClient) {
    
    fun getMenuItems(): Flow<Result<List<MenuItemResponse>>> = flow {
        try {
            val response = apiClient.get(ApiEndpoints.MENU_ITEMS)
            val apiResponse: ApiResponse<List<MenuItemResponse>> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                emit(Result.success(apiResponse.data))
            } else {
                emit(Result.failure(Exception(apiResponse.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getMenuCategories(): Flow<Result<List<MenuCategory>>> = flow {
        try {
            val response = apiClient.get(ApiEndpoints.MENU_CATEGORIES)
            val apiResponse: ApiResponse<List<MenuCategory>> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                emit(Result.success(apiResponse.data))
            } else {
                emit(Result.failure(Exception(apiResponse.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun createMenuItem(item: MenuItemRequest): Result<MenuItemResponse> {
        return try {
            val response = apiClient.post(ApiEndpoints.MENU_ITEMS, item)
            val apiResponse: ApiResponse<MenuItemResponse> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                Result.success(apiResponse.data)
            } else {
                Result.failure(Exception(apiResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateMenuItem(id: Long, item: MenuItemRequest): Result<MenuItemResponse> {
        return try {
            val response = apiClient.put("${ApiEndpoints.MENU_ITEMS}/$id", item)
            val apiResponse: ApiResponse<MenuItemResponse> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                Result.success(apiResponse.data)
            } else {
                Result.failure(Exception(apiResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteMenuItem(id: Long): Result<Boolean> {
        return try {
            val response = apiClient.delete("${ApiEndpoints.MENU_ITEMS}/$id")
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
}
