package com.warriortech.resb.data.local.dao

//import androidx.room.*
//import com.warriortech.resb.data.local.entity.OrderItemEntity
//import com.warriortech.resb.data.local.entity.SyncStatus
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface OrderItemDao {
//    @Query("SELECT * FROM order_items")
//    fun getAllOrderItems(): Flow<List<OrderItemEntity>>
//
//    @Query("SELECT * FROM order_items WHERE id = :id")
//    suspend fun getOrderItemById(id: Long): OrderItemEntity?
//
//    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
//    fun getOrderItemsByOrderId(orderId: Long): Flow<List<OrderItemEntity>>
//
//    @Query("SELECT * FROM order_items WHERE tempOrderId = :tempOrderId")
//    suspend fun getOrderItemsByTempOrderId(tempOrderId: String): List<OrderItemEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertOrderItem(orderItem: OrderItemEntity): Long
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertOrderItems(orderItems: List<OrderItemEntity>)
//
//    @Update
//    suspend fun updateOrderItem(orderItem: OrderItemEntity)
//
//    @Delete
//    suspend fun deleteOrderItem(orderItem: OrderItemEntity)
//
//    @Query("SELECT * FROM order_items WHERE syncStatus = :syncStatus")
//    suspend fun getOrderItemsBySyncStatus(syncStatus: SyncStatus): List<OrderItemEntity>
//
//    @Query("UPDATE order_items SET syncStatus = :newStatus WHERE id = :id")
//    suspend fun updateOrderItemSyncStatus(id: Long, newStatus: SyncStatus)
//
//    @Query("UPDATE order_items SET orderId = :serverId, tempOrderId = NULL WHERE tempOrderId = :tempOrderId")
//    suspend fun updateOrderItemsWithOrderId(tempOrderId: String, serverId: Long)
//}