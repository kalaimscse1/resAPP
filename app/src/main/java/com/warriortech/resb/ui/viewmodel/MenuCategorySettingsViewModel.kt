
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.MenuCategoryRepository
import com.warriortech.resb.model.MenuCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuCategorySettingsViewModel @Inject constructor(
    private val categoryRepository: MenuCategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiState {
        object Loading : UiState()
        data class Success(val categories: List<MenuCategory>) : UiState()
        data class Error(val message: String) : UiState()
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val categories = categoryRepository.getAllCategories()
                _uiState.value = UiState.Success(categories)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addCategory(name: String, description: String, sortOrder: Int) {
        viewModelScope.launch {
            try {
                val category = MenuCategory(
                    id = 0,
                    name = name,
//                    description = description,
//                    sortOrder = sortOrder
                )
                categoryRepository.insertCategory(category)
                loadCategories()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to add category")
            }
        }
    }

    fun updateCategory(id: Long, name: String, description: String, sortOrder: Int) {
        viewModelScope.launch {
            try {
                val category = MenuCategory(
                    id = id,
                    name = name,
//                    description = description,
//                    sortOrder = sortOrder
                )
                categoryRepository.updateCategory(category)
                loadCategories()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to update category")
            }
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(id)
                loadCategories()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to delete category")
            }
        }
    }
}
