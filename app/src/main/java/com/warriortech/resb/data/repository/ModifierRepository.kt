
package com.warriortech.resb.data.repository

import com.warriortech.resb.data.local.dao.ModifierDao
import com.warriortech.resb.data.local.dao.OrderItemModifierDao
import com.warriortech.resb.data.local.entity.OrderItemModifierEntity
import com.warriortech.resb.data.local.entity.SyncStatus
import com.warriortech.resb.data.local.entity.toModel
import com.warriortech.resb.data.local.entity.toEntity
import com.warriortech.resb.model.ModifierType
import com.warriortech.resb.model.Modifiers
import com.warriortech.resb.model.OrderItemModifier
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map
import kotlin.let

@Singleton
class ModifierRepository @Inject constructor(
    private val modifierDao: ModifierDao,
    private val orderItemModifierDao: OrderItemModifierDao,
    private val apiService: ApiService,
    networkMonitor: NetworkMonitor
) : OfflineFirstRepository(networkMonitor) {

    fun getAllAvailableModifiers(): Flow<List<Modifiers>> {
        return modifierDao.getAllAvailableModifiers()
            .map { entities -> entities.map { it.toModel() } }
    }

    fun getModifiersByCategory(categoryId: Long): Flow<List<Modifiers>> {
        return modifierDao.getModifiersByCategory(categoryId)
            .map { entities -> entities.map { it.toModel() } }
    }

    fun getModifiersByMenuItem(menuItemId: Long): Flow<List<Modifiers>> {
        return modifierDao.getModifiersByMenuItem(menuItemId)
            .map { entities -> entities.map { it.toModel() } }
    }

    suspend fun getModifierById(id: Long): Modifiers? {
        return modifierDao.getModifierById(id)?.toModel()
    }

    suspend fun saveOrderItemModifiers(orderDetailId: Long, modifiers: List<Modifiers>) {
        val modifierEntities = modifiers.map { modifier ->
            OrderItemModifierEntity(
                order_detail_id = orderDetailId,
                modifier_id = modifier.modifier_id,
                modifier_name = modifier.modifier_name,
                price_adjustment = modifier.price_adjustment
            )
        }
        orderItemModifierDao.insertOrderItemModifiers(modifierEntities)
    }

    suspend fun getOrderItemModifiers(orderDetailId: Long): List<OrderItemModifier> {
        return orderItemModifierDao.getModifiersByOrderDetailId(orderDetailId).map { entity ->
            OrderItemModifier(
                id = entity.id,
                order_detail_id = entity.order_detail_id,
                modifier_id = entity.modifier_id,
                modifier_name = entity.modifier_name,
                price_adjustment = entity.price_adjustment
            )
        }
    }

    suspend fun deleteOrderItemModifiers(orderDetailId: Long) {
        orderItemModifierDao.deleteModifiersByOrderDetailId(orderDetailId)
    }

    suspend fun syncData() {
        try {
            val response = apiService.getAllModifiers(SessionManager.getCompanyCode()?:"")
            if (response.isSuccessful) {
                response.body()?.let { modifiers ->
                    val modifierEntities = modifiers.map { it.toEntity() }
                    modifierDao.insertModifiers(modifierEntities)
                }
            }
        } catch (e: Exception) {
            // Handle sync error - will retry later
        }
    }

    suspend fun createModifier(modifier: Modifiers): Result<Modifiers> {
        return try {
            if (isOnline()) {
                val response = apiService.createModifier(modifier,SessionManager.getCompanyCode()?:"")
                if (response.isSuccessful) {
                    response.body()?.let { createdModifier ->
                        modifierDao.insertModifier(createdModifier.toEntity())
                        Result.success(createdModifier)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("API Error: ${response.code()}"))
                }
            } else {
                // Store locally with pending sync status
                val localModifier = modifier.copy(modifier_id = System.currentTimeMillis())
                val entity = localModifier.toEntity().copy(syncStatus = SyncStatus.PENDING_SYNC)
                modifierDao.insertModifier(entity)
                Result.success(localModifier)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateModifier(modifier: Modifiers): Result<Modifiers> {
        return try {
            if (isOnline()) {
                val response = apiService.updateModifier(modifier.modifier_id, modifier,SessionManager.getCompanyCode()?:"")
                if (response.isSuccessful) {
                    response.body()?.let { updatedModifier ->
                        modifierDao.updateModifier(updatedModifier.toEntity())
                        Result.success(updatedModifier)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("API Error: ${response.code()}"))
                }
            } else {
                val entity = modifier.toEntity().copy(syncStatus = SyncStatus.PENDING_UPDATE)
                modifierDao.updateModifier(entity)
                Result.success(modifier)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteModifier(modifierId: Long): Result<Unit> {
        return try {
            if (isOnline()) {
                val response = apiService.deleteModifier(modifierId,SessionManager.getCompanyCode()?:"")
                if (response.isSuccessful) {
                    modifierDao.getModifierById(modifierId)?.let { entity ->
                        modifierDao.deleteModifier(entity)
                    }
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("API Error: ${response.code()}"))
                }
            } else {
                modifierDao.getModifierById(modifierId)?.let { entity ->
                    val updatedEntity = entity.copy(syncStatus = SyncStatus.PENDING_DELETE)
                    modifierDao.updateModifier(updatedEntity)
                }
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Predefined modifiers for common use cases
    suspend fun initializeDefaultModifiers() {
        val defaultModifiers = listOf(
            Modifiers(
                modifier_id = 1,
                modifier_name = "Less Sugar",
                modifier_name_tamil = "குறைந்த சர்க்கரை",
                modifier_type = ModifierType.REMOVAL,
                price_adjustment = 0.0
            ),
            Modifiers(
                modifier_id = 2,
                modifier_name = "No Sugar",
                modifier_name_tamil = "சர்க்கரை இல்லாமல்",
                modifier_type = ModifierType.REMOVAL,
                price_adjustment = 0.0
            ),
            Modifiers(
                modifier_id = 3,
                modifier_name = "Extra Strong",
                modifier_name_tamil = "கூடுதல் வலிமை",
                modifier_type = ModifierType.ADDITION,
                price_adjustment = 5.0
            ),
            Modifiers(
                modifier_id = 4,
                modifier_name = "Less Milk",
                modifier_name_tamil = "குறைந்த பால்",
                modifier_type = ModifierType.REMOVAL,
                price_adjustment = 0.0
            ),
            Modifiers(
                modifier_id = 5,
                modifier_name = "Extra Hot",
                modifier_name_tamil = "அதிக சூடு",
                modifier_type = ModifierType.ADDITION,
                price_adjustment = 0.0
            )
        )

        val entities = defaultModifiers.map { it.toEntity() }
        modifierDao.insertModifiers(entities)
    }
}
