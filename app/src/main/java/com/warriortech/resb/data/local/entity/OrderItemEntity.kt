package com.warriortech.resb.data.local.entity

//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.Index
//import androidx.room.PrimaryKey
//import com.warriortech.resb.model.OrderItem
//
//@Entity(
//    tableName = "order_items",
//    foreignKeys = [
//        ForeignKey(
//            entity = OrderEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["orderId"],
//            onDelete = ForeignKey.CASCADE
//        ),
//        ForeignKey(
//            entity = MenuItemEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["menuItemId"],
//            onDelete = ForeignKey.NO_ACTION
//        )
//    ],
//    indices = [
//        Index("orderId"),
//        Index("menuItemId")
//    ]
//)
//data class OrderItemEntity(
//    @PrimaryKey(autoGenerate = true)
//    val id: Long = 0,
//    val orderId: Long,
//    val menuItemId: Long,
//    val quantity: Int,
//    val price: Double,
//    val syncStatus: SyncStatus = SyncStatus.PENDING_SYNC,
//    val tempOrderId: String? = null // For referencing local orders
//) {
//    companion object {
//        fun fromModel(orderItem: OrderItem, syncStatus: SyncStatus = SyncStatus.SYNCED): OrderItemEntity {
//            return OrderItemEntity(
//                id = orderItem.id,
//                orderId = orderItem.orderId,
//                menuItemId = orderItem.menuItemId,
//                quantity = orderItem.quantity,
//                price = orderItem.price,
//                syncStatus = syncStatus
//            )
//        }
//
//        fun createLocalOrderItem(
//            orderId: Long,
//            menuItemId: Long,
//            quantity: Int,
//            price: Double,
//            tempOrderId: String? = null
//        ): OrderItemEntity {
//            return OrderItemEntity(
//                orderId = orderId,
//                menuItemId = menuItemId,
//                quantity = quantity,
//                price = price,
//                syncStatus = SyncStatus.PENDING_SYNC,
//                tempOrderId = tempOrderId
//            )
//        }
//    }
//
//    fun toModel(): OrderItem {
//        return OrderItem(
//            id = this.id,
//            orderId = this.orderId,
//            menuItemId = this.menuItemId,
//            quantity = this.quantity,
//            price = this.price,
//            menuItemName = menuItem.name
//        )
//    }
//}