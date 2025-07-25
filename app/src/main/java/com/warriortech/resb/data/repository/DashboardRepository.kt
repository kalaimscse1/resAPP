
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.DashboardMetrics
import com.warriortech.resb.model.RunningOrder
import com.warriortech.resb.model.DashboardChartData
import com.warriortech.resb.model.PaymentModeData
import com.warriortech.resb.model.WeeklySalesData
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
                val response = apiService.getDashboardMetrics(SessionManager.getCompanyCode()?:"")
                if (response.isSuccessful && response.body() != null) {
                    val metrics = response.body()!!
                    val chartData = getChartData()
                    metrics.copy(chartData = chartData)
                } else {
                    // Return default metrics if API fails
                    DashboardMetrics(
                        runningOrders = 0,
                        pendingBills = 0,
                        totalSales = 0.0,
                        pendingDue = 0.0,
                        chartData = getDefaultChartData()
                    )
                }
            } catch (e: Exception) {
                // Return default metrics on error
                DashboardMetrics(
                    runningOrders = 0,
                    pendingBills = 0,
                    totalSales = 0.0,
                    pendingDue = 0.0,
                    chartData = getDefaultChartData()
                )
            }
        }
    }

    private suspend fun getChartData(): DashboardChartData {
        return try {
            // In a real app, these would be separate API calls
            val paymentModeData = getPaymentModeData()
            val weeklySalesData = getWeeklySalesData()
            DashboardChartData(paymentModeData, weeklySalesData)
        } catch (e: Exception) {
            getDefaultChartData()
        }
    }

    private fun getPaymentModeData(): List<PaymentModeData> {
        // Mock data - replace with actual API call
        return listOf(
            PaymentModeData("Cash", 15000.0, androidx.compose.ui.graphics.Color(0xFF4CAF50)),
            PaymentModeData("Card", 25000.0, androidx.compose.ui.graphics.Color(0xFF2196F3)),
            PaymentModeData("UPI", 18000.0, androidx.compose.ui.graphics.Color(0xFFFF9800)),
            PaymentModeData("Others", 5000.0, androidx.compose.ui.graphics.Color(0xFF9C27B0))
        )
    }

    private fun getWeeklySalesData(): List<WeeklySalesData> {
        // Mock data - replace with actual API call
        return listOf(
            WeeklySalesData("Mon", 8000.0),
            WeeklySalesData("Tue", 12000.0),
            WeeklySalesData("Wed", 15000.0),
            WeeklySalesData("Thu", 10000.0),
            WeeklySalesData("Fri", 18000.0),
            WeeklySalesData("Sat", 22000.0),
            WeeklySalesData("Sun", 16000.0)
        )
    }

    private fun getDefaultChartData(): DashboardChartData {
        return DashboardChartData(
            paymentModeData = getPaymentModeData(),
            weeklySalesData = getWeeklySalesData()
        )
    }

    suspend fun getRunningOrders(): List<RunningOrder> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRunningOrders(SessionManager.getCompanyCode()?:"")
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
                val response = apiService.getRecentActivity(SessionManager.getCompanyCode()?:"")
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
