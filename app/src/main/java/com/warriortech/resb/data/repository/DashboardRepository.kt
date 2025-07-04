
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.DashboardMetrics
import com.warriortech.resb.model.RunningOrder
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getDashboardMetrics(): DashboardMetrics {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDashboardMetrics()
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else {
                    // Return default metrics if API fails
                    DashboardMetrics(
                        runningOrders = 0,
                        pendingBills = 0,
                        totalSales = 0.0,
                        pendingDue = 0.0
                    )
                }
            } catch (e: Exception) {
                // Return default metrics on error
                DashboardMetrics(
                    runningOrders = 0,
                    pendingBills = 0,
                    totalSales = 0.0,
                    pendingDue = 0.0
                )
            }
        }
    }

    suspend fun getRunningOrders(): List<RunningOrder> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRunningOrders()
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getRecentActivity(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecentActivity()
                if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                listOf(
                    "Order #1001 completed",
                    "New order received - Table 5",
                    "Payment received - Order #998"
                )
            }
        }
    }
}
