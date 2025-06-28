package com.warriortech.resb.data.repository

import com.warriortech.resb.data.local.dao.TableDao
import com.warriortech.resb.data.local.entity.SyncStatus
import com.warriortech.resb.data.local.entity.TableEntity
import com.warriortech.resb.model.Area
import com.warriortech.resb.model.Table
import com.warriortech.resb.network.ApiService
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
    fun getAllTables(): Flow<List<Table>> {
        return tableDao.getAllTables()
            .map { entities -> entities.map { it.toModel() } }
            .onStart {
                // Try to sync in background when flow starts
                if (isOnline()) {
                    syncTablesFromRemote()
                }
            }
    }

    suspend fun getAllAreas(): List<Area> {
        return if (isOnline()) {
            safeApiCall(
                apiCall = { apiService.getAllAreas() }
            ) ?: emptyList()
        } else {
            // Return cached areas or empty list if offline
            emptyList()
        }
    }

    fun getTablesBySection(section: Long): Flow<List<Table>> {
        return tableDao.getTablesBySection(section)
            .map { entities -> entities.map { it.toModel() } }
            .onStart {
                if (isOnline()) {
                    syncTablesFromRemote()
                }
            }
    }

    suspend fun updateTableStatus(tableId: Long, status: String): Boolean {
        return try {
            // Always update local first
            tableDao.updateTableStatus(tableId, status, SyncStatus.PENDING_SYNC)

            // Try to sync with remote if online
            if (isOnline()) {
                val success = safeApiCall(
                    apiCall = { apiService.updateTableStatus(tableId, status) }
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
                    val entities = remoteTables.map { it.toEntity() }
                    tableDao.insertTables(entities)
                }
            },
            apiCall = { apiService.getAllTables() }
        )
    }

    suspend fun forceSyncAllTables() {
        if (isOnline()) {
            syncTablesFromRemote()
        }
    }

    suspend fun getstatus(tableId: Long):String{
        val data =apiService.getTablesByStatus(tableId)
        return data.is_ac
    }

    // Get tables that need to be synced
    suspend fun getPendingSyncTables() = tableDao.getTablesBySyncStatus(SyncStatus.PENDING_SYNC)
}

// Extension functions for entity conversion
private fun Table.toEntity() = TableEntity(
    table_id = table_id,
    table_name = table_name,
    table_status = table_status,
    area_id = area_id,
    syncStatus = SyncStatus.SYNCED,
    seating_capacity = seating_capacity,
    is_ac = is_ac,
    area_name = area_name,
    table_availabiltiy = table_availability
)

private fun TableEntity.toModel() = Table(
    table_id = table_id,
    table_name = table_name,
    table_status = table_status,
    area_id = area_id,
    area_name = area_name,
    seating_capacity = seating_capacity,
    is_ac = is_ac,
    table_availability = table_availabiltiy
)