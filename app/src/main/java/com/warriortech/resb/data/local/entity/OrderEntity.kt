package com.warriortech.resb.data.local.entity

//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import com.warriortech.resb.model.Order
//
//@Entity(tableName = "orders")
//data class OrderEntity(
//    @PrimaryKey
//    val id: Long = 0,
//    val tableId: Long,
//    val waiterId: Long,
//    val status: String,
//    val kotNumber: String,
//    val totalAmount: Double,
//    val createdAt: String,
//    val syncStatus: SyncStatus = SyncStatus.PENDING_SYNC,
//    val lastModified: Long = System.currentTimeMillis(),
//    val tempId: String? = null // Used for locally created orders that don't have a server ID yet
//) {
//    companion object {
//        fun fromModel(order: Order, syncStatus: SyncStatus = SyncStatus.SYNCED): OrderEntity {
//            return OrderEntity(
//                id = order.id,
//                tableId = order.tableId,
//                waiterId = order.waiterId,
//                status = order.status,
//                kotNumber = order.kotNumber,
//                totalAmount = order.totalAmount,
//                createdAt = order.createdAt,
//                syncStatus = syncStatus
//            )
//        }
//
//        fun createLocalOrder(
//            tableId: Long,
//            waiterId: Long,
//            kotNumber: String,
//            status: String = "pending"
//        ): OrderEntity {
//            return OrderEntity(
//                id = 0, // Will be updated when synced
//                tableId = tableId,
//                waiterId = waiterId,
//                status = status,
//                kotNumber = kotNumber,
//                totalAmount = 0.0, // Will be calculated later
//                createdAt = System.currentTimeMillis().toString(),
//                syncStatus = SyncStatus.PENDING_SYNC,
//                tempId = "local_${System.currentTimeMillis()}_${(Math.random() * 1000).toInt()}"
//            )
//        }
//    }
//
//    fun toModel(): Order {
//        return Order(
//            id = this.id,
//            tableId = this.tableId,
//            waiterId = this.waiterId,
//            status = this.status,
//            kotNumber = this.kotNumber,
//            totalAmount = this.totalAmount,
//            createdAt = this.createdAt,
//            updatedAt = this.lastModified.toString()
//        )
//    }
//}