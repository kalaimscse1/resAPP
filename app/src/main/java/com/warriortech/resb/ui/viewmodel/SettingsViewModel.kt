
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.screens.SettingsItem
import com.warriortech.resb.screens.SettingsModule
import com.warriortech.resb.screens.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    // Add repository dependencies here
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _selectedModule = MutableStateFlow<SettingsModule?>(null)
    val selectedModule: StateFlow<SettingsModule?> = _selectedModule.asStateFlow()

    fun loadModuleData(module: SettingsModule) {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            try {
                // Simulate loading data - replace with actual repository calls
                val mockData = generateMockData(module)
                _uiState.value = SettingsUiState.Success(mockData)
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Failed to load ${module.title}: ${e.message}")
            }
        }
    }

    fun addItem(module: SettingsModule, data: Map<String, String>) {
        viewModelScope.launch {
            try {
                // Add implementation for creating items in database
                // Example: repository.addArea(data) for Area module
                loadModuleData(module) // Refresh data
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Failed to add ${module.title}: ${e.message}")
            }
        }
    }

    fun editItem(item: SettingsItem) {
        viewModelScope.launch {
            try {
                // Add implementation for editing items in database
                // Refresh data after edit
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Failed to edit item: ${e.message}")
            }
        }
    }

    fun deleteItem(item: SettingsItem) {
        viewModelScope.launch {
            try {
                // Add implementation for deleting items from database
                // Refresh data after delete
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("Failed to delete item: ${e.message}")
            }
        }
    }

    private fun generateMockData(module: SettingsModule): List<SettingsItem> {
        return when (module) {
            is SettingsModule.Area -> listOf(
                SettingsItem("1", "Main Dining", "Primary dining area", mapOf("capacity" to "50")),
                SettingsItem("2", "VIP Section", "Premium dining area", mapOf("capacity" to "20")),
                SettingsItem("3", "Outdoor Terrace", "Open air dining", mapOf("capacity" to "30"))
            )
            is SettingsModule.Table -> listOf(
                SettingsItem("1", "Table 1", "4-seater in main dining", mapOf("capacity" to "4", "area" to "Main Dining")),
                SettingsItem("2", "Table 2", "2-seater in main dining", mapOf("capacity" to "2", "area" to "Main Dining")),
                SettingsItem("3", "VIP Table 1", "6-seater in VIP section", mapOf("capacity" to "6", "area" to "VIP Section"))
            )
            is SettingsModule.Menu -> listOf(
                SettingsItem("1", "Breakfast Menu", "Morning menu items", mapOf("active" to "true")),
                SettingsItem("2", "Lunch Menu", "Afternoon menu items", mapOf("active" to "true")),
                SettingsItem("3", "Dinner Menu", "Evening menu items", mapOf("active" to "true"))
            )
            is SettingsModule.MenuCategory -> listOf(
                SettingsItem("1", "Appetizers", "Starter dishes", mapOf("sort_order" to "1")),
                SettingsItem("2", "Main Course", "Primary dishes", mapOf("sort_order" to "2")),
                SettingsItem("3", "Desserts", "Sweet dishes", mapOf("sort_order" to "3"))
            )
            is SettingsModule.MenuItem -> listOf(
                SettingsItem("1", "Chicken Biryani", "Aromatic rice with chicken", mapOf("rate" to "250", "category" to "Main Course")),
                SettingsItem("2", "Paneer Tikka", "Grilled cottage cheese", mapOf("rate" to "180", "category" to "Appetizers")),
                SettingsItem("3", "Gulab Jamun", "Sweet milk dumplings", mapOf("rate" to "80", "category" to "Desserts"))
            )
            is SettingsModule.Customer -> listOf(
                SettingsItem("1", "John Doe", "Regular customer", mapOf("phone" to "9876543210", "email" to "john@example.com")),
                SettingsItem("2", "Jane Smith", "VIP customer", mapOf("phone" to "9876543211", "email" to "jane@example.com"))
            )
            is SettingsModule.Staff -> listOf(
                SettingsItem("1", "Ravi Kumar", "Head Chef", mapOf("role" to "Chef", "phone" to "9876543212")),
                SettingsItem("2", "Priya Sharma", "Manager", mapOf("role" to "Manager", "phone" to "9876543213"))
            )
            is SettingsModule.Role -> listOf(
                SettingsItem("1", "Manager", "Full access to all features", mapOf("permissions" to "all")),
                SettingsItem("2", "Waiter", "Order taking and serving", mapOf("permissions" to "orders,billing")),
                SettingsItem("3", "Chef", "Kitchen operations", mapOf("permissions" to "kitchen,orders"))
            )
            is SettingsModule.Printer -> listOf(
                SettingsItem("1", "Kitchen Printer", "KOT printer in kitchen", mapOf("ip" to "192.168.1.100", "type" to "thermal")),
                SettingsItem("2", "Billing Printer", "Receipt printer at counter", mapOf("ip" to "192.168.1.101", "type" to "thermal"))
            )
            is SettingsModule.Tax -> listOf(
                SettingsItem("1", "CGST", "Central GST", mapOf("rate" to "9", "type" to "percentage")),
                SettingsItem("2", "SGST", "State GST", mapOf("rate" to "9", "type" to "percentage"))
            )
            is SettingsModule.TaxSplit -> listOf(
                SettingsItem("1", "Standard GST Split", "18% split as 9% CGST + 9% SGST", mapOf("percentage" to "18")),
                SettingsItem("2", "Service Tax", "Service charge", mapOf("percentage" to "10"))
            )
        }
    }
}
