package com.warriortech.resb.ui.viewmodel



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.Order
import com.warriortech.resb.model.OrderItem
import com.warriortech.resb.data.repository.MenuItemRepository
import com.warriortech.resb.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuItemRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    sealed class MenuUiState {
        object Loading : MenuUiState()
        data class Success(val menuItems: List<MenuItem>) : MenuUiState()
        data class Error(val message: String) : MenuUiState()
    }

    sealed class OrderUiState {
        object Idle : OrderUiState()
        object Loading : OrderUiState()
        data class Success(val order: Order) : OrderUiState()
        data class Error(val message: String) : OrderUiState()
    }

    private val _menuState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val menuState: StateFlow<MenuUiState> = _menuState.asStateFlow()

    private val _orderState = MutableStateFlow<OrderUiState>(OrderUiState.Idle)
    val orderState: StateFlow<OrderUiState> = _orderState.asStateFlow()

    private val _selectedItems = MutableStateFlow<Map<MenuItem, Int>>(emptyMap())
    var selectedItems: StateFlow<Map<MenuItem, Int>> = _selectedItems.asStateFlow()
    val categories = MutableStateFlow<List<String>>(emptyList())
    val selectedCategory = MutableStateFlow<String?>(null)


    fun loadMenuItems(category: String? = null) {
        viewModelScope.launch {
            _menuState.value = MenuUiState.Loading

            menuRepository.getMenuItems(category).collect { result ->
                result.fold(
                    onSuccess = { menuItems ->
                        _menuState.value = MenuUiState.Success(menuItems)
                        categories.value= menuItems.map { it.menu_cat_name }.distinct().sorted()
                        selectedCategory.value=categories.value.firstOrNull()
                    },
                    onFailure = { error ->
                        _menuState.value = MenuUiState.Error(error.message ?: "Failed to load menu items")
                    }
                )
            }
        }
    }


    fun addItemToOrder(menuItem: MenuItem) {
        val currentItems = _selectedItems.value.toMutableMap()
        val currentCount = currentItems[menuItem] ?: 0
        currentItems[menuItem] = currentCount + 1
        _selectedItems.value = currentItems
    }

    fun removeItemFromOrder(menuItem: MenuItem) {
        val currentItems = _selectedItems.value.toMutableMap()
        val currentCount = currentItems[menuItem] ?: 0

        if (currentCount > 1) {
            currentItems[menuItem] = currentCount - 1
        } else {
            currentItems.remove(menuItem)
        }

        _selectedItems.value = currentItems
    }

    fun clearOrder() {
        _selectedItems.value = emptyMap()
        _orderState.value = OrderUiState.Idle
    }

    fun placeOrder(tableId: Int) {
        viewModelScope.launch {
            if (_selectedItems.value.isEmpty()) {
                _orderState.value = OrderUiState.Error("No items selected")
                return@launch
            }

            _orderState.value = OrderUiState.Loading

            val orderItems = _selectedItems.value.map { (menuItem, quantity) ->
                OrderItem(
                    menuItemId = menuItem.menu_item_id,
                    menuItemName = menuItem.menu_item_name,
                    quantity = quantity,
                    price = menuItem.rate
                )
            }

            orderRepository.createOrder(tableId, orderItems).collect { result ->
                result.fold(
                    onSuccess = { order ->
                        // After creating the order, print the KOT
                        order.id?.let { printKOT(it) } ?: run {
                            _orderState.value = OrderUiState.Error("Order ID is missing")
                        }
                    },
                    onFailure = { error ->
                        _orderState.value = OrderUiState.Error(error.message ?: "Failed to place order")
                    }
                )
            }
        }
    }

    private fun printKOT(orderId: Long) {
        viewModelScope.launch {
            orderRepository.printKOT(orderId).collect { result ->
                result.fold(
                    onSuccess = { printResponse ->
                        // Using OrderUiState.Success even though we get a PrintResponse
                        // We'll create a fake Order object with the orderId for simplicity
                        val order = Order(
                            id = printResponse.orderId,
                            tableId = 0, // We don't know the tableId here
                            items = emptyList(),
                            totalAmount = 0.0,
                            status = "PENDING",
                            isPrinted = true
                        )
                        _orderState.value = OrderUiState.Success(order)

                        // Clear the selection after successful order
                        _selectedItems.value = emptyMap()
                    },
                    onFailure = { error ->
                        _orderState.value = OrderUiState.Error(error.message ?: "Failed to print KOT")
                    }
                )
            }
        }
    }

    fun getOrderTotal(): Double {
        return _selectedItems.value.entries.sumOf { (menuItem, quantity) ->
            menuItem.rate * quantity
        }
    }
}
