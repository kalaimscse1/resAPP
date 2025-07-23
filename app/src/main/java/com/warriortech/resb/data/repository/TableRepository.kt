package com.warriortech.resb.data.repository
import com.warriortech.resb.data.local.dao.TableDao
import com.warriortech.resb.data.local.entity.SyncStatus
import com.warriortech.resb.data.local.entity.TableEntity
import com.warriortech.resb.model.Area
import com.warriortech.resb.model.Table
import com.warriortech.resb.model.TblTable
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.util.ConnectionState
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
    networkMonitor: NetworkMonitor
) : OfflineFirstRepository(networkMonitor) {

    private val connectionState = networkMonitor.isOnline

    // Offline-first approach: Always return local data immediately, sync in background
    suspend fun getAllTables(): Flow<List<Table>> = flow {
        try {
            val  response  = apiService.getAllTables(SessionManager.getCompanyCode()?:"")
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
        val response = apiService.deleteTable(tableId,SessionManager.getCompanyCode()?:"")
        if (!response.isSuccessful) {
            throw Exception("Failed to delete table: ${response.message()}")
        }
    }
    suspend fun getAllAreas(): List<Area> {
        return if (isOnline()) {
            safeApiCall(
                apiCall = { apiService.getAllAreas(SessionManager.getCompanyCode()?:"").body()!! }
            ) ?: emptyList()
        } else {
            // Return cached areas or empty list if offline
            emptyList()
        }
    }

    fun getTablesBySection(section: Long): Flow<List<Table>> =flow {
        try {
            val response = apiService.getTablesBySection(section,SessionManager.getCompanyCode()?:"")
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
                    apiCall = { apiService.updateTableStatus(tableId, status,SessionManager.getCompanyCode()?:"") }
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
                    val entities = remoteTables.map { TableEntity(
                        table_id = it.table_id,
                        table_name = it.table_name,
                        seating_capacity = it.seating_capacity,
                        is_ac = it.is_ac,
                        table_status = it.table_status,
                        area_id = it.area_id,
                        table_availabiltiy = it.table_availability,
                        is_active = it.is_active,
                        syncStatus = SyncStatus.SYNCED
                    ) }
                    tableDao.insertTables(entities)
                }
            },
            apiCall = { apiService.getAllTables(SessionManager.getCompanyCode()?:"").body()!! }
        )
    }

    suspend fun forceSyncAllTables() {
        if (isOnline()) {
            syncTablesFromRemote()
        }
    }

    suspend fun getstatus(tableId: Long):String{
        val data =apiService.getTablesByStatus(tableId,SessionManager.getCompanyCode()?:"")
        return data.is_ac
    }
    suspend fun insertTable(table: TblTable) {
        safeApiCall {
            val entity = table.toEntity()
            tableDao.insertTable(entity)
            if (isOnline()) {
                // Sync with remote if online
                apiService.createTable(table,SessionManager.getCompanyCode()?:"")
            }
        }
    }

    // Get tables that need to be synced
    suspend fun getPendingSyncTables() = tableDao.getTablesBySyncStatus(SyncStatus.PENDING_SYNC)
    suspend fun updateTable(table: TblTable) {
        safeApiCall {
            val entity = table.toEntity()
            tableDao.updateTable(entity)
            if (isOnline()) {
                // Sync with remote if online
                apiService.updateTable(table.table_id, table,SessionManager.getCompanyCode()?:"")
            }
        }
    }
    suspend fun deleteTable(lng: Long) {
        safeApiCall {
            tableDao.deleteTable(lng)
            if (isOnline()) {
                // Sync with remote if online
                apiService.deleteTable(lng,SessionManager.getCompanyCode()?:"")
            }
        }
    }

    suspend fun getTableById(tableId: Long): Table? {
        return tableDao.getTableById(tableId)?.toModel() as Table?
    }
}

// Extension functions for entity conversion
private fun TblTable.toEntity() = TableEntity(
    table_id = table_id,
    table_name = table_name,
    table_status = table_status,
    area_id = area_id,
    syncStatus = SyncStatus.SYNCED,
    seating_capacity = seating_capacity,
    is_ac = is_ac,
    table_availabiltiy = table_availability,
    is_active = is_active
)

private fun TableEntity.toModel() = TblTable(
    table_id = table_id,
    table_name = table_name,
    table_status = table_status,
    area_id = area_id,
    seating_capacity = seating_capacity,
    is_ac = is_ac,
    table_availability = table_availabiltiy,
    is_active = is_active
)