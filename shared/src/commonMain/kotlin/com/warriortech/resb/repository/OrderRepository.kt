package com.warriortech.resb.repository

import com.warriortech.resb.model.ApiResponse
import com.warriortech.resb.model.OrderDetails
import com.warriortech.resb.model.OrderDetailsResponse
import com.warriortech.resb.model.OrderMaster
import com.warriortech.resb.model.OrderResponse
import com.warriortech.resb.network.ApiClient
import com.warriortech.resb.network.ApiEndpoints
import io.ktor.client.call.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderRepository(private val apiClient: ApiClient) {
    
    fun getOrders(): Flow<Result<List<OrderResponse>>> = flow {
        try {
            val response = apiClient.get(ApiEndpoints.ORDERS)
            val apiResponse: ApiResponse<List<OrderResponse>> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                emit(Result.success(apiResponse.data))
            } else {
                emit(Result.failure(Exception(apiResponse.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getOrderDetails(orderId: String): Flow<Result<List<OrderDetailsResponse>>> = flow {
        try {
            val response = apiClient.get("${ApiEndpoints.ORDER_DETAILS}/$orderId")
            val apiResponse: ApiResponse<List<OrderDetailsResponse>> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                emit(Result.success(apiResponse.data))
            } else {
                emit(Result.failure(Exception(apiResponse.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun createOrder(order: OrderMaster): Result<OrderResponse> {
        return try {
            val response = apiClient.post(ApiEndpoints.ORDERS, order)
            val apiResponse: ApiResponse<OrderResponse> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                Result.success(apiResponse.data)
            } else {
                Result.failure(Exception(apiResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addOrderDetails(details: OrderDetails): Result<OrderDetailsResponse> {
        return try {
            val response = apiClient.post(ApiEndpoints.ORDER_DETAILS, details)
            val apiResponse: ApiResponse<OrderDetailsResponse> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                Result.success(apiResponse.data)
            } else {
                Result.failure(Exception(apiResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Boolean> {
        return try {
            val response = apiClient.put("${ApiEndpoints.ORDERS}/$orderId/status", mapOf("status" to status))
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
