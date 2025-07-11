package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.model.MenuItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuItemRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getAllMenuItems(): Flow<List<MenuItem>> = flow {
        try {
            val response = apiService.getAllMenuItems()
            if (response.isSuccessful) {
                emit(response.body() ?: emptyList())
            } else {
                throw Exception("Failed to fetch menu items: ${response.message()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun insertMenuItem(menuItem: MenuItem): MenuItem {
        val response = apiService.createMenuItem(menuItem)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to create menu item")
        } else {
            throw Exception("Failed to create menu item: ${response.message()}")
        }
    }

    suspend fun updateMenuItem(menuItem: MenuItem): MenuItem {
        val response = apiService.updateMenuItem(menuItem.id, menuItem)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to update menu item")
        } else {
            throw Exception("Failed to update menu item: ${response.message()}")
        }
    }

    suspend fun deleteMenuItem(menuItemId: Int) {
        val response = apiService.deleteMenuItem(menuItemId)
        if (!response.isSuccessful) {
            throw Exception("Failed to delete menu item: ${response.message()}")
        }
    }
}