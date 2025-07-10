
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.MenuCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuCategoryRepository @Inject constructor() {

    private val categories = mutableListOf<MenuCategory>()

    suspend fun getAllCategories(): List<MenuCategory> {
        return categories.sortedBy { it.sortOrder }
    }

    suspend fun insertCategory(category: MenuCategory): Long {
        val newId = (categories.maxOfOrNull { it.id } ?: 0) + 1
        val newCategory = category.copy(id = newId)
        categories.add(newCategory)
        return newId
    }

    suspend fun updateCategory(category: MenuCategory) {
        val index = categories.indexOfFirst { it.id == category.id }
        if (index != -1) {
            categories[index] = category
        }
    }

    suspend fun deleteCategory(id: Long) {
        categories.removeAll { it.id == id }
    }

    suspend fun getCategoryById(id: Long): MenuCategory? {
        return categories.find { it.id == id }
    }
}
