package com.warriortech.resb.model

data class MenuItem(
    val menu_item_id: Long,
    val menu_item_name: String,
    val menu_item_name_tamil:String,
    val rate: Double,
    val ac_rate:Double,
    val menu_cat_name: String,
    val is_available: String,
    val image: String? = null
)


/**
 * MenuCategory model for grouping menu items
 * Updated to use Int for id to match backend
 */
data class MenuCategory(
    val id: Long,
    val name: String,
    val items: List<MenuItem> = emptyList()
)