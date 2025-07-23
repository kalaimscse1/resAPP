
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Menu
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getAllMenus(): List<Menu> {
        val response = apiService.getAllMenus(SessionManager.getCompanyCode()?:"")
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch menus: ${response.message()}")
        }
    }

    suspend fun insertMenu(menu: Menu): Menu {
        val response = apiService.createMenu(menu,SessionManager.getCompanyCode()?:"")
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to create menu")
        } else {
            throw Exception("Failed to create menu: ${response.message()}")
        }
    }

    suspend fun updateMenu(menu: Menu): Int {
        val response = apiService.updateMenu(menu.menu_id, menu,SessionManager.getCompanyCode()?:"")
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to update menu")
        } else {
            throw Exception("Failed to update menu: ${response.message()}")
        }
    }

    suspend fun deleteMenu(menuId: Long) {
        val response = apiService.deleteMenu(menuId,SessionManager.getCompanyCode()?:"")
        if (!response.isSuccessful) {
            throw Exception("Failed to delete menu: ${response.message()}")
        }
    }
}
