package com.warriortech.resb.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.TblMenuItemResponse

@Entity(
    tableName = "tbl_menu_item",
    foreignKeys = [
        ForeignKey(entity = TblKitchenCategory::class, parentColumns = ["kitchen_cat_id"], childColumns = ["kitchen_cat_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TblMenu::class, parentColumns = ["menu_id"], childColumns = ["menu_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TblTax::class, parentColumns = ["tax_id"], childColumns = ["tax_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TblUnit::class, parentColumns = ["unit_id"], childColumns = ["unit_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TblMenuItem::class, parentColumns = ["menu_item_id"], childColumns = ["menu_item_id"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [
        Index(value = ["kitchen_cat_id"]),
        Index(value = ["menu_id"]),
        Index(value = ["tax_id"]),
        Index(value = ["unit_id"]),
        Index(value = ["menu_item_id"]),
    ]
)
data class TblMenuItem(
    @PrimaryKey(autoGenerate = true) val menu_item_id: Int = 0,
    val menu_item_code: String?,
    val menu_item_name: String?,
    val menu_item_name_tamil: String?,
    val menu_id: Int?,
    val item_cat_id: Int?,
    val image: String?,
    val rate: Double?,
    val ac_rate: Double?,
    val parcel_rate: Double?,
    val parcel_charge: Double?,
    val tax_id: Int?,
    val kitchen_cat_id: Int?,
    val is_available: String?,
    val is_favourite: Boolean?,
    val stock_maintain: String?,
    val preparation_time: Int?,
    val rate_lock: String?,
    val unit_id: Int?,
    val min_stock: Int?,
    val hsn_code: String?,
    val order_by: Int?,
    val is_inventory: Int?,
    val is_raw: String?,
    val cess_specific: Double?,
    val is_active: Boolean?,
    val is_synced: Boolean = false,
    val last_synced_at: Long? = null
)