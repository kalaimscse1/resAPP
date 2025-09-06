package com.warriortech.resb.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.warriortech.resb.model.Table
import com.warriortech.resb.model.TblTable

@Entity(
    tableName = "tbl_table",
    foreignKeys = [
        ForeignKey(entity = TblTax::class, parentColumns = ["tax_id"], childColumns = ["tax_id"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [
        Index(value = ["tax_id"]),
    ]
)
data class TblTable(
    @PrimaryKey(autoGenerate = true) val table_id: Int = 0,
    val area_id: Int?,
    val table_name: String?,
    val seating_capacity: Int?,
    val is_ac: String?,
    val table_status: String?,
    val table_availability: String?,
    val is_active: Boolean?,
    val is_synced: Boolean = false,
    val last_synced_at: Long? = null
)

enum class SyncStatus {
    SYNCED,           // Data is synchronized with the server
    PENDING_SYNC,     // Local changes need to be synced to server
    SYNC_FAILED,
    PENDING_DELETE,
    PENDING_UPDATE,// Failed to sync with server
}