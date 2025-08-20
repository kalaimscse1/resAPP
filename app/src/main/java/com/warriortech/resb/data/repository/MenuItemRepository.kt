package com.warriortech.resb.data.repository

import com.warriortech.resb.data.local.dao.MenuItemDao
import com.warriortech.resb.data.local.entity.MenuItemEntity
import com.warriortech.resb.data.local.entity.SyncStatus
import com.warriortech.resb.model.Menu
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.TblMenuItemRequest
import com.warriortech.resb.model.TblMenuItemResponse
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.screens.SettingsModule
import com.warriortech.resb.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuItemRepository @Inject constructor(
    private val menuItemDao: MenuItemDao,
    private val apiService: ApiService,
    networkMonitor: NetworkMonitor,
    private val sessionManager: SessionManager
) : OfflineFirstRepository(networkMonitor) {

    fun getAllMenuItems(): Flow<List<TblMenuItemResponse>> {
        return menuItemDao.getAllMenuItems()
            .map { entities -> entities.map { it.toModel() } }
            .onStart {
                if (isOnline()) {
                    syncMenuItemsFromRemote()
                }
            }
    }
    suspend fun getMenuItems(category: String? = null): Flow<Result<List<TblMenuItemResponse>>> = flow {
        try {
            getAllMenuItems()
            val response = apiService.getMenuItems(sessionManager.getCompanyCode()?:"")

            if (response.isSuccessful) {
                val menuItems = response.body()
                if (menuItems != null && category !="FAVOURITES") {
                    // If category is provided, filter by category
                    val filteredItems = if (category != null) {
                        menuItems.filter { it.item_cat_name == category }
                    } else {
                        menuItems
                    }
                    emit(Result.success(filteredItems))
                } else if (menuItems != null){
                    val filteredItems =
                        menuItems.filter { it.is_favourite == true }
                    emit(Result.success(filteredItems))
                }
                else
                    {
                        emit(Result.failure(Exception("No menu items data received")))
                    }

            } else {
                emit(Result.failure(Exception("Error fetching menu items: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getMenuItemsByCategory(categoryId: Long): Flow<List<TblMenuItemResponse>> {
        return menuItemDao.getMenuItemsByCategory(categoryId.toString())
            .map { entities -> entities.map { it.toModel() } }
    }

    suspend fun getMenus(): List<Menu>{
        val response = apiService.getAllMenus(sessionManager.getCompanyCode()?:"")
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch menus: ${response.message()}")
        }
    }
    suspend fun getMenuItemById(id: Long): TblMenuItemResponse? {
        return menuItemDao.getMenuItemById(id)?.toModel()
    }

    private suspend fun syncMenuItemsFromRemote() {
        safeApiCall(
            onSuccess = { remoteItems: List<TblMenuItemResponse> ->
                withContext(Dispatchers.IO) {
                    val entities = remoteItems.map { it.toEntity() }
                    menuItemDao.insertMenuItems(entities)
                }
            },
            apiCall = { apiService.getAllMenuItems(sessionManager.getCompanyCode()?:"").body()!! }
        )
    }

    suspend fun forceSyncAllMenuItems() {
        if (isOnline()) {
            syncMenuItemsFromRemote()
        }
    }


    suspend fun insertMenuItem(menuItem: TblMenuItemRequest): TblMenuItemResponse {
        val response = apiService.createMenuItem(menuItem,sessionManager.getCompanyCode()?:"")
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to create menu item")
        } else {
            throw Exception("Failed to create menu item: ${response.message()}")
        }
    }

    suspend fun updateMenuItem(menuItem: TblMenuItemRequest): Int {
        val response = apiService.updateMenuItem(menuItem.menu_item_id, menuItem,sessionManager.getCompanyCode()?:"")
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to update menu item")
        } else {
            throw Exception("Failed to update menu item: ${response.message()}")
        }
    }

    suspend fun deleteMenuItem(menuItemId: Int): Response<Any> {
        val response = apiService.deleteMenuItem(menuItemId,sessionManager.getCompanyCode()?:"")
        if (!response.isSuccessful) {
            throw Exception("Failed to delete menu item: ${response.message()}")
        }
        return response
    }
}

// Extension functions
private fun TblMenuItemResponse.toEntity() = MenuItemEntity(
    menu_item_id = menu_item_id,
    menu_item_name = menu_item_name,
    menu_item_name_tamil = menu_item_name_tamil,
    rate = rate,
    item_cat_name = item_cat_name,
    image = image,
    syncStatus = SyncStatus.SYNCED,
    ac_rate = ac_rate,
    parcel_rate = parcel_rate,
    is_available = is_available,
    item_cat_id = item_cat_id,
    parcel_charge = parcel_charge,
    tax_id = tax_id,
    tax_name = tax_name,
    tax_percentage = tax_percentage,
    kitchen_cat_id = kitchen_cat_id,
    kitchen_cat_name = kitchen_cat_name,
    stock_maintain = stock_maintain,
    rate_lock = rate_lock,
    unit_id = unit_id,
    unit_name = unit_name,
    min_stock = min_stock,
    hsn_code = hsn_code,
    order_by = order_by,
    is_inventory = is_inventory,
    is_raw = is_raw,
    cess_specific = cess_specific,
    cess_per = cess_per,
    is_favourite = is_favourite,
    menu_item_code = menu_item_code,
    menu_id = menu_id,
    menu_name = menu_name,
    is_active = is_active,
)

private fun MenuItemEntity.toModel() = TblMenuItemResponse(
    menu_item_id = this.menu_item_id,
    menu_item_name = this.menu_item_name,
    menu_item_name_tamil = this.menu_item_name_tamil,
    rate = this.rate,
    item_cat_name = this.item_cat_name,
    image = this.image.toString(),
    ac_rate = this.ac_rate,
    is_available = this.is_available,
    parcel_rate = this.parcel_rate,
    item_cat_id = this.item_cat_id,
    parcel_charge = this.parcel_charge,
    tax_id = this.tax_id,
    tax_name = this.tax_name,
    tax_percentage = this.tax_percentage,
    kitchen_cat_id = this.kitchen_cat_id,
    kitchen_cat_name = this.kitchen_cat_name,
    stock_maintain = this.stock_maintain,
    rate_lock = this.rate_lock,
    unit_id = this.unit_id,
    unit_name = this.unit_name,
    min_stock = this.min_stock,
    hsn_code = this.hsn_code,
    order_by = this.order_by,
    is_inventory = this.is_inventory,
    is_raw = this.is_raw,
    cess_per = this.cess_per,
    cess_specific = this.cess_specific,
    is_favourite = this.is_favourite,
    menu_item_code = this.menu_item_code,
    menu_id = this.menu_id,
    menu_name = this.menu_name,
    is_active = this.is_active
)