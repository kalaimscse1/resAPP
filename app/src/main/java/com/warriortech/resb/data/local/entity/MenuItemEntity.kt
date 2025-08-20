package com.warriortech.resb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.TblMenuItemResponse

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey
    var menu_item_id: Long,
    var menu_item_code:String,
    var menu_item_name:String,
    var menu_item_name_tamil:String,
    var menu_id:Long,
    var menu_name:String,
    var item_cat_id:Long,
    var item_cat_name:String,
    var image:String?=null,
    var rate:Double,
    var ac_rate:Double,
    var parcel_rate:Double,
    var parcel_charge:Double,
    var tax_id:Long,
    var tax_name:String,
    var tax_percentage:String,
    var kitchen_cat_id:Long,
    var kitchen_cat_name:String,
    var is_available:String,
    var is_favourite:Boolean,
    var stock_maintain:String,
    var rate_lock:String,
    var unit_id:Long,
    var unit_name:String,
    var min_stock:Long,
    var hsn_code:String,
    var order_by:Long,
    var is_inventory:Long,
    var is_raw:String,
    val cess_per:String,
    val cess_specific: Double,
    var is_active:Long,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModified: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromModel(menuItem: TblMenuItemResponse, syncStatus: SyncStatus = SyncStatus.SYNCED): MenuItemEntity {
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
                cess_specific = menuItem.cess_specific,
                is_favourite = menuItem.is_favourite,
                menu_item_code = menuItem.menu_item_code,
                menu_id = menuItem.menu_id,
                menu_name = menuItem.menu_name,
                is_active = menuItem.is_active)
        }
    }

    fun toModel(): TblMenuItemResponse {
        return TblMenuItemResponse(
            menu_item_id = this.menu_item_id,
            menu_item_name = this.menu_item_name,
            menu_item_name_tamil = this.menu_item_name_tamil,
            rate = this.rate,
            item_cat_name = this.item_cat_name,
            image = this.image.toString(),
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
            cess_specific = this.cess_specific,
            is_favourite = this.is_favourite,
            menu_item_code = this.menu_item_code,
            menu_id = this.menu_id,
            menu_name = this.menu_name,
            is_active = this.is_active,
        )
    }
}