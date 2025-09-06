package com.warriortech.resb.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.warriortech.resb.data.local.entity.*

@Dao
interface TblOrderDetailsDao {
    @Query("SELECT * FROM tbl_order_details")

    fun getAll(): Flow<List<TblOrderDetails>>

    @Query("SELECT * FROM tbl_order_details WHERE order_details_id = :id")
    suspend fun getById(id: Int): TblOrderDetails?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TblOrderDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TblOrderDetails>)

    @Update
    suspend fun update(item: TblOrderDetails)

    @Delete
    suspend fun delete(item: TblOrderDetails)

    @Query("SELECT * FROM tbl_order_details WHERE is_synced = 0")
    suspend fun getUnsynced(): List<TblOrderDetails>
}