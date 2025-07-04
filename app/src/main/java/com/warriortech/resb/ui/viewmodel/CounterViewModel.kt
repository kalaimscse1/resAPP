
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.MenuRepository
import com.warriortech.resb.model.MenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {

    private val _menuState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val menuState: StateFlow<MenuUiState> = _menuState.asStateFlow()

    private val _selectedItems = MutableStateFlow<Map<MenuItem, Int>>(emptyMap())
    val selectedItems: StateFlow<Map<MenuItem, Int>> = _selectedItems.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    val selectedCategory = MutableStateFlow<String?>(null)

    sealed class MenuUiState {
        object Loading : MenuUiState()
        data class Success(val menuItems: List<MenuItem>) : MenuUiState()
        data class Error(val message: String) : MenuUiState()
    }

    fun loadMenuItems() {
        viewModelScope.launch {
            try {
                _menuState.value = MenuUiState.Loading
                val menuItems = menuRepository.getMenuItems()
                _menuState.value = MenuUiState.Success(menuItems)
                
                // Extract unique categories
                val uniqueCategories = menuItems.map { it.item_cat_name }.distinct().sorted()
                _categories.value = uniqueCategories
                
                // Set first category as selected if available
                if (uniqueCategories.isNotEmpty() && selectedCategory.value == null) {
                    selectedCategory.value = uniqueCategories.first()
                }
            } catch (e: Exception) {
                _menuState.value = MenuUiState.Error(e.message ?: "Failed to load menu items")
            }
        }
    }

    fun addItemToOrder(menuItem: MenuItem) {
        val currentItems = _selectedItems.value.toMutableMap()
        val currentQuantity = currentItems[menuItem] ?: 0
        currentItems[menuItem] = currentQuantity + 1
        _selectedItems.value = currentItems
    }

    fun removeItemFromOrder(menuItem: MenuItem) {
        val currentItems = _selectedItems.value.toMutableMap()
        val currentQuantity = currentItems[menuItem] ?: 0
        
        if (currentQuantity > 1) {
            currentItems[menuItem] = currentQuantity - 1
        } else {
            currentItems.remove(menuItem)
        }
        
        _selectedItems.value = currentItems
    }

    fun clearOrder() {
        _selectedItems.value = emptyMap()
    }

    fun getOrderTotal(): Double {
        return _selectedItems.value.entries.sumOf { (menuItem, quantity) ->
            menuItem.rate * quantity
        }
    }
}
