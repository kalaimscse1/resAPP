
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.ai.AIRepository
import com.warriortech.resb.data.local.dao.MenuItemDao
import com.warriortech.resb.data.local.entity.MenuItemEntity
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.TblOrderDetailsResponse
import dagger.hilt.android.lifecycle.HiltViewModel

fun MenuItemEntity.toMenuItem(): MenuItem {
    return MenuItem(
        id = this.id,
        name = this.name,
        rate = this.rate,
        description = this.description ?: ""
    )
}
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIAssistantViewModel @Inject constructor(
    private val aiRepository: AIRepository,
    private val menuItemDao: MenuItemDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIAssistantUiState())
    val uiState: StateFlow<AIAssistantUiState> = _uiState.asStateFlow()

    fun setApiKey(apiKey: String) {
        aiRepository.setApiKey(apiKey)
        _uiState.update { it.copy(apiKeyConfigured = true) }
    }

    fun enhanceMenuDescriptions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val menuItems = menuItemDao.getAllMenuItems()
                val results = mutableListOf<String>()
                
                menuItems.take(3).forEach { menuItem ->
                    val result = aiRepository.generateMenuDescription(menuItem.toMenuItem())
                    result.onSuccess { description ->
                        results.add("${menuItem.name}: $description")
                    }.onFailure { error ->
                        results.add("${menuItem.name}: Failed to generate description - ${error.message}")
                    }
                }
                
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        results = results,
                        errorMessage = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Failed to enhance menu: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun generateUpsellSuggestions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                // Sample order items for demonstration
                val sampleOrderItems = listOf(
                    TblOrderDetailsResponse(
                        orderId = 1,
                        name = "Chicken Biryani",
                        rate = 250.0,
                        quantity = 1
                    ),
                    TblOrderDetailsResponse(
                        orderId = 1,
                        name = "Paneer Butter Masala",
                        rate = 180.0,
                        quantity = 1
                    )
                )
                
                val result = aiRepository.suggestUpsells(sampleOrderItems)
                result.onSuccess { suggestions ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            results = suggestions,
                            errorMessage = null
                        ) 
                    }
                }.onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to generate suggestions: ${error.message}"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Failed to generate suggestions: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun analyzeSalesData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                // Sample sales data for demonstration
                val sampleSalesData = listOf(
                    TblOrderDetailsResponse(orderId = 1, name = "Chicken Biryani", rate = 250.0, quantity = 3),
                    TblOrderDetailsResponse(orderId = 2, name = "Paneer Butter Masala", rate = 180.0, quantity = 2),
                    TblOrderDetailsResponse(orderId = 3, name = "Dal Tadka", rate = 120.0, quantity = 5),
                    TblOrderDetailsResponse(orderId = 4, name = "Chicken Biryani", rate = 250.0, quantity = 2)
                )
                
                val result = aiRepository.analyzeSalesData(sampleSalesData)
                result.onSuccess { analysis ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            results = listOf(analysis),
                            errorMessage = null
                        ) 
                    }
                }.onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to analyze sales: ${error.message}"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Failed to analyze sales: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun generateCustomerRecommendations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                // Sample customer order history
                val customerHistory = listOf(
                    TblOrderDetailsResponse(orderId = 1, name = "Chicken Biryani", rate = 250.0, quantity = 1),
                    TblOrderDetailsResponse(orderId = 2, name = "Chicken Biryani", rate = 250.0, quantity = 1),
                    TblOrderDetailsResponse(orderId = 3, name = "Butter Naan", rate = 50.0, quantity = 2),
                    TblOrderDetailsResponse(orderId = 4, name = "Lassi", rate = 60.0, quantity = 3)
                )
                
                val result = aiRepository.generateCustomerRecommendations(customerHistory)
                result.onSuccess { recommendations ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            results = recommendations,
                            errorMessage = null
                        ) 
                    }
                }.onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = "Failed to generate recommendations: ${error.message}"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Failed to generate recommendations: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun clearResults() {
        _uiState.update { it.copy(results = emptyList(), errorMessage = null) }
    }
}

data class AIAssistantUiState(
    val isLoading: Boolean = false,
    val apiKeyConfigured: Boolean = false,
    val results: List<String> = emptyList(),
    val errorMessage: String? = null
)
