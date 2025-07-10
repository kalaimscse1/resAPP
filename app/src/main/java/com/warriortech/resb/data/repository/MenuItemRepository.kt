package com.warriortech.resb.data.repository

import com.warriortech.resb.data.local.dao.MenuItemDao
import com.warriortech.resb.data.local.entity.MenuItemEntity
import com.warriortech.resb.data.local.entity.SyncStatus
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuItemRepository @Inject constructor(
    private val menuItemDao: MenuItemDao,
    private val apiService: ApiService,
    networkMonitor: NetworkMonitor
) : OfflineFirstRepository(networkMonitor) {

    fun getAllMenuItems(): Flow<List<MenuItem>> {
        return menuItemDao.getAllMenuItems()
            .map { entities -> entities.map { it.toModel() } }
            .onStart {
                if (isOnline()) {
                    syncMenuItemsFromRemote()
                }
            }
    }
    suspend fun getMenuItems(category: String? = null): Flow<Result<List<MenuItem>>> = flow {
        try {
            getAllMenuItems()
            val response = apiService.getMenuItems()

            if (response.isSuccessful) {
                val menuItems = response.body()
                if (menuItems != null) {
                    // If category is provided, filter by category
                    val filteredItems = if (category != null) {
                        menuItems.filter { it.item_cat_name == category }
                    } else {
                        menuItems
                    }
                    emit(Result.success(filteredItems))
                } else {
                    emit(Result.failure(Exception("No menu items data received")))
                }
            } else {
                emit(Result.failure(Exception("Error fetching menu items: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getMenuItemsByCategory(categoryId: Long): Flow<List<MenuItem>> {
        return menuItemDao.getMenuItemsByCategory(categoryId.toString())
            .map { entities -> entities.map { it.toModel() } }
    }

    suspend fun getMenuItemById(id: Long): MenuItem? {
        return menuItemDao.getMenuItemById(id)?.toModel()
    }

    private suspend fun syncMenuItemsFromRemote() {
        safeApiCall(
            onSuccess = { remoteItems: List<MenuItem> ->
                withContext(Dispatchers.IO) {
                    val entities = remoteItems.map { it.toEntity() }
                    menuItemDao.insertMenuItems(entities)
                }
            },
            apiCall = { apiService.getAllMenuItems() }
        )
    }

    suspend fun forceSyncAllMenuItems() {
        if (isOnline()) {
            syncMenuItemsFromRemote()
        }
    }

    suspend fun getAllMenuItems(): List<com.warriortech.resb.model.MenuItem> {
        // TODO: Implement actual data fetching from database
        // For now, return sample data
        return listOf(
            com.warriortech.resb.model.MenuItem(
                menu_item_id = 1,
                menu_item_name = "Chicken Biryani",
                menu_item_name_tamil = "சிக்கன் பிரியாணி",
                item_cat_id = 1,
                rate = 250.0,
                ac_rate = 280.0,
                parcel_rate = 230.0,
                image = "",
                is_available = true,
                parcel_charge = 10.0,
                tax_id = 1,
                tax_name = "GST",
                tax_percentage = 5.0,
                kitchen_cat_id = 1,
                kitchen_cat_name = "Main Kitchen",
                stock_maintain = true,
                rate_lock = false,
                unit_id = 1,
                unit_name = "Plate",
                min_stock = 5,
                hsn_code = "12345",
                order_by = 1,
                is_inventory = true,
                is_raw = false,
                cess_specific = 2.0,
                cess_per = 1.0
            ),
            com.warriortech.resb.model.MenuItem(
                menu_item_id = 2,
                menu_item_name = "Mutton Curry",
                menu_item_name_tamil = "ஆட்டுக்கறி",
                item_cat_id = 1,
                rate = 180.0,
                ac_rate = 200.0,
                parcel_rate = 170.0,
                image = "",
                is_available = true,
                parcel_charge = 10.0,
                tax_id = 1,
                tax_name = "GST",
                tax_percentage = 5.0,
                kitchen_cat_id = 1,
                kitchen_cat_name = "Main Kitchen",
                stock_maintain = true,
                rate_lock = false,
                unit_id = 1,
                unit_name = "Plate",
                min_stock = 5,
                hsn_code = "12345",
                order_by = 1,
                is_inventory = true,
                is_raw = false,
                cess_specific = 2.0,
                cess_per = 1.0
            )
        )
    }

    suspend fun insertMenuItem(menuItem: com.warriortech.resb.model.MenuItem) {
        // TODO: Implement actual data insertion
    }

    suspend fun updateMenuItem(menuItem: com.warriortech.resb.model.MenuItem) {
        // TODO: Implement actual data update
    }

    suspend fun deleteMenuItem(menuItemId: Int) {
        // TODO: Implement actual data deletion
    }
}

// Extension functions
private fun MenuItem.toEntity() = MenuItemEntity(
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
    cess_per= cess_per
)

private fun MenuItemEntity.toModel() = MenuItem(
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
    cess_specific = this.cess_specific
)