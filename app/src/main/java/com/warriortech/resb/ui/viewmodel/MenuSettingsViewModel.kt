
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.MenuRepository
import com.warriortech.resb.model.Menu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuSettingsViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiState {
        object Loading : UiState()
        data class Success(val menus: List<Menu>) : UiState()
        data class Error(val message: String) : UiState()
    }

    fun loadMenus() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val menus = menuRepository.getAllMenus()
                _uiState.value = UiState.Success(menus)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addMenu(name: String, description: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                val menu = Menu(
                    menu_id = 0,
                    menu_name = name,
                    order_by = description,
                    is_active = isActive,
                )
                menuRepository.insertMenu(menu)
                loadMenus()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to add menu")
            }
        }
    }

    fun updateMenu(id: Long, name: String, description: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                val menu = Menu(
                    menu_id = id,
                    menu_name = name,
                    order_by = description,
                    is_active = isActive,
                )
                menuRepository.updateMenu(menu)
                loadMenus()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to update menu")
            }
        }
    }

    fun deleteMenu(id: Long) {
        viewModelScope.launch {
            try {
                menuRepository.deleteMenu(id)
                loadMenus()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to delete menu")
            }
        }
    }
}
