package com.warriortech.resb.data.local.dao

import androidx.room.*
import com.warriortech.resb.data.local.entity.SyncStatus
import com.warriortech.resb.data.local.entity.TableEntity
import com.warriortech.resb.model.Area
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {
    @Query("SELECT * FROM tables")
    fun getAllTables(): Flow<List<TableEntity>>

    @Query("SELECT * FROM tables WHERE table_id = :id")
    suspend fun getTableById(id: Long): TableEntity?
    
    @Query("SELECT * FROM tables WHERE area_id = :section")
    fun getTablesBySection(section: Long): Flow<List<TableEntity>>
    
    @Query("SELECT * FROM tables WHERE table_status = :status")
    fun getTablesByStatus(status: String): Flow<List<TableEntity>>
    
    @Query("SELECT * FROM tables WHERE is_ac = :section AND table_status = :status")
    fun getTablesBySectionAndStatus(section: String, status: String): Flow<List<TableEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTable(table: TableEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTables(tables: List<TableEntity>)
    
    @Update
    suspend fun updateTable(table: TableEntity)
    
    @Query("UPDATE tables SET is_active = false WHERE table_id = :tableId")
    suspend fun deleteTable(tableId: Long)
    
    @Query("UPDATE tables SET table_status = :status, syncStatus = :syncStatus, lastModified = :lastModified WHERE table_id = :id")
    suspend fun updateTableStatus(id: Long, status: String, syncStatus: SyncStatus, lastModified: Long = System.currentTimeMillis())
    
    @Query("SELECT * FROM tables WHERE syncStatus = :syncStatus")
    suspend fun getTablesBySyncStatus(syncStatus: SyncStatus): List<TableEntity>
    
    @Query("UPDATE tables SET syncStatus = :newStatus WHERE table_id = :id")
    suspend fun updateTableSyncStatus(id: Long, newStatus: SyncStatus)
    
}

