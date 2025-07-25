package com.warriortech.resb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.warriortech.resb.model.Table
import com.warriortech.resb.model.TblTable

@Entity(tableName = "tables")
data class TableEntity(
    @PrimaryKey
    val table_id: Long,
    val table_name: String,
    val seating_capacity: Int,
    val is_ac: String,
    val table_status: String,
    val area_id:Long,
    val table_availabiltiy:String,
    val is_active: Boolean ,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModified: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromModel(table: TblTable, syncStatus: SyncStatus = SyncStatus.SYNCED): TableEntity {
            return TableEntity(
                table_id = table.table_id,
                table_name = table.table_name,
                seating_capacity = table.seating_capacity,
                is_ac = table.is_ac,
                table_status = table.table_status,
                syncStatus = syncStatus,
                area_id = table.area_id,
                table_availabiltiy = table.table_availability,
                is_active = true
            )
        }
    }
    
    fun toModel(): TblTable {
        return TblTable(
            table_id = this.table_id,
            table_name = this.table_name,
            seating_capacity = this.seating_capacity,
            is_ac = this.is_ac,
            table_status = this.table_status,
            area_id = this.area_id,
            table_availability = this.table_availabiltiy,
            is_active = this.is_active
        )
    }
}

enum class SyncStatus {
    SYNCED,           // Data is synchronized with the server
    PENDING_SYNC,     // Local changes need to be synced to server
    SYNC_FAILED,
    PENDING_DELETE,
    PENDING_UPDATE,// Failed to sync with server
}