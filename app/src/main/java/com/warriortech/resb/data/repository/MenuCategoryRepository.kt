
package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.model.MenuCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuCategoryRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getAllCategories(): List<MenuCategory> {
        val response = apiService.getAllMenuCategories()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch categories: ${response.message()}")
        }
    }

    suspend fun insertCategory(category: MenuCategory): MenuCategory {
        val response = apiService.createMenuCategory(category)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to create category")
        } else {
            throw Exception("Failed to create category: ${response.message()}")
        }
    }

    suspend fun updateCategory(category: MenuCategory): MenuCategory {
        val response = apiService.updateMenuCategory(category.id, category)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to update category")
        } else {
            throw Exception("Failed to update category: ${response.message()}")
        }
    }

    suspend fun deleteCategory(categoryId: Int) {
        val response = apiService.deleteMenuCategory(categoryId)
        if (!response.isSuccessful) {
            throw Exception("Failed to delete category: ${response.message()}")
        }
    }
}
