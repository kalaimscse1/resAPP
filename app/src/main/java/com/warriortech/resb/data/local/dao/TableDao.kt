package com.warriortech.resb.data.local.dao

import androidx.room.*
import com.warriortech.resb.data.local.entity.SyncStatus
import com.warriortech.resb.data.local.entity.TblTable
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {
    @Query("SELECT * FROM tbl_table")
    fun getAllTables(): Flow<List<TblTable>>

    @Query("SELECT * FROM tbl_table WHERE table_id = :id")
    suspend fun getTableById(id: Long): TblTable?
    
    @Query("SELECT * FROM tbl_table WHERE area_id = :section")
    fun getTablesBySection(section: Long): Flow<List<TblTable>>
    
    @Query("SELECT * FROM tbl_table WHERE table_status = :status")
    fun getTablesByStatus(status: String): Flow<List<TblTable>>
    
    @Query("SELECT * FROM tbl_table WHERE is_ac = :section AND table_status = :status")
    fun getTablesBySectionAndStatus(section: String, status: String): Flow<List<TblTable>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTable(table: TblTable): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTables(tables: List<TblTable>)
    
    @Update
    suspend fun updateTable(table: TblTable)
    
    @Query("UPDATE tbl_table SET is_active = false WHERE table_id = :tableId")
    suspend fun deleteTable(tableId: Long)
    
    @Query("UPDATE tbl_table SET table_status = :status, is_synced = :syncStatus, last_synced_at = :lastModified WHERE table_id = :id")
    suspend fun updateTableStatus(id: Long, status: String, syncStatus: SyncStatus, lastModified: Long = System.currentTimeMillis())
    
    @Query("SELECT * FROM tbl_table WHERE is_synced = :syncStatus")
    suspend fun getTablesBySyncStatus(syncStatus: SyncStatus): List<TblTable>
    
    @Query("UPDATE tbl_table SET is_synced = :newStatus WHERE table_id = :id")
    suspend fun updateTableSyncStatus(id: Long, newStatus: SyncStatus)
    
}

