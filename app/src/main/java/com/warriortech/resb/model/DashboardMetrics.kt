data class DashboardMetrics(
    val runningOrders: Int = 0,
    val pendingBills: Int = 0,
    val totalSales: Double = 0.0,
    val pendingDue: Double = 0.0,
    val chartData: DashboardChartData? = null
)

data class DashboardChartData(
    val paymentModeData: List<PaymentModeData> = emptyList(),
    val weeklySalesData: List<WeeklySalesData> = emptyList()
)

data class PaymentModeData(
    val paymentMode: String,
    val amount: Double
)

data class WeeklySalesData(
    val date: String,
    val sales: Double
)