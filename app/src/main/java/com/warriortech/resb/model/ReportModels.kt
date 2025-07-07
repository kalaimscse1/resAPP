package com.warriortech.resb.model

import java.time.LocalDate

data class SalesReport(
    val date: LocalDate,
    val totalSales: Double,
    val totalOrders: Int,
    val gstBreakdown: GstBreakdown,
    val cessTotal: Double,
    val hsnSummary: List<HsnSummary>
)

data class GstBreakdown(
    val cgst: Double,
    val sgst: Double,
    val igst: Double,
    val totalGst: Double
)

data class HsnSummary(
    val hsnCode: String,
    val description: String,
    val quantity: Int,
    val taxableValue: Double,
    val cgst: Double,
    val sgst: Double,
    val igst: Double,
    val cess: Double,
    val totalTax: Double
)

data class TodaySalesMetrics(
    val totalRevenue: Double,
    val totalOrders: Int,
    val averageOrderValue: Double,
    val taxCollected: Double,
    val cessCollected: Double
)
data class SalesSummaryReport(
    val totalSales: Double,
    val totalOrders: Int,
    val averageOrderValue: Double,
    val topSellingItems: List<TopSellingItem>
)

// API Response wrappers
data class TodaySalesResponse(
    val success: Boolean,
    val message: String,
    val data: TodaySalesReport?
)

data class GSTSummaryResponse(
    val success: Boolean,
    val message: String,
    val data: GSTSummaryReport?
)

data class SalesSummaryResponse(
    val success: Boolean,
    val message: String,
    val data: SalesSummaryReport?
)

data class TodaySalesReport(
    val totalSales: Double,
    val totalOrders: Int,
    val totalTax: Double,
    val totalCess: Double,
    val salesByHour: Map<String, Double>
)

data class GSTSummaryReport(
    val totalCGST: Double,
    val totalSGST: Double,
    val totalIGST: Double,
    val gstByRate: Map<String, GstRateBreakdown>
)

data class GstRateBreakdown(
    val rate: Double,
    val taxableAmount: Double,
    val cgst: Double,
    val sgst: Double,
    val igst: Double
)

data class TopSellingItem(
    val itemName: String,
    val quantity: Int,
    val revenue: Double
)