package com.warriortech.resb.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.warriortech.resb.data.local.RestaurantDatabase
import com.warriortech.resb.data.local.entity.MenuItemEntity
import com.warriortech.resb.data.local.entity.SyncStatus
import com.warriortech.resb.data.local.entity.TableEntity
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.Table
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val apiService: ApiService
) : CoroutineWorker(appContext, workerParams) {

private val database = RestaurantDatabase.getDatabase(appContext)
    private val tableDao = database.tableDao()
    private val menuItemDao = database.menuItemDao()
//    private val orderDao = database.orderDao()
//    private val orderItemDao = database.orderItemDao()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Sync tables
            syncTables()

            // Sync menu items
            syncMenuItems()

            // Sync orders (most important, as they affect revenue)
//            syncOrders()

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing data with server")
            Result.retry()
        }
    }

    private suspend fun syncTables() {
        // Get local tables that need to be synced
        val pendingTables = tableDao.getTablesBySyncStatus(SyncStatus.PENDING_SYNC)

        for (table in pendingTables) {
            try {
                // Update table status on server
                val response = apiService.updateTableStatus(table.table_id, table.table_status,SessionManager.getCompanyCode()?:"")

                if (response.isSuccessful) {
                    // Mark as synced
                    tableDao.updateTableSyncStatus(table.table_id, SyncStatus.SYNCED)
                } else {
                    // Mark as failed
                    tableDao.updateTableSyncStatus(table.table_id, SyncStatus.SYNC_FAILED)
                }
            } catch (e: Exception) {
                tableDao.updateTableSyncStatus(table.table_id, SyncStatus.SYNC_FAILED)
                Timber.e(e, "Failed to sync table ${table.table_id}")
            }
        }

        // Get latest tables from server and update local cache
        try {
            val remoteTables = apiService.getAllTables(SessionManager.getCompanyCode()?:"").body()!!
            val localTables = remoteTables.map {
                tableDao.getTableById(it.table_id)?.let { localTable ->
                    // Preserve local sync status if it's pending or failed
                    if (localTable.syncStatus != SyncStatus.SYNCED) {
                        localTable
                    } else {
                        // Otherwise update with remote data
                        it.toEntity()
                    }
                } ?: it.toEntity() // If doesn't exist locally, add it
            }
            tableDao.insertTables(localTables)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch tables from server")
        }
    }

    private suspend fun syncMenuItems() {
        // Similar to tables, menu items are mostly read-only from client side
        try {
            val remoteMenuItems = apiService.getAllMenuItems(SessionManager.getCompanyCode()?:"").body()!!
            val localMenuItems = remoteMenuItems.map {
                menuItemDao.getMenuItemById(it.menu_item_id)?.let { localItem ->
                    // Preserve local sync status if it's pending or failed
                    if (localItem.syncStatus != SyncStatus.SYNCED) {
                        localItem
                    } else {
                        // Otherwise update with remote data
                        it.toEntity()
                    }
                } ?: it.toEntity() // If doesn't exist locally, add it
            }
            menuItemDao.insertMenuItems(localMenuItems)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch menu items from server")
        }
    }

    //    private suspend fun syncOrders() {
//        // Get local orders that need to be synced
//        val pendingOrders = orderDao.getOrdersBySyncStatus(SyncStatus.PENDING_SYNC)
//
//        for (order in pendingOrders) {
//            try {
//                // Check if this is a new order (tempId is not null and id is 0)
//                if (order.tempId != null && order.id == 0L) {
//                    // New order to be created on server
//                    val orderItems = orderItemDao.getOrderItemsByTempOrderId(order.tempId)
//                    val response = apiService.createOrder(order.toModel(), orderItems.map { it.toModel() })
//
//                    if (response.isSuccessful) {
//                        val createdOrder = response.body()
//                        if (createdOrder != null) {
//                            // Update local order with server ID
//                            orderDao.updateOrderWithServerId(order.tempId, createdOrder.id, SyncStatus.SYNCED)
//                            // Update order items with server order ID
//                            orderItemDao.updateOrderItemsWithOrderId(order.tempId, createdOrder.id)
//                        }
//                    }
//                } else {
//                    // Existing order to update on server
//                    val response = apiService.updateOrder(order.id, order.toModel())
//
//                    if (response.isSuccessful) {
//                        // Mark as synced
//                        orderDao.updateOrderSyncStatus(order.id, SyncStatus.SYNCED)
//                    } else {
//                        // Mark as failed
//                        orderDao.updateOrderSyncStatus(order.id, SyncStatus.SYNC_FAILED)
//                    }
//                }
//            } catch (e: Exception) {
//                orderDao.updateOrderSyncStatus(order.id, SyncStatus.SYNC_FAILED)
//                Timber.e(e, "Failed to sync order ${order.id}")
//            }
//        }
//
//        // For order items, we need to sync those that have pending status
//        val pendingOrderItems = orderItemDao.getOrderItemsBySyncStatus(SyncStatus.PENDING_SYNC)
//
//        for (orderItem in pendingOrderItems) {
//            try {
//                // Skip items with temp order IDs - they're handled in the order sync
//                if (orderItem.tempOrderId != null) continue
//
//                val response = apiService.updateOrderItem(orderItem.id, orderItem.toModel())
//
//                if (response.isSuccessful) {
//                    // Mark as synced
//                    orderItemDao.updateOrderItemSyncStatus(orderItem.id, SyncStatus.SYNCED)
//                } else {
//                    // Mark as failed
//                    orderItemDao.updateOrderItemSyncStatus(orderItem.id, SyncStatus.SYNC_FAILED)
//                }
//            } catch (e: Exception) {
//                orderItemDao.updateOrderItemSyncStatus(orderItem.id, SyncStatus.SYNC_FAILED)
//                Timber.e(e, "Failed to sync order item ${orderItem.id}")
//            }
//        }
//    }
    private fun Table.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): TableEntity {
        return TableEntity(
            table_id = this.table_id,
            table_name = this.table_name,
            seating_capacity = this.seating_capacity,
            is_ac = this.is_ac,
            table_status = this.table_status,
            syncStatus = syncStatus,
            area_id = this.area_id,
            table_availabiltiy = this.table_availability,
            is_active = true
        )
    }
    private fun MenuItem.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): MenuItemEntity {
        return return MenuItemEntity(
            menu_item_id = this.menu_item_id,
            menu_item_name = this.menu_item_name,
            menu_item_name_tamil = this.menu_item_name_tamil,
            rate = this.rate,
            item_cat_name = this.item_cat_name,
            image = this.image,
            syncStatus = syncStatus,
            ac_rate = this.ac_rate,
            parcel_rate = this.parcel_rate,
            is_available = this.is_available,
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

    companion object {
        const val WORK_NAME = "SyncWorker"
    }
}