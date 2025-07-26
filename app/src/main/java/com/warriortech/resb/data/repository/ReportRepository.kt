package com.warriortech.resb.data.repository

import com.warriortech.resb.model.*
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getTodaySales(): Flow<Result<TodaySalesReport>> = flow {
        try {
            val response = apiService.getTodaySales(SessionManager.getCompanyCode()?:"")
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch today's sales: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception("Network error: ${e.message}")))
        }
    }

    suspend fun getGSTSummary(): Flow<Result<GSTSummaryReport>> = flow {
        try {
            val response = apiService.getGSTSummary(SessionManager.getCompanyCode()?:"")
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch GST summary: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception("Network error: ${e.message}")))
        }
    }

    suspend fun getSalesSummaryByDate(date: String): Flow<Result<SalesSummaryReport>> = flow {
        try {
            val response = apiService.getSalesSummaryByDate(date,SessionManager.getCompanyCode()?:"")
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to fetch sales summary: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception("Network error: ${e.message}")))
        }
    }
}
