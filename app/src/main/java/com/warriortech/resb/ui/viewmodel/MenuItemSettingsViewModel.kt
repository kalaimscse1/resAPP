
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.MenuItemRepository
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.screens.settings.MenuItemSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuItemSettingsViewModel @Inject constructor(
    private val menuItemRepository: MenuItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuItemSettingsUiState>(MenuItemSettingsUiState.Loading)
    val uiState: StateFlow<MenuItemSettingsUiState> = _uiState.asStateFlow()

    fun loadMenuItems() {
        viewModelScope.launch {
            try {
                _uiState.value = MenuItemSettingsUiState.Loading
                val menuItems = menuItemRepository.getAllMenuItems()
                menuItems.collect {
                    _uiState.value = MenuItemSettingsUiState.Success(it)
                }

            } catch (e: Exception) {
                _uiState.value = MenuItemSettingsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addMenuItem(menuItem: MenuItem) {
        viewModelScope.launch {
            try {
                menuItemRepository.insertMenuItem(menuItem)
                loadMenuItems()
            } catch (e: Exception) {
                _uiState.value = MenuItemSettingsUiState.Error(e.message ?: "Failed to add menu item")
            }
        }
    }

    fun updateMenuItem(menuItem: MenuItem) {
        viewModelScope.launch {
            try {
                menuItemRepository.updateMenuItem(menuItem)
                loadMenuItems()
            } catch (e: Exception) {
                _uiState.value = MenuItemSettingsUiState.Error(e.message ?: "Failed to update menu item")
            }
        }
    }

    fun deleteMenuItem(menuItemId: Int) {
        viewModelScope.launch {
            try {
                val response=menuItemRepository.deleteMenuItem(menuItemId)
                if (response.isSuccessful) {
                    loadMenuItems()
                }
                else{
                    _uiState.value = MenuItemSettingsUiState.Error(response.message() ?: "Failed to delete menu item")
                }

            } catch (e: Exception) {
                _uiState.value = MenuItemSettingsUiState.Error(e.message ?: "Failed to delete menu item")
            }
        }
    }
}
