package com.warriortech.resb.repository

import com.warriortech.resb.model.ApiResponse
import com.warriortech.resb.model.Table
import com.warriortech.resb.model.TableRequest
import com.warriortech.resb.model.TableStatusResponse
import com.warriortech.resb.network.ApiClient
import com.warriortech.resb.network.ApiEndpoints
import io.ktor.client.call.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TableRepository(private val apiClient: ApiClient) {
    
    fun getTables(): Flow<Result<List<Table>>> = flow {
        try {
            val response = apiClient.get(ApiEndpoints.TABLES)
            val apiResponse: ApiResponse<List<Table>> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                emit(Result.success(apiResponse.data))
            } else {
                emit(Result.failure(Exception(apiResponse.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getTableStatus(): Flow<Result<List<TableStatusResponse>>> = flow {
        try {
            val response = apiClient.get("${ApiEndpoints.TABLES}/status")
            val apiResponse: ApiResponse<List<TableStatusResponse>> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                emit(Result.success(apiResponse.data))
            } else {
                emit(Result.failure(Exception(apiResponse.message)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun createTable(table: TableRequest): Result<Table> {
        return try {
            val response = apiClient.post(ApiEndpoints.TABLES, table)
            val apiResponse: ApiResponse<Table> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                Result.success(apiResponse.data)
            } else {
                Result.failure(Exception(apiResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateTable(id: Long, table: TableRequest): Result<Table> {
        return try {
            val response = apiClient.put("${ApiEndpoints.TABLES}/$id", table)
            val apiResponse: ApiResponse<Table> = response.body()
            if (apiResponse.success && apiResponse.data != null) {
                Result.success(apiResponse.data)
            } else {
                Result.failure(Exception(apiResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateTableStatus(id: Long, status: String): Result<Boolean> {
        return try {
            val response = apiClient.put("${ApiEndpoints.TABLES}/$id/status", mapOf("status" to status))
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
