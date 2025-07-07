
package com.warriortech.resb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.warriortech.resb.model.Modifiers
import com.warriortech.resb.model.ModifierType
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.text.isNotEmpty
import kotlin.text.split
import kotlin.text.toLong

@Entity(tableName = "modifiers")
data class ModifierEntity(
    @PrimaryKey
    val modifier_id: Long,
    val modifier_name: String,
    val modifier_name_tamil: String,
    val modifier_type: String,
    val price_adjustment: Double,
    val is_available: Boolean,
    val category_ids: String, // JSON string of category IDs
    val menu_item_ids: String, // JSON string of menu item IDs
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModified: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_item_modifiers")
data class OrderItemModifierEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val order_detail_id: Long,
    val modifier_id: Long,
    val modifier_name: String,
    val price_adjustment: Double,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

// Extension functions
fun ModifierEntity.toModel(): Modifiers {
    return Modifiers(
        modifier_id = this.modifier_id,
        modifier_name = this.modifier_name,
        modifier_name_tamil = this.modifier_name_tamil,
        modifier_type = ModifierType.valueOf(this.modifier_type),
        price_adjustment = this.price_adjustment,
        is_available = this.is_available,
        category_ids = if (category_ids.isNotEmpty()) category_ids.split(",").map { it.toLong() } else emptyList(),
        menu_item_ids = if (menu_item_ids.isNotEmpty()) menu_item_ids.split(",").map { it.toLong() } else emptyList()
    )
}

fun Modifiers.toEntity(): ModifierEntity {
    return ModifierEntity(
        modifier_id = this.modifier_id,
        modifier_name = this.modifier_name,
        modifier_name_tamil = this.modifier_name_tamil,
        modifier_type = this.modifier_type.name,
        price_adjustment = this.price_adjustment,
        is_available = this.is_available,
        category_ids = this.category_ids.joinToString(","),
        menu_item_ids = this.menu_item_ids.joinToString(",")
    )
}
