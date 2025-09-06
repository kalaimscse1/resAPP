package com.warriortech.resb.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.warriortech.resb.data.local.RestaurantDatabase
import com.warriortech.resb.data.local.entity.SyncStatus
import com.warriortech.resb.data.local.entity.TblMenuItem
import com.warriortech.resb.data.local.entity.TblTable
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.Table
import com.warriortech.resb.model.TblMenuItemResponse
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : CoroutineWorker(appContext, workerParams) {

private val database = RestaurantDatabase.getDatabase(appContext)
    private val tableDao = database.tableDao()
    private val menuItemDao = database.menuItemDao()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Sync tables
            syncTables()

            // Sync menu items
            syncMenuItems()

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
                val response = apiService.updateTableStatus(table.table_id, table.table_status,sessionManager.getCompanyCode()?:"")

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
            val remoteTables = apiService.getAllTables(sessionManager.getCompanyCode()?:"").body()!!
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
            val remoteMenuItems = apiService.getAllMenuItems(sessionManager.getCompanyCode()?:"").body()!!
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

    private fun Table.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): TblTable {
        return TblTable(
            table_id = this.table_id,
            area_id = this.area_id,
            table_name = this.table_name,
            seating_capacity = this.seating_capacity,
            is_ac = this.is_ac,
            table_status = this.table_status,
            table_availability = this.table_availability,
            is_active = this.is_active,
            is_synced = syncStatus == SyncStatus.SYNCED,
            last_synced_at = if (syncStatus == SyncStatus.SYNCED) System.currentTimeMillis() else null
        )
    }
    
    private fun TblMenuItemResponse.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): TblMenuItem {
        return TblMenuItem(
            menu_item_id = this.menu_item_id,
            menu_item_code = this.menu_item_code,
            menu_item_name = this.menu_item_name,
            menu_item_name_tamil = this.menu_item_name_tamil,
            menu_id = this.menu_id.toInt(),
            item_cat_id = this.item_cat_id.toInt(),
            image = this.image,
            rate = this.rate,
            ac_rate = this.ac_rate,
            parcel_rate = this.parcel_rate,
            parcel_charge = this.parcel_charge,
            tax_id = this.tax_id.toInt(),
            kitchen_cat_id = this.kitchen_cat_id.toInt(),
            is_available = this.is_available,
            is_favourite = this.is_favourite,
            stock_maintain = this.stock_maintain,
            preparation_time = this.preparation_time.toInt(),
            rate_lock = this.rate_lock,
            unit_id = this.unit_id.toInt(),
            min_stock = this.min_stock.toInt(),
            hsn_code = this.hsn_code,
            order_by = this.order_by.toInt(),
            is_inventory = this.is_inventory.toInt(),
            is_raw = this.is_raw,
            cess_specific = this.cess_specific,
            is_active = this.is_active == 1L,
            is_synced = syncStatus == SyncStatus.SYNCED,
            last_synced_at = if (syncStatus == SyncStatus.SYNCED) System.currentTimeMillis() else null
        )
    }

    companion object {
        const val WORK_NAME = "SyncWorker"
    }
}