package com.warriortech.resb.model

data class KotData(
    val tableId: Long,
    val tableNumber: String,
    val section: String,
    val kotNumber: String,
    val items: List<CartItem>,
    val createdAt: String
)
data class KOTRequest(
    val tableNumber: String,
    val kotId: Int?,
    val orderId: Long?,
    val waiterName: String?,
    val orderCreatedAt: String,
    val items: List<KOTItem>
)

data class KOTItem(
    val name: String,
    val quantity: Int,
    val category: String,
    val addOns: List<String>// e.g., "Extra Cheese", "No Onions"
)