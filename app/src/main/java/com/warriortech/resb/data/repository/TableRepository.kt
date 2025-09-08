package com.warriortech.resb.data.repository

import com.warriortech.resb.data.local.dao.TableDao
import com.warriortech.resb.data.local.entity.SyncStatus
import com.warriortech.resb.data.local.entity.TblTableEntity
import com.warriortech.resb.model.Area
import com.warriortech.resb.model.Table
import com.warriortech.resb.model.TableStatusResponse
import com.warriortech.resb.model.TblTable
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableRepository @Inject constructor(
    private val tableDao: TableDao,
    private val apiService: ApiService,
    networkMonitor: NetworkMonitor,
    private val sessionManager: SessionManager
) : OfflineFirstRepository(networkMonitor) {

    private val connectionState = networkMonitor.isOnline

    // Offline-first approach: Always return local data immediately, sync in background
    suspend fun getAllTables(): Flow<List<Table>> = flow {
        try {
            val  response  = apiService.getAllTables(sessionManager.getCompanyCode()?:"")
            if (response.isSuccessful){
                emit(response.body()!!)
            }else {
                throw Exception("Failed to fetch table: ${response.message()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getActiveTables(): Flow<List<TableStatusResponse>> = flow {
        try {
            val  response  = apiService.getActiveTables(sessionManager.getCompanyCode()?:"")
            if (response.isSuccessful){
                emit(response.body()!!)
            }else {
                throw Exception("Failed to fetch table: ${response.message()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }
    suspend fun deleteTable(tableId: Int) {
        val response = apiService.deleteTable(tableId,sessionManager.getCompanyCode()?:"")
        if (!response.isSuccessful) {
            throw Exception("Failed to delete table: ${response.message()}")
        }
    }
    suspend fun getAllAreas(): List<Area> {
        return if (isOnline()) {
            safeApiCall(
                apiCall = { apiService.getAllAreas(sessionManager.getCompanyCode()?:"").body()!! }
            ) ?: emptyList()
        } else {
            // Return cached areas or empty list if offline
            emptyList()
        }
    }

    fun getTablesBySection(section: Long): Flow<List<TableStatusResponse>> =flow {
        try {
            val response = apiService.getTablesBySection(section,sessionManager.getCompanyCode()?:"")
            if (response.isSuccessful) {

            emit(response.body()!!)
            } else {
                throw Exception("Failed to fetch tables: ${response.message()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateTableStatus(tableId: Long, status: String): Boolean {
        return try {
            // Always update local first
            tableDao.updateTableStatus(tableId, status, SyncStatus.PENDING_SYNC)

            // Try to sync with remote if online
            if (isOnline()) {
                val success = safeApiCall(
                    apiCall = { apiService.updateTableStatus(tableId, status,sessionManager.getCompanyCode()?:"") }
                ) != null

                if (success) {
                    // Mark as synced if successful
                    tableDao.updateTableSyncStatus(tableId, SyncStatus.SYNCED)
                }
                success
            } else {
                // Return true for offline - will sync later
                true
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating table status")
            false
        }
    }

    private suspend fun syncTablesFromRemote() {
        safeApiCall(
            onSuccess = { remoteTables: List<Table> ->
                withContext(Dispatchers.IO) {
                    val entities = remoteTables.map { TblTableEntity(
                        table_id = it.table_id.toInt(),
                        table_name = it.table_name,
                        seating_capacity = it.seating_capacity,
                        is_ac = it.is_ac,
                        table_status = it.table_status,
                        area_id = it.area_id.toInt(),
                        table_availability = it.table_availability,
                        is_active = it.is_active,
                    ) }
                    tableDao.insertTables(entities)
                }
            },
            apiCall = { apiService.getAllTables(sessionManager.getCompanyCode()?:"").body()!! }
        )
    }

    suspend fun forceSyncAllTables() {
        if (isOnline()) {
            syncTablesFromRemote()
        }
    }

    suspend fun getstatus(tableId: Long):String{
        val data =apiService.getTablesByStatus(tableId,sessionManager.getCompanyCode()?:"")
        return data.is_ac
    }
    suspend fun insertTable(table: TblTable) {
        safeApiCall {
            // Convert TblTable to TblTableEntity for database storage
            val entity = TblTableEntity(
                table_id = table.table_id.toInt(),
                area_id = table.area_id.toInt(),
                table_name = table.table_name,
                seating_capacity = table.seating_capacity,
                is_ac = table.is_ac,
                table_status = table.table_status,
                table_availability = table.table_availability,
                is_active = table.is_active,
                is_synced = SyncStatus.PENDING_SYNC,
                last_synced_at = null
            )
            tableDao.insertTable(entity)
            if (isOnline()) {
                // Sync with remote if online
                apiService.createTable(table,sessionManager.getCompanyCode()?:"")
            }
        }
    }

    // Get tables that need to be synced
    suspend fun getPendingSyncTables() = tableDao.getTablesBySyncStatus(SyncStatus.PENDING_SYNC)
    suspend fun updateTable(table: TblTable) {
        safeApiCall {
            // Convert TblTable to TblTableEntity for database storage
            val entity = TblTableEntity(
                table_id = table.table_id.toInt(),
                area_id = table.area_id.toInt(),
                table_name = table.table_name,
                seating_capacity = table.seating_capacity,
                is_ac = table.is_ac,
                table_status = table.table_status,
                table_availability = table.table_availability,
                is_active = table.is_active,
                is_synced = SyncStatus.PENDING_SYNC,
                last_synced_at = null
            )
            tableDao.updateTable(entity)
            if (isOnline()) {
                // Sync with remote if online
                apiService.updateTable(table.table_id, table,sessionManager.getCompanyCode()?:"")
            }
        }
    }
    suspend fun deleteTable(lng: Long) {
        safeApiCall {
            tableDao.deleteTable(lng)
            if (isOnline()) {
                // Sync with remote if online
                apiService.deleteTable(lng,sessionManager.getCompanyCode()?:"")
            }
        }
    }

    suspend fun getTableById(tableId: Long): Table? {
        val entity = tableDao.getTableById(tableId)
        return entity?.let {
            Table(
                table_id = it.table_id.toLong(),
                area_id = it.area_id?.toLong() ?: 0L,
                area_name = "",
                table_name = it.table_name ?: "",
                seating_capacity = it.seating_capacity ?: 0,
                is_ac = it.is_ac ?: "",
                table_status = it.table_status ?: "",
                table_availability = it.table_availability ?: "",
                is_active = it.is_active ?: false
            )
        }
    }
}