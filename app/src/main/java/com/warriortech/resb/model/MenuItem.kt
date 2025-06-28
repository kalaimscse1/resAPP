package com.warriortech.resb.model

data class MenuItem(
    val menu_item_id: Long,
    val menu_item_name: String,
    val menu_item_name_tamil:String,
    val item_cat_id:Long,
    val item_cat_name:String,
    val rate:Double,
    val ac_rate:Double,
    val parcel_rate:Double,
    val parcel_charge:Double,
    val tax_id:Long,
    val tax_name:String,
    val tax_percentage:String,
    val kitchen_cat_id:Long,
    val kitchen_cat_name:String,
    val stock_maintain:String,
    val rate_lock:String,
    val unit_id:Long,
    val unit_name:String,
    val min_stock:Long,
    val hsn_code:String,
    val order_by:Long,
    val is_inventory:Long,
    val is_raw:String,
    val is_available: String,
    val image: String? = null,
    var qty: Int? = 0
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
