package com.warriortech.resb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.warriortech.resb.model.Modifiers
@Entity(tableName = "tbl_tax")
data class TblTax(
    @PrimaryKey(autoGenerate = true) val tax_id: Int = 0,
    val tax_name: String?,
    val tax_percentage: String?,
    val cess_percentage: String?,
    val is_active: Boolean?,
    val is_synced: Boolean = false,
    val last_synced_at: Long? = null
)

//@Entity(tableName = "modifiers")
//data class ModifierEntity(
//    @PrimaryKey
//    val add_on_id: Long,
//    val item_cat_id: Long,
//    val add_on_name: String,
//    val add_on_price: Double,
//    val is_active: Boolean,
//    val syncStatus: SyncStatus = SyncStatus.SYNCED,
//    val lastModified: Long = System.currentTimeMillis()
//)
//
//@Entity(tableName = "order_item_modifiers")
//data class OrderItemModifierEntity(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    val order_detail_id: Long,
//    val modifier_id: Long,
//    val modifier_name: String,
//    val price_adjustment: Double,
//    val syncStatus: SyncStatus = SyncStatus.SYNCED
//)
//
//// Extension functions
//fun ModifierEntity.toModel(): Modifiers {
//    return Modifiers(
//        add_on_id = this.add_on_id,
//        item_cat_id = this.item_cat_id,
//        add_on_name = this.add_on_name,
//        add_on_price = this.add_on_price,
//        is_active = this.is_active
//    )
//}
//
//fun Modifiers.toEntity(): ModifierEntity {
//    return ModifierEntity(
//        add_on_id = this.add_on_id,
//        item_cat_id = this.item_cat_id,
//        add_on_name = this.add_on_name,
//        add_on_price = this.add_on_price,
//        is_active = this.is_active
//    )
//}
