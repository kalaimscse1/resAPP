package com.warriortech.resb.data.local.dao

//import androidx.room.*
//import com.warriortech.resb.data.local.entity.MenuItemEntity
//import com.warriortech.resb.data.local.entity.SyncStatus
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface MenuItemDao {
//    @Query("SELECT * FROM menu_items")
//    fun getAllMenuItems(): Flow<List<MenuItemEntity>>
//
//    @Query("SELECT * FROM menu_items WHERE id = :id")
//    suspend fun getMenuItemById(id: Long): MenuItemEntity?
//
//    @Query("SELECT * FROM menu_items WHERE category = :category")
//    fun getMenuItemsByCategory(category: String): Flow<List<MenuItemEntity>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertMenuItem(menuItem: MenuItemEntity): Long
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertMenuItems(menuItems: List<MenuItemEntity>)
//
//    @Update
//    suspend fun updateMenuItem(menuItem: MenuItemEntity)
//
//    @Delete
//    suspend fun deleteMenuItem(menuItem: MenuItemEntity)
//
//    @Query("SELECT * FROM menu_items WHERE syncStatus = :syncStatus")
//    suspend fun getMenuItemsBySyncStatus(syncStatus: SyncStatus): List<MenuItemEntity>
//
//    @Query("UPDATE menu_items SET syncStatus = :newStatus WHERE id = :id")
//    suspend fun updateMenuItemSyncStatus(id: Long, newStatus: SyncStatus)
//}