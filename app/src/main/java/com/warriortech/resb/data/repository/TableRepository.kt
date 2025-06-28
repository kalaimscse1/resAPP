package com.warriortech.resb.data.repository

import android.util.Log
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

class TableRepository(
    private val tableDao: TableDao,
    private val apiService: ApiService,
    private val networkMonitor: NetworkMonitor
) {
    // Get the current connection state as a flow
    private val connectionState = networkMonitor.isOnline


    // Get all tables, preferring remote data when online
    fun getAllTables(): Flow<List<Table>> {
        return combine(
            tableDao.getAllTables(),
            connectionState
        ) { tables, connectionState ->
            // If online, try to fetch from API first
            if (connectionState == ConnectionState.Available) {
                try {
                    val remoteTables = withContext(Dispatchers.IO) {
                        apiService.getAllTables()
                    }
                    
                    // Update local database with remote data
                    withContext(Dispatchers.IO) {
                        val entities = remoteTables.map { it.toEntity() }
                        tableDao.insertTables(entities)
                    }
                    
                    // Return the tables from API
                    return@combine remoteTables
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching tables from API, falling back to local data")
                }
            }
            
            // If offline or API call failed, return local data
            tables.map { it.toModel() }
        }
    }
    suspend fun getAllAreas():List<Area>{
        return apiService.getAllAreas()
    }

    suspend fun getstatus(tableId: Long):String{
        val data =apiService.getTablesByStatus(tableId)
        return data.is_ac
    }
//    fun getAllAreas(): Flow<List<Area>> {
//        return combine(
//            tableDao.getAllTables(),
//            connectionState
//        ) { tables, connectionState ->
//            // If online, try to fetch from API first
////            if (connectionState == ConnectionState.Available) {
////                try {
//                    val remoteTables = withContext(Dispatchers.IO) {
//                        apiService.getAllAreas()
//                    }
//
//                    // Update local database with remote data
////                    withContext(Dispatchers.IO) {
////                        val entities = remoteTables.map { it.toEntity() }
////                        tableDao.insertTables(entities)
////                    }
//
//                    // Return the tables from API
//                    return@combine remoteTables
////                } catch (e: Exception) {
////                    Timber.e(e, "Error fetching tables from API, falling back to local data")
////                }
////            }
//
//            // If offline or API call failed, return local data
////            tables.map { it.toModel() }
//        }
//    }
//    fun getAllAreas():Flow<List<Area>> {
//        try {
//            return apiService.getAllAreas()
//        }
//        catch (e: Exception) {
//            Log.e("MenuViewModel", "Error fetching menu", e)
//        }
//    }
//        combine(
//            tableDao.getAllTables(),
//            connectionState
//        ) { tables, connectionState ->
//            // If online, try to fetch from API first
//            if (connectionState == ConnectionState.Available) {
//                try {
//                    val remoteTables = withContext(Dispatchers.IO) {
//                        apiService.getAllAreas()
//                    }
//
//                    // Update local database with remote data
//                    withContext(Dispatchers.IO) {
//                        val entities = remoteTables.map { it.toEntity() }
//                        tableDao.insertTables(entities)
//                    }
//
//                    // Return the tables from API
//                    return@combine remoteTables
//                } catch (e: Exception) {
//                    Timber.e(e, "Error fetching tables from API, falling back to local data")
//                }
//            }
//
//            // If offline or API call failed, return local data
//            tables.map { it.toModel() }
//        }
//    }
    
    // Get tables by section, same pattern as getAllTables
    fun getTablesBySection(section: Long): Flow<List<Table>> {
        return combine(
            tableDao.getTablesBySection(section),
            connectionState
        ) { tables, connectionState ->
            if (connectionState == ConnectionState.Available) {
                try {
                    val remoteTables = withContext(Dispatchers.IO) {
                        apiService.getTablesBySection(section)
                    }
                    
                    withContext(Dispatchers.IO) {
                        val entities = remoteTables.map { it.toEntity() }
                        tableDao.insertTables(entities)
                    }
                    
                    return@combine remoteTables
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching tables by section from API, falling back to local data")
                }
            }
            
            tables.map { it.toModel() }
        }
    }
    
    // Update table status - works offline and syncs when online
    suspend fun updateTableStatus(tableId: Long, status: String): Table? {
        // First update the local database with the new status
        val syncStatus = if (connectionState.first() == ConnectionState.Available) {
            SyncStatus.SYNCED
        } else {
            SyncStatus.PENDING_SYNC
        }
        
        tableDao.updateTableStatus(tableId, status, syncStatus)
        
        // If online, also update the remote server immediately
        if (connectionState.first() == ConnectionState.Available) {
            try {
                val response = apiService.updateTableStatus(tableId, status)
                if (!response.isSuccessful) {
                    // API call failed, mark for retry
                    tableDao.updateTableSyncStatus(tableId, SyncStatus.PENDING_SYNC)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error updating table status on API")
                // API call failed, mark for retry
                tableDao.updateTableSyncStatus(tableId, SyncStatus.PENDING_SYNC)
            }
        }
        
        // Return the updated table
        return tableDao.getTableById(tableId)?.toModel()
    }
    
    // Extension function to convert API Table to TableEntity
    private fun Table.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): TableEntity {
        return TableEntity(
            table_id = this.table_id,
            table_name = this.table_name,
            seating_capacity = this.seating_capacity,
            is_ac = this.is_ac,
            table_status = this.table_status,
            syncStatus = syncStatus,
            area_id = this.area_id,
            area_name = this.area_name,
            table_availabiltiy = this.table_availability
        )
    }
}