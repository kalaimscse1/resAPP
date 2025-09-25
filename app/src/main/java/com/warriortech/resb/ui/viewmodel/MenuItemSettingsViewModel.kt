package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.MenuCategoryRepository
import com.warriortech.resb.data.repository.MenuItemRepository
import com.warriortech.resb.data.repository.MenuRepository
import com.warriortech.resb.data.repository.TaxRepository
import com.warriortech.resb.model.KitchenCategory
import com.warriortech.resb.model.Menu
import com.warriortech.resb.model.MenuCategory
import com.warriortech.resb.model.Tax
import com.warriortech.resb.model.TblMenuItemRequest
import com.warriortech.resb.model.TblUnit
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.screens.settings.MenuItemSettingsUiState
import com.warriortech.resb.util.CurrencySettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuItemSettingsViewModel @Inject constructor(
    private val menuItemRepository: MenuItemRepository,
    private val menuRepository: MenuRepository,
    private val menuCategoryRepository: MenuCategoryRepository,
    private val taxRepository: TaxRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<MenuItemSettingsUiState>(MenuItemSettingsUiState.Loading)
    val uiState: StateFlow<MenuItemSettingsUiState> = _uiState.asStateFlow()

    private val _menus = MutableStateFlow<List<Menu>>(emptyList())
    val menus: StateFlow<List<Menu>> = _menus.asStateFlow()

    private val _menuCategories = MutableStateFlow<List<MenuCategory>>(emptyList())
    val menuCategories: StateFlow<List<MenuCategory>> = _menuCategories.asStateFlow()

    private val _kitchenCategories = MutableStateFlow<List<KitchenCategory>>(emptyList())
    val kitchenCategories: StateFlow<List<KitchenCategory>> = _kitchenCategories.asStateFlow()

    private val _taxes = MutableStateFlow<List<Tax>>(emptyList())
    val taxes: StateFlow<List<Tax>> = _taxes.asStateFlow()

    private val _units = MutableStateFlow<List<TblUnit>>(emptyList())
    val units: StateFlow<List<TblUnit>> = _units.asStateFlow()


    init {
        CurrencySettings.update(
            symbol = sessionManager.getRestaurantProfile()?.currency ?: "",
            decimals = sessionManager.getRestaurantProfile()?.decimal_point?.toInt() ?: 2
        )

    }

    fun loadMenuItems() {
        viewModelScope.launch {
            try {
                _uiState.value = MenuItemSettingsUiState.Loading
                val menus = menuRepository.getAllMenus()
                val menuCategories = menuCategoryRepository.getAllCategories()
                val taxes = taxRepository.getAllTaxes()
                val kitchenCategories = menuCategoryRepository.getAllKitchenCategories()
                val units = menuCategoryRepository.getAllUnits()
                _menus.value = menus
                _menuCategories.value = menuCategories
                _taxes.value = taxes
                _kitchenCategories.value = kitchenCategories
                _units.value = units
                val menuItems = menuItemRepository.getAllMenuItems()
                menuItems.collect {
                    _uiState.value = MenuItemSettingsUiState.Success(it)
                }

            } catch (e: Exception) {
                _uiState.value = MenuItemSettingsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addMenuItem(menuItem: TblMenuItemRequest) {
        viewModelScope.launch {
            try {
                menuItemRepository.insertMenuItem(menuItem)
                loadMenuItems()
            } catch (e: Exception) {
                _uiState.value =
                    MenuItemSettingsUiState.Error(e.message ?: "Failed to add menu item")
            }
        }
    }

    fun updateMenuItem(menuItem: TblMenuItemRequest) {
        viewModelScope.launch {
            try {
                menuItemRepository.updateMenuItem(menuItem)
                loadMenuItems()
            } catch (e: Exception) {
                _uiState.value =
                    MenuItemSettingsUiState.Error(e.message ?: "Failed to update menu item")
            }
        }
    }

    fun deleteMenuItem(menuItemId: Int) {
        viewModelScope.launch {
            try {
                val response = menuItemRepository.deleteMenuItem(menuItemId)
                if (response.isSuccessful) {
                    loadMenuItems()
                } else {
                    _uiState.value = MenuItemSettingsUiState.Error(
                        response.message() ?: "Failed to delete menu item"
                    )
                }

            } catch (e: Exception) {
                _uiState.value =
                    MenuItemSettingsUiState.Error(e.message ?: "Failed to delete menu item")
            }
        }
    }
}
