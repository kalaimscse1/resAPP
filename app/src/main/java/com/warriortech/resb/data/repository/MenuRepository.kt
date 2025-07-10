
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Menu
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuRepository @Inject constructor() {

    private val menus = mutableListOf<Menu>()

    suspend fun getAllMenus(): List<Menu> {
        return menus.toList()
    }

    suspend fun insertMenu(menu: Menu): Long {
        val newId = (menus.maxOfOrNull { it.id } ?: 0) + 1
        val newMenu = menu.copy(id = newId)
        menus.add(newMenu)
        return newId
    }

    suspend fun updateMenu(menu: Menu) {
        val index = menus.indexOfFirst { it.id == menu.id }
        if (index != -1) {
            menus[index] = menu
        }
    }

    suspend fun deleteMenu(id: Long) {
        menus.removeAll { it.id == id }
    }

    suspend fun getMenuById(id: Long): Menu? {
        return menus.find { it.id == id }
    }
}
