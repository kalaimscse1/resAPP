package com.warriortech.resb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Table(
    @SerialName("table_id") val tableId: Long,
    @SerialName("area_id") val areaId: Long,
    @SerialName("area_name") val areaName: String,
    @SerialName("table_name") val tableName: String,
    @SerialName("seating_capacity") val seatingCapacity: Int,
    @SerialName("is_ac") val isAc: String,
    @SerialName("table_status") val tableStatus: String,
    @SerialName("table_availability") val tableAvailability: String,
    @SerialName("is_active") val isActive: Boolean
)

@Serializable
data class TableRequest(
    @SerialName("table_id") val tableId: Long,
    @SerialName("area_id") val areaId: Long,
    @SerialName("table_name") val tableName: String,
    @SerialName("seating_capacity") val seatingCapacity: Int,
    @SerialName("is_ac") val isAc: String,
    @SerialName("table_status") val tableStatus: String,
    @SerialName("table_availability") val tableAvailability: String,
    @SerialName("is_active") val isActive: Boolean
)

@Serializable
data class TableStatusResponse(
    @SerialName("table_id") var tableId: Long,
    @SerialName("area_id") var areaId: Long,
    @SerialName("area_name") var areaName: String,
    @SerialName("table_name") var tableName: String,
    @SerialName("seating_capacity") var seatingCapacity: Long,
    @SerialName("is_ac") var isAc: String,
    @SerialName("table_status") var tableStatus: String,
    @SerialName("table_availability") var tableAvailability: String,
    @SerialName("is_active") var isActive: Boolean,
    var grandTotal: Double,
    @SerialName("staff_name") var staffName: String,
    @SerialName("order_time") var orderTime: String
)
