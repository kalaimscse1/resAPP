package com.warriortech.resb.data.local.dao

//import androidx.room.*
//import com.warriortech.resb.data.local.entity.OrderEntity
//import com.warriortech.resb.data.local.entity.SyncStatus
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface OrderDao {
//    @Query("SELECT * FROM orders")
//    fun getAllOrders(): Flow<List<OrderEntity>>
//
//    @Query("SELECT * FROM orders WHERE id = :id")
//    suspend fun getOrderById(id: Long): OrderEntity?
//
//    @Query("SELECT * FROM orders WHERE tableId = :tableId")
//    fun getOrdersByTableId(tableId: Long): Flow<List<OrderEntity>>
//
//    @Query("SELECT * FROM orders WHERE tableId = :tableId AND (status = 'pending' OR status = 'in_progress')")
//    suspend fun getActiveOrderByTableId(tableId: Long): OrderEntity?
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertOrder(order: OrderEntity): Long
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertOrders(orders: List<OrderEntity>)
//
//    @Update
//    suspend fun updateOrder(order: OrderEntity)
//
//    @Delete
//    suspend fun deleteOrder(order: OrderEntity)
//
//    @Query("UPDATE orders SET status = :status, syncStatus = :syncStatus, lastModified = :lastModified WHERE id = :id")
//    suspend fun updateOrderStatus(id: Long, status: String, syncStatus: SyncStatus, lastModified: Long = System.currentTimeMillis())
//
//    @Query("SELECT * FROM orders WHERE syncStatus = :syncStatus")
//    suspend fun getOrdersBySyncStatus(syncStatus: SyncStatus): List<OrderEntity>
//
//    @Query("UPDATE orders SET id = :serverId, syncStatus = :syncStatus WHERE tempId = :tempId")
//    suspend fun updateOrderWithServerId(tempId: String, serverId: Long, syncStatus: SyncStatus)
//
//    @Query("UPDATE orders SET syncStatus = :newStatus WHERE id = :id")
//    suspend fun updateOrderSyncStatus(id: Long, newStatus: SyncStatus)
//
//    @Query("SELECT * FROM orders WHERE tempId = :tempId")
//    suspend fun getOrderByTempId(tempId: String): OrderEntity?
//}