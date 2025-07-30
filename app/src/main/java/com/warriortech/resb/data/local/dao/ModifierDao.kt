package com.warriortech.resb.data.local.dao

import androidx.room.*
import com.warriortech.resb.data.local.entity.ModifierEntity
import com.warriortech.resb.data.local.entity.OrderItemModifierEntity
import com.warriortech.resb.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ModifierDao {
    @Query("SELECT * FROM modifiers WHERE is_active = 1")
    fun getAllAvailableModifiers(): Flow<List<ModifierEntity>>

    @Query("SELECT * FROM modifiers WHERE add_on_id = :id")
    suspend fun getModifierById(id: Long): ModifierEntity?

    @Query("SELECT * FROM modifiers WHERE add_on_id LIKE '%' || :categoryId || '%' AND is_active = 1")
    fun getModifiersByCategory(categoryId: Long): Flow<List<ModifierEntity>>

    @Query("SELECT * FROM modifiers WHERE item_cat_id LIKE '%' || :menuItemId || '%' AND is_active = 1")
    fun getModifiersByMenuItem(menuItemId: Long): Flow<List<ModifierEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModifier(modifier: ModifierEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModifiers(modifiers: List<ModifierEntity>)

    @Update
    suspend fun updateModifier(modifier: ModifierEntity)

    @Delete
    suspend fun deleteModifier(modifier: ModifierEntity)

    @Query("SELECT * FROM modifiers WHERE syncStatus = :syncStatus")
    suspend fun getModifiersBySyncStatus(syncStatus: SyncStatus): List<ModifierEntity>
}

@Dao
interface OrderItemModifierDao {
    @Query("SELECT * FROM order_item_modifiers WHERE order_detail_id = :orderDetailId")
    suspend fun getModifiersByOrderDetailId(orderDetailId: Long): List<OrderItemModifierEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItemModifier(modifier: OrderItemModifierEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItemModifiers(modifiers: List<OrderItemModifierEntity>)

    @Delete
    suspend fun deleteOrderItemModifier(modifier: OrderItemModifierEntity)

    @Query("DELETE FROM order_item_modifiers WHERE order_detail_id = :orderDetailId")
    suspend fun deleteModifiersByOrderDetailId(orderDetailId: Long)
}
