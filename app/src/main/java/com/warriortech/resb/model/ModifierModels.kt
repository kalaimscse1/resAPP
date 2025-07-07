
package com.warriortech.resb.model

data class Modifiers(
    val modifier_id: Long,
    val modifier_name: String,
    val modifier_name_tamil: String = "",
    val modifier_type: ModifierType,
    val price_adjustment: Double = 0.0,
    val is_available: Boolean = true,
    val category_ids: List<Long> = emptyList(), // Which categories this modifier applies to
    val menu_item_ids: List<Long> = emptyList() // Specific items this modifier applies to
)

enum class ModifierType {
    ADDITION,    // Add something (extra cheese)
    REMOVAL,     // Remove something (no onions)
    SUBSTITUTION // Replace something (almond milk instead of regular)
}

data class OrderItemModifier(
    val id: Long = 0,
    val order_detail_id: Long,
    val modifier_id: Long,
    val modifier_name: String,
    val price_adjustment: Double
)

data class MenuItemWithModifiers(
    val menuItem: MenuItem,
    val availableModifiers: List<Modifiers>
)

data class OrderItemWithModifiers(
    val orderItem: TblOrderDetailsResponse,
    val modifiers: List<OrderItemModifier>
)
