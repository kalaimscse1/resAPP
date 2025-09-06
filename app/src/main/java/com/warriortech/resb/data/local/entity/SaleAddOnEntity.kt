package com.warriortech.resb.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "tbl_sale_add_on",
    foreignKeys = [
        ForeignKey(entity = TblArea::class, parentColumns = ["area_id"], childColumns = ["area_id"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [
        Index(value = ["area_id"]),
    ]
)
data class TblSaleAddOn(
    @PrimaryKey(autoGenerate = true) val sale_add_on_id: Int = 0,
    val order_master_id: String?,
    val item_add_on_id: Int?,
    val menu_item_id: Int?,
    val status: Boolean?,
    val is_active: Boolean?,
    val is_synced: Boolean = false,
    val last_synced_at: Long? = null
)