package com.warriortech.resb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.warriortech.resb.model.MenuItem

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey
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
    val cess_per:String,
    val cess_specific: Double,
    val image: String? = null,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModified: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromModel(menuItem: MenuItem, syncStatus: SyncStatus = SyncStatus.SYNCED): MenuItemEntity {
            return MenuItemEntity(
                menu_item_id = menuItem.menu_item_id,
                menu_item_name = menuItem.menu_item_name,
                menu_item_name_tamil = menuItem.menu_item_name_tamil,
                rate = menuItem.rate,
                item_cat_name = menuItem.item_cat_name,
                image = menuItem.image,
                syncStatus = syncStatus,
                ac_rate = menuItem.ac_rate,
                parcel_rate = menuItem.parcel_rate,
                is_available = menuItem.is_available,
                item_cat_id = menuItem.item_cat_id,
                parcel_charge = menuItem.parcel_charge,
                tax_id = menuItem.tax_id,
                tax_name = menuItem.tax_name,
                tax_percentage = menuItem.tax_percentage,
                kitchen_cat_id = menuItem.kitchen_cat_id,
                kitchen_cat_name = menuItem.kitchen_cat_name,
                stock_maintain = menuItem.stock_maintain,
                rate_lock = menuItem.rate_lock,
                unit_id = menuItem.unit_id,
                unit_name = menuItem.unit_name,
                min_stock = menuItem.min_stock,
                hsn_code = menuItem.hsn_code,
                order_by = menuItem.order_by,
                is_inventory = menuItem.is_inventory,
                is_raw = menuItem.is_raw,
                cess_per = menuItem.cess_per,
                cess_specific = menuItem.cess_specific
            )
        }
    }

    fun toModel(): MenuItem {
        return MenuItem(
            menu_item_id = this.menu_item_id,
            menu_item_name = this.menu_item_name,
            menu_item_name_tamil = this.menu_item_name_tamil,
            rate = this.rate,
            item_cat_name = this.item_cat_name,
            image = this.image,
            ac_rate = this.ac_rate,
            is_available = this.is_available,
            parcel_rate = this.parcel_rate,
            item_cat_id = this.item_cat_id,
            parcel_charge = this.parcel_charge,
            tax_id = this.tax_id,
            tax_name = this.tax_name,
            tax_percentage = this.tax_percentage,
            kitchen_cat_id = this.kitchen_cat_id,
            kitchen_cat_name = this.kitchen_cat_name,
            stock_maintain = this.stock_maintain,
            rate_lock = this.rate_lock,
            unit_id = this.unit_id,
            unit_name = this.unit_name,
            min_stock = this.min_stock,
            hsn_code = this.hsn_code,
            order_by = this.order_by,
            is_inventory = this.is_inventory,
            is_raw = this.is_raw,
            cess_per = this.cess_per,
            cess_specific = this.cess_specific
        )
    }
}