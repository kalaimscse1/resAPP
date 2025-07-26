package com.warriortech.resb.model

data class KitchenKOT(
    val kotId: Int,
    val kotNumber: String,
    val tableNumber: String,
    val orderType: String, // DINE_IN, TAKEAWAY, DELIVERY
    val waiterName: String?,
    val orderTime: String,
    val status: KOTStatus,
    val items: List<KitchenKOTItem>,
    val orderId: Long?
)

data class KitchenKOTItem(
    val itemName: String,
    val quantity: Int,
    val category: String,
    val specialInstructions: String? = null,
    val modifiers: List<String> = emptyList()
)

enum class KOTStatus {
    PENDING,
    IN_PROGRESS,
    READY,
    SERVED
}

data class KOTStatusUpdate(
    val kotId: Int,
    val status: KOTStatus
)

data class KitchenKOTResponse(
    val success: Boolean,
    val message: String,
    val data: List<KitchenKOT>?
)

data class KOTUpdateResponse(
    val success: Boolean,
    val message: String,
    val kotId: Int?
)

data class KitchenCategory(
    val kitchen_cat_id: Long,
    val kitchen_cat_name: String,
    val is_active: Boolean
)