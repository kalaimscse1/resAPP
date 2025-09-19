package com.warriortech.resb.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.warriortech.resb.data.local.RestaurantDatabase
import com.warriortech.resb.data.local.dao.MenuItemDao
import com.warriortech.resb.data.local.dao.TableDao
import com.warriortech.resb.data.local.entity.SyncStatus
import com.warriortech.resb.data.local.entity.TblMenuItem
import com.warriortech.resb.data.local.entity.TblTableEntity
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.Table
import com.warriortech.resb.model.TblMenuItemResponse
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val sessionManager: SessionManager,
    private val menuDao: MenuItemDao,
    private val tableDao: TableDao
) : CoroutineWorker(appContext, workerParams) {

    @dagger.assisted.AssistedFactory
    interface Factory {
        fun create(appContext: Context, workerParams: WorkerParameters): SyncWorker
    }

    val database: RestaurantDatabase = RestaurantDatabase.getDatabase(context = appContext)


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            syncTables()
            syncMenuItems()
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing data")
            Result.retry()
        }
    }

    private suspend fun syncTables() {
        // 1️⃣ Sync pending local tables to server
        val pendingTables = tableDao.getTablesBySyncStatus(SyncStatus.PENDING_SYNC)
        for (table in pendingTables) {
            try {
                val response = apiService.updateTableStatus(
                    table.table_id.toLong(),
                    table.table_status.toString(),
                    sessionManager.getCompanyCode() ?: ""
                )
                val newStatus = if (response.isSuccessful) SyncStatus.SYNCED else SyncStatus.SYNC_FAILED
                tableDao.updateTableSyncStatus(table.table_id.toLong(), newStatus)
            } catch (e: Exception) {
                tableDao.updateTableSyncStatus(table.table_id.toLong(), SyncStatus.SYNC_FAILED)
                Timber.e(e, "Failed to sync table ${table.table_id}")
            }
        }

        // 2️⃣ Fetch remote tables and update local database
        try {
            val remoteTables = apiService.getAllTables(sessionManager.getCompanyCode() ?: "").body() ?: emptyList()
            val localTables = remoteTables.map { remote ->
                tableDao.getTableById(remote.table_id)?.let { local ->
                    if (local.is_synced != SyncStatus.SYNCED) local else remote.toEntity()
                } ?: remote.toEntity()
            }
            tableDao.insertTables(localTables)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch tables from server")
        }
    }

    private suspend fun syncMenuItems() {
        try {
            val remoteMenuItems = apiService.getAllMenuItems(sessionManager.getCompanyCode() ?: "").body() ?: emptyList()
            val localMenuItems = remoteMenuItems.map { remote ->
                menuDao.getMenuItemById(remote.menu_item_id)?.let { local ->
                    if (local.is_synced != SyncStatus.SYNCED) local else remote.toEntity()
                } ?: remote.toEntity()
            }
            menuDao.insertMenuItems(localMenuItems)
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch menu items from server")
        }
    }

    /** Convert remote Table model to local entity */
    private fun Table.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): TblTableEntity {
        return TblTableEntity(
            table_id = this.table_id.toInt(),
            area_id = this.area_id.toInt(),
            table_name = this.table_name,
            seating_capacity = this.seating_capacity,
            is_ac = this.is_ac,
            table_status = this.table_status,
            table_availability = this.table_availability,
            is_active = this.is_active,
            is_synced = SyncStatus.SYNCED,
            last_synced_at = if (syncStatus == SyncStatus.SYNCED) System.currentTimeMillis() else null
        )
    }

    /** Convert remote MenuItemResponse to local entity */
    private fun TblMenuItemResponse.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): TblMenuItem {
        return TblMenuItem(
            menu_item_id = this.menu_item_id.toInt(),
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
            is_synced = SyncStatus.SYNCED,
            last_synced_at = if (syncStatus == SyncStatus.SYNCED) System.currentTimeMillis() else null
        )
    }

    companion object {
        const val WORK_NAME = "SyncWorker"
    }
}