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
import com.warriortech.resb.model.Table
import com.warriortech.resb.model.TblMenuItemRequest
import com.warriortech.resb.model.TblMenuItemResponse
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class SyncWorker @AssistedInject constructor(
    appContext: Context,
    workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : CoroutineWorker(appContext, workerParams) {

    @AssistedFactory
    interface Factory {
        fun create(appContext: Context, workerParams: WorkerParameters): SyncWorker
    }

    private val database: RestaurantDatabase = RestaurantDatabase.getDatabase(appContext)
    private val tableDao = database.tableDao()
    private val menuItemDao = database.menuItemDao()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Sync local changes to server
            syncPendingTablesToServer()
            syncPendingMenuItemsToServer()

            // Sync server changes to local
            syncTablesFromServer()
            syncMenuItemsFromServer()

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error during background synchronization")
            Result.retry()
        }
    }

    private suspend fun syncPendingTablesToServer() {
        try {
            val pendingTables = tableDao.getTablesBySyncStatus(SyncStatus.PENDING_SYNC)
            Timber.d("Found ${pendingTables.size} pending tables to sync")

            for (table in pendingTables) {
                try {
                    val tableRequest = table.toApiModel()
                    val response = apiService.createTable(tableRequest, sessionManager.getCompanyCode() ?: "")

                    if (response.isSuccessful) {
                        val updatedTable = table.copy(
                            is_synced = SyncStatus.SYNCED,
                            last_synced_at = System.currentTimeMillis()
                        )
                        tableDao.updateTable(updatedTable)
                        Timber.d("Table synced to server: ${table.table_name}")
                    } else {
                        Timber.w("Failed to sync table ${table.table_name}: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing table ${table.table_name} to server")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in syncPendingTablesToServer")
        }
    }

    private suspend fun syncPendingMenuItemsToServer() {
        try {
            val pendingMenuItems = menuItemDao.getMenuItemsBySyncStatus(SyncStatus.PENDING_SYNC)
            Timber.d("Found ${pendingMenuItems.size} pending menu items to sync")

            for (menuItem in pendingMenuItems) {
                try {
                    val menuItemRequest = menuItem.toApiModel()
                    val response = apiService.createMenuItem(menuItemRequest, sessionManager.getCompanyCode() ?: "")

                    if (response.isSuccessful) {
                        val updatedMenuItem = menuItem.copy(
                            is_synced = SyncStatus.SYNCED,
                            last_synced_at = System.currentTimeMillis()
                        )
                        menuItemDao.updateMenuItem(updatedMenuItem)
                        Timber.d("Menu item synced to server: ${menuItem.menu_item_name}")
                    } else {
                        Timber.w("Failed to sync menu item ${menuItem.menu_item_name}: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error syncing menu item ${menuItem.menu_item_name} to server")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in syncPendingMenuItemsToServer")
        }
    }

    private suspend fun syncTablesFromServer() {
        try {
            val response = apiService.getAllTables(sessionManager.getCompanyCode() ?: "")
            if (response.isSuccessful) {
                val remoteTables = response.body() ?: emptyList()
                Timber.d("Fetched ${remoteTables.size} tables from server")

                val localTables = remoteTables.map { remote ->
                    // Check if table exists locally
                    val existingTable = tableDao.getTableById(remote.table_id.toLong())

                    if (existingTable != null && existingTable.is_synced == SyncStatus.PENDING_SYNC) {
                        // Keep local version if it has pending changes
                        existingTable
                    } else {
                        // Use server version
                        remote.toEntity().copy(
                            is_synced = SyncStatus.SYNCED,
                            last_synced_at = System.currentTimeMillis()
                        )
                    }
                }

                tableDao.insertTables(localTables)
                Timber.d("Updated local tables from server")
            } else {
                Timber.w("Failed to fetch tables from server: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in syncTablesFromServer")
        }
    }

    private suspend fun syncMenuItemsFromServer() {
        try {
            val response = apiService.getAllMenuItems(sessionManager.getCompanyCode() ?: "")
            if (response.isSuccessful) {
                val remoteMenuItems = response.body() ?: emptyList()
                Timber.d("Fetched ${remoteMenuItems.size} menu items from server")

                val localMenuItems = remoteMenuItems.map { remote ->
                    // Check if menu item exists locally
                    val existingMenuItem = menuItemDao.getMenuItemById(remote.menu_item_id)

                    if (existingMenuItem != null && existingMenuItem.is_synced == SyncStatus.PENDING_SYNC) {
                        // Keep local version if it has pending changes
                        existingMenuItem
                    } else {
                        // Use server version
                        remote.toEntity().copy(
                            is_synced = SyncStatus.SYNCED,
                            last_synced_at = System.currentTimeMillis()
                        )
                    }
                }

                menuItemDao.insertMenuItems(localMenuItems)
                Timber.d("Updated local menu items from server")
            } else {
                Timber.w("Failed to fetch menu items from server: ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in syncMenuItemsFromServer")
        }
    }

    /** Convert remote MenuItemResponse to local entity */
    private fun TblMenuItemResponse.toEntity() = TblMenuItem(
        menu_item_id = menu_item_id.toInt(),
        menu_item_code = menu_item_code,
        menu_item_name = menu_item_name,
        menu_item_name_tamil = menu_item_name_tamil,
        menu_id = menu_id.toInt(),
        rate = rate,
        image = image,
        ac_rate = ac_rate,
        parcel_rate = parcel_rate,
        is_available = is_available,
        item_cat_id = item_cat_id.toInt(),
        parcel_charge = parcel_charge,
        tax_id = tax_id.toInt(),
        kitchen_cat_id = kitchen_cat_id.toInt(),
        stock_maintain = stock_maintain,
        rate_lock = rate_lock,
        unit_id = unit_id.toInt(),
        min_stock = min_stock.toInt(),
        hsn_code = hsn_code,
        order_by = order_by.toInt(),
        is_inventory = is_inventory.toInt(),
        is_raw = is_raw,
        cess_specific = cess_specific,
        is_favourite = is_favourite,
        is_active = is_active == 1L,
        preparation_time = preparation_time.toInt(),
        is_synced = SyncStatus.SYNCED,
        last_synced_at = System.currentTimeMillis()
    )

    /** Convert local Table entity to API model */
    private fun TblTableEntity.toApiModel() = Table(
        table_id = table_id.toLong(),
        area_id = area_id.toLong(),
        table_name = table_name,
        seating_capacity = seating_capacity,
        is_ac = is_ac,
        table_status = table_status,
        table_availability = table_availability,
        is_active = is_active
    )

    /** Convert remote Table model to local entity */
    private fun Table.toEntity() = TblTableEntity(
        table_id = table_id.toInt(),
        area_id = area_id.toInt(),
        table_name = table_name,
        seating_capacity = seating_capacity,
        is_ac = is_ac,
        table_status = table_status,
        table_availability = table_availability,
        is_active = is_active,
        is_synced = SyncStatus.SYNCED,
        last_synced_at = System.currentTimeMillis()
    )

    /** Convert local MenuItem entity to API model */
    private fun TblMenuItem.toApiModel() = TblMenuItemRequest(
        menu_item_id = menu_item_id.toLong(),
        menu_item_code = menu_item_code,
        menu_item_name = menu_item_name,
        menu_item_name_tamil = menu_item_name_tamil,
        menu_id = menu_id.toLong(),
        rate = rate,
        image = image,
        ac_rate = ac_rate,
        parcel_rate = parcel_rate,
        is_available = is_available,
        item_cat_id = item_cat_id.toLong(),
        parcel_charge = parcel_charge,
        tax_id = tax_id.toLong(),
        kitchen_cat_id = kitchen_cat_id.toLong(),
        stock_maintain = stock_maintain,
        rate_lock = rate_lock,
        unit_id = unit_id.toLong(),
        min_stock = min_stock.toLong(),
        hsn_code = hsn_code,
        order_by = order_by.toLong(),
        is_inventory = is_inventory.toLong(),
        is_raw = is_raw,
        cess_specific = cess_specific,
        is_favourite = is_favourite,
        is_active = if (is_active) 1L else 0L,
        preparation_time = preparation_time.toLong()
    )

    companion object {
        const val WORK_NAME = "SyncWorker"
    }
}