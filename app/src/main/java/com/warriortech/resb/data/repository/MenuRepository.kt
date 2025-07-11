
package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.model.Menu
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getAllMenus(): List<Menu> {
        val response = apiService.getAllMenus()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch menus: ${response.message()}")
        }
    }

    suspend fun insertMenu(menu: Menu): Menu {
        val response = apiService.createMenu(menu)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to create menu")
        } else {
            throw Exception("Failed to create menu: ${response.message()}")
        }
    }

    suspend fun updateMenu(menu: Menu): Menu {
        val response = apiService.updateMenu(menu.id, menu)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to update menu")
        } else {
            throw Exception("Failed to update menu: ${response.message()}")
        }
    }

    suspend fun deleteMenu(menuId: Int) {
        val response = apiService.deleteMenu(menuId)
        if (!response.isSuccessful) {
            throw Exception("Failed to delete menu: ${response.message()}")
        }
    }
}
