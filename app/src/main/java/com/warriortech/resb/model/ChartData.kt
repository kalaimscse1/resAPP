
package com.warriortech.resb.model

data class PaymentModeData(
    val paymentMode: String,
    val amount: Double,
    val color: androidx.compose.ui.graphics.Color
)

data class WeeklySalesData(
    val date: String,
    val amount: Double
)

data class DashboardChartData(
    val paymentModeData: List<PaymentModeData>,
    val weeklySalesData: List<WeeklySalesData>
)
