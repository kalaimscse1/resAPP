package com.warriortech.resb.data.local.entity

//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import com.warriortech.resb.model.MenuItem
//
//@Entity(tableName = "menu_items")
//data class MenuItemEntity(
//    @PrimaryKey
//    val id: Long,
//    val name: String,
//    val description: String,
//    val price: Double,
//    val category: String,
//    val imageUrl: String?,
//    val syncStatus: SyncStatus = SyncStatus.SYNCED,
//    val lastModified: Long = System.currentTimeMillis()
//) {
//    companion object {
//        fun fromModel(menuItem: MenuItem, syncStatus: SyncStatus = SyncStatus.SYNCED): MenuItemEntity {
//            return MenuItemEntity(
//                id = menuItem.id,
//                name = menuItem.name,
//                description = menuItem.description,
//                price = menuItem.price,
//                category = menuItem.category,
//                imageUrl = menuItem.imageUrl,
//                syncStatus = syncStatus
//            )
//        }
//    }
//
//    fun toModel(): MenuItem {
//        return MenuItem(
//            id = this.id,
//            name = this.name,
//            description = this.description,
//            price = this.price,
//            category = this.category,
//            imageUrl = this.imageUrl
//        )
//    }
//}