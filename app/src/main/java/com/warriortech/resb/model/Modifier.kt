
package com.warriortech.resb.model

data class Modifier(
    val modifier_id: Long = 0,
    val name: String,
    val price: Double,
    val is_active: Boolean = true,
    val modifier_group_id: Long
)

data class ModifierGroup(
    val modifier_group_id: Long = 0,
    val name: String,
    val is_required: Boolean = false,
    val max_selection: Int = 1,
    val min_selection: Int = 0,
    val is_active: Boolean = true,
    val modifiers: List<Modifier> = emptyList()
)

data class MenuItemWithModifiers(
    val menuItem: MenuItem,
    val selectedModifiers: List<Modifier> = emptyList(),
    val totalPrice: Double = menuItem.price
)
