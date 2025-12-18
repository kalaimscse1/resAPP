package com.warriortech.resb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
    @SerialName("menu_item_id") val menuItemId: Long,
    @SerialName("menu_item_name") val menuItemName: String,
    @SerialName("menu_item_name_tamil") val menuItemNameTamil: String,
    @SerialName("item_cat_id") val itemCatId: Long,
    @SerialName("item_cat_name") val itemCatName: String,
    val rate: Double,
    @SerialName("ac_rate") val acRate: Double,
    @SerialName("parcel_rate") val parcelRate: Double,
    @SerialName("parcel_charge") val parcelCharge: Double,
    @SerialName("tax_id") val taxId: Long,
    @SerialName("tax_name") val taxName: String,
    @SerialName("tax_percentage") val taxPercentage: String,
    @SerialName("cess_per") val cessPer: String,
    @SerialName("cess_specific") val cessSpecific: Double,
    @SerialName("kitchen_cat_id") val kitchenCatId: Long,
    @SerialName("kitchen_cat_name") val kitchenCatName: String,
    @SerialName("stock_maintain") val stockMaintain: String,
    @SerialName("rate_lock") val rateLock: String,
    @SerialName("unit_id") val unitId: Long,
    @SerialName("unit_name") val unitName: String,
    @SerialName("min_stock") val minStock: Long,
    @SerialName("hsn_code") val hsnCode: String,
    @SerialName("order_by") val orderBy: Long,
    @SerialName("is_inventory") val isInventory: Long,
    @SerialName("is_raw") val isRaw: String,
    @SerialName("is_available") val isAvailable: String,
    @SerialName("is_favourite") val isFavourite: Boolean,
    val image: String = "",
    var qty: Int = 0
)

@Serializable
data class MenuCategory(
    @SerialName("item_cat_id") val itemCatId: Long,
    @SerialName("item_cat_name") val itemCatName: String,
    @SerialName("order_by") val orderBy: String,
    @SerialName("is_active") val isActive: Boolean
)

@Serializable
data class MenuItemRequest(
    @SerialName("menu_item_id") var menuItemId: Long,
    @SerialName("menu_item_code") var menuItemCode: String,
    @SerialName("menu_item_name") var menuItemName: String,
    @SerialName("menu_item_name_tamil") var menuItemNameTamil: String,
    @SerialName("menu_id") var menuId: Long,
    @SerialName("item_cat_id") var itemCatId: Long,
    var image: String,
    var rate: Double,
    @SerialName("ac_rate") var acRate: Double,
    @SerialName("parcel_rate") var parcelRate: Double,
    @SerialName("parcel_charge") var parcelCharge: Double,
    @SerialName("tax_id") var taxId: Long,
    @SerialName("kitchen_cat_id") var kitchenCatId: Long,
    @SerialName("is_available") var isAvailable: String,
    @SerialName("is_favourite") var isFavourite: Boolean,
    @SerialName("stock_maintain") var stockMaintain: String,
    @SerialName("preparation_time") var preparationTime: Long,
    @SerialName("rate_lock") var rateLock: String,
    @SerialName("unit_id") var unitId: Long,
    @SerialName("min_stock") var minStock: Long,
    @SerialName("hsn_code") var hsnCode: String,
    @SerialName("order_by") var orderBy: Long,
    @SerialName("is_inventory") var isInventory: Long,
    @SerialName("is_raw") var isRaw: String,
    @SerialName("cess_specific") var cessSpecific: Double,
    @SerialName("is_active") var isActive: Long
)

@Serializable
data class MenuItemResponse(
    @SerialName("menu_item_id") var menuItemId: Long,
    @SerialName("menu_item_code") var menuItemCode: String,
    @SerialName("menu_item_name") var menuItemName: String,
    @SerialName("menu_item_name_tamil") var menuItemNameTamil: String,
    @SerialName("menu_id") var menuId: Long,
    @SerialName("menu_name") var menuName: String,
    @SerialName("item_cat_id") var itemCatId: Long,
    @SerialName("item_cat_name") var itemCatName: String,
    var image: String,
    var rate: Double,
    @SerialName("ac_rate") var acRate: Double,
    @SerialName("parcel_rate") var parcelRate: Double,
    @SerialName("parcel_charge") var parcelCharge: Double,
    @SerialName("tax_id") var taxId: Long,
    @SerialName("tax_name") var taxName: String,
    @SerialName("tax_percentage") var taxPercentage: String,
    @SerialName("kitchen_cat_id") var kitchenCatId: Long,
    @SerialName("kitchen_cat_name") var kitchenCatName: String,
    @SerialName("is_available") var isAvailable: String,
    @SerialName("preparation_time") var preparationTime: Long,
    @SerialName("is_favourite") var isFavourite: Boolean,
    @SerialName("stock_maintain") var stockMaintain: String,
    @SerialName("rate_lock") var rateLock: String,
    @SerialName("unit_id") var unitId: Long,
    @SerialName("unit_name") var unitName: String,
    @SerialName("min_stock") var minStock: Long,
    @SerialName("hsn_code") var hsnCode: String,
    @SerialName("order_by") var orderBy: Long,
    @SerialName("is_inventory") var isInventory: Long,
    @SerialName("is_raw") var isRaw: String,
    @SerialName("cess_per") val cessPer: String,
    @SerialName("cess_specific") val cessSpecific: Double,
    @SerialName("is_active") var isActive: Long,
    var qty: Int = 0,
    @SerialName("actual_rate") var actualRate: Double = 0.0
)

@Serializable
data class Unit(
    @SerialName("unit_id") var unitId: Long,
    @SerialName("unit_name") var unitName: String,
    @SerialName("is_active") var isActive: Long
)
