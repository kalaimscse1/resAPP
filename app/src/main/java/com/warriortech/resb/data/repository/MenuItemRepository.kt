package com.warriortech.resb.data.repository


import com.warriortech.resb.model.MenuCategory
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Menu-related API operations
 * Updated to work with the Kotlin Mini App backend
 */
@Singleton
class MenuItemRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Get all menu items
     * If category is provided, will filter the items by category client-side
     */
    suspend fun getMenuItems(category: String? = null): Flow<Result<List<MenuItem>>> = flow {
        try {
            val response = apiService.getMenuItems()

            if (response.isSuccessful) {
                val menuItems = response.body()
                if (menuItems != null) {
                    // If category is provided, filter by category
                    val filteredItems = if (category != null) {
                        menuItems.filter { it.menu_cat_name == category }
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

    /**
     * Get menu items grouped by category
     * Groups the items client-side after fetching all menu items
     */
    suspend fun getMenuCategories(): Flow<Result<List<MenuCategory>>> = flow {
        try {
            val response = apiService.getMenuItems()

            if (response.isSuccessful) {
                val menuItems = response.body()
                if (menuItems != null) {
                    // Group items by category
                    val groupedItems = menuItems.groupBy { it.menu_cat_name }

                    // Convert to MenuCategory objects
                    val categories = groupedItems.map { (categoryName, items) ->
                        MenuCategory(
                            id = items.firstOrNull()?.menu_item_id ?: 0, // Use first item's ID or 0
                            name = categoryName,
                            items = items
                        )
                    }

                    emit(Result.success(categories))
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
}
