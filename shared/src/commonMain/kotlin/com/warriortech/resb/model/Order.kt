package com.warriortech.resb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val id: Long? = null,
    val menuItem: MenuItemResponse,
    val quantity: Int,
    val notes: String? = null,
    val orderDetailsId: Long? = null,
    val kotNumber: Int? = null
)

@Serializable
data class Order(
    val id: Long? = null,
    val tableId: Int,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: String,
    val createdAt: Long? = null,
    val isPrinted: Boolean = false
)

@Serializable
enum class OrderStatus {
    PENDING,
    PREPARING,
    READY,
    DELIVERED,
    COMPLETED,
    CANCELLED,
    RUNNING,
    HOLD
}

@Serializable
data class OrderMaster(
    @SerialName("order_master_id") var orderMasterId: String,
    @SerialName("order_date") var orderDate: String,
    @SerialName("order_create_time") var orderCreateTime: String,
    @SerialName("order_completed_time") var orderCompletedTime: String,
    @SerialName("staff_id") var staffId: Long,
    @SerialName("is_dine_in") var isDineIn: Boolean,
    @SerialName("is_take_away") var isTakeAway: Boolean,
    @SerialName("is_delivery") var isDelivery: Boolean,
    @SerialName("table_id") var tableId: Long,
    @SerialName("no_of_person") var noOfPerson: Int,
    @SerialName("waiter_request_status") var waiterRequestStatus: Boolean,
    @SerialName("kitchen_response_status") var kitchenResponseStatus: Boolean,
    @SerialName("order_status") var orderStatus: String,
    @SerialName("is_delivered") var isDelivered: Boolean,
    @SerialName("is_merge") var isMerge: Boolean,
    @SerialName("is_active") var isActive: Long
)

@Serializable
data class OrderDetails(
    @SerialName("order_details_id") var orderDetailsId: Long,
    @SerialName("order_master_id") var orderMasterId: String,
    @SerialName("kot_number") var kotNumber: Int? = null,
    @SerialName("menu_item_id") var menuItemId: Long,
    var rate: Double,
    @SerialName("actual_rate") var actualRate: Double,
    var qty: Int,
    var total: Double,
    @SerialName("tax_id") var taxId: Long,
    @SerialName("tax_name") var taxName: String,
    @SerialName("tax_amount") var taxAmount: Double,
    @SerialName("sgst_per") var sgstPer: Double,
    var sgst: Double,
    @SerialName("cgst_per") var cgstPer: Double,
    var cgst: Double,
    @SerialName("igst_per") var igstPer: Double,
    var igst: Double,
    @SerialName("cess_per") var cessPer: Double,
    var cess: Double,
    @SerialName("cess_specific") var cessSpecific: Double,
    @SerialName("grand_total") var grandTotal: Double,
    @SerialName("prepare_status") var prepareStatus: Boolean,
    @SerialName("item_add_mode") var itemAddMode: Boolean,
    @SerialName("is_flag") var isFlag: Boolean,
    @SerialName("merge_order_nos") var mergeOrderNos: String,
    @SerialName("merge_order_tables") var mergeOrderTables: String,
    @SerialName("merge_pax") var mergePax: Int,
    @SerialName("is_active") var isActive: Long
)

@Serializable
data class OrderResponse(
    @SerialName("order_master_id") var orderMasterId: String,
    @SerialName("order_date") var orderDate: String,
    @SerialName("order_create_time") var orderCreateTime: String,
    @SerialName("order_completed_time") var orderCompletedTime: String,
    @SerialName("staff_id") var staffId: Long,
    @SerialName("staff_name") var staffName: String,
    @SerialName("is_dine_in") var isDineIn: Boolean,
    @SerialName("is_take_away") var isTakeAway: Boolean,
    @SerialName("is_delivery") var isDelivery: Boolean,
    @SerialName("table_id") var tableId: Long,
    @SerialName("table_name") var tableName: String,
    @SerialName("area_id") var areaId: Long,
    @SerialName("area_name") var areaName: String,
    @SerialName("no_of_person") var noOfPerson: Int,
    @SerialName("waiter_request_status") var waiterRequestStatus: Boolean,
    @SerialName("kitchen_response_status") var kitchenResponseStatus: Boolean,
    @SerialName("order_status") var orderStatus: String,
    @SerialName("is_delivered") var isDelivered: Boolean,
    @SerialName("is_merge") var isMerge: Boolean,
    @SerialName("is_active") var isActive: Long,
    @SerialName("kot_number") var kotNumber: Int? = null
)

@Serializable
data class OrderDetailsResponse(
    @SerialName("order_details_id") var orderDetailsId: Long,
    @SerialName("order_master_id") var orderMasterId: String,
    @SerialName("kot_number") var kotNumber: Int,
    val menuItem: MenuItemResponse,
    var rate: Double,
    @SerialName("actual_rate") var actualRate: Double,
    var qty: Int,
    var total: Double,
    @SerialName("tax_id") var taxId: Long,
    @SerialName("tax_name") var taxName: String,
    @SerialName("tax_amount") var taxAmount: Double,
    @SerialName("sgst_per") var sgstPer: Double,
    var sgst: Double,
    @SerialName("cgst_per") var cgstPer: Double,
    var cgst: Double,
    @SerialName("igst_per") var igstPer: Double,
    var igst: Double,
    @SerialName("cess_per") var cessPer: Double,
    var cess: Double,
    @SerialName("cess_specific") var cessSpecific: Double,
    @SerialName("grand_total") var grandTotal: Double,
    @SerialName("prepare_status") var prepareStatus: Boolean,
    @SerialName("item_add_mode") var itemAddMode: Boolean,
    @SerialName("is_flag") var isFlag: Boolean,
    @SerialName("merge_order_nos") var mergeOrderNos: String,
    @SerialName("merge_order_tables") var mergeOrderTables: String,
    @SerialName("merge_pax") var mergePax: Int,
    @SerialName("is_active") var isActive: Long
)
