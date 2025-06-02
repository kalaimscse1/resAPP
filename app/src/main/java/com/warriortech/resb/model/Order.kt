package com.warriortech.resb.model

data class OrderItem(
    val id: Long? = null,
    val menuItemId: Long,
    val menuItemName: String,
    val quantity: Int,
    val price: Double,
    val notes: String? = null
)

/**
 * Order model that matches the server response format
 * Updated to use Int for ids and Long for timestamp to match backend
 */
data class Order(
    val id: Long? = null,
    val tableId: Int,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: String,  // Use string since we receive string from backend
    val createdAt: Long? = null,
    val isPrinted: Boolean = false
) {
    fun getOrderStatus(): OrderStatus {
        return try {
            OrderStatus.valueOf(status)
        } catch (e: Exception) {
            OrderStatus.PENDING // Default
        }
    }
}
enum class OrderStatus {
    PENDING,   // Just created
    PREPARING, // In the kitchen
    READY,     // Ready to serve
    DELIVERED, // Delivered to the table
    COMPLETED, // Payment received
    CANCELLED  // Order cancelled
}

/**
 * CreateOrderRequest model for sending to the backend
 * Updated to use Int for tableId to match backend
 */
data class CreateOrderRequest(
    val tableId: Int,
    val items: List<OrderItem>
)

/**
 * PrintResponse model for KOT print response
 */
data class PrintResponse(
    val message: String,
    val orderId: Long
)
