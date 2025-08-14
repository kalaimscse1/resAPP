package com.warriortech.resb.ui.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.Order
import com.warriortech.resb.model.OrderItem
import com.warriortech.resb.data.repository.MenuItemRepository
import com.warriortech.resb.data.repository.ModifierRepository
import com.warriortech.resb.data.repository.OrderRepository
import com.warriortech.resb.data.repository.TableRepository
import com.warriortech.resb.model.KOTItem
import com.warriortech.resb.model.KOTRequest
import com.warriortech.resb.model.Modifiers
import com.warriortech.resb.model.TblOrderDetailsResponse
import com.warriortech.resb.network.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuItemRepository,
    private val orderRepository: OrderRepository,
    private val tableRepository: TableRepository,
    private val modifierRepository: ModifierRepository,
    private val sessionManager: SessionManager
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

    private val _selectedTableId = MutableStateFlow<Long?>(null)

    private val _selectedItems = MutableStateFlow<Map<MenuItem, Int>>(emptyMap())
    var selectedItems: StateFlow<Map<MenuItem, Int>> = _selectedItems.asStateFlow()

    val categories = MutableStateFlow<List<String>>(emptyList())

    val selectedCategory = MutableStateFlow<String?>(null)

    val tableStatus = MutableStateFlow<String?>(null)

    val existingOrderId = MutableStateFlow<String?>(null)

    private val _newselectedItems = MutableStateFlow<Map<MenuItem, Int>>(emptyMap())
    var newselectedItems: StateFlow<Map<MenuItem, Int>> = _newselectedItems.asStateFlow()

    private val _isExistingOrderLoaded = MutableStateFlow(false)
    val isExistingOrderLoaded: StateFlow<Boolean> = _isExistingOrderLoaded.asStateFlow()

    val orderDetailsResponse = MutableStateFlow<List<TblOrderDetailsResponse>>(emptyList())

    private val _selectedMenuItemForModifier = MutableStateFlow<MenuItem?>(null)
    val selectedMenuItemForModifier: StateFlow<MenuItem?> =
        _selectedMenuItemForModifier.asStateFlow()

    private val _showModifierDialog = MutableStateFlow<Boolean>(false)
    val showModifierDialog: StateFlow<Boolean> = _showModifierDialog.asStateFlow()

    private val _modifierGroups = MutableStateFlow<List<Modifiers>>(emptyList())
    val modifierGroups: StateFlow<List<Modifiers>> = _modifierGroups.asStateFlow()

    private val _selectedModifiers = MutableStateFlow<Map<Long, List<Modifiers>>>(emptyMap())
    val selectedModifiers: StateFlow<Map<Long, List<Modifiers>>> = _selectedModifiers.asStateFlow()

    fun initializeScreen(isTableOrder: Boolean, currentTableId: Long) {
        viewModelScope.launch {
            loadMenuItems()
            if (isTableOrder) {
                val existingItemsForTable =
                    orderRepository.getOpenOrderItemsForTable(currentTableId) // Or from local cache
                if (existingItemsForTable.isNotEmpty()) {
                    orderDetailsResponse.value = existingItemsForTable
                    val menuItems = existingItemsForTable.map {

                        MenuItem(
                            menu_item_id = it.menuItem.menu_item_id,
                            menu_item_name = it.menuItem.menu_item_name,
                            menu_item_name_tamil = it.menuItem.menu_item_name_tamil,
                            item_cat_id = it.menuItem.item_cat_id,
                            item_cat_name = it.menuItem.item_cat_name,
                            rate = it.rate,
                            ac_rate = it.rate,
                            parcel_rate = it.rate,
                            parcel_charge = it.rate,
                            tax_id = it.menuItem.tax_id,
                            tax_name = it.menuItem.tax_name,
                            tax_percentage = it.menuItem.tax_percentage,
                            kitchen_cat_id = it.menuItem.kitchen_cat_id,
                            kitchen_cat_name = it.menuItem.kitchen_cat_name,
                            stock_maintain = it.menuItem.stock_maintain,
                            rate_lock = it.menuItem.rate_lock,
                            unit_id = it.menuItem.unit_id,
                            unit_name = it.menuItem.unit_name,
                            min_stock = it.menuItem.min_stock,
                            hsn_code = it.menuItem.hsn_code,
                            order_by = it.menuItem.order_by,
                            is_inventory = it.menuItem.is_inventory,
                            is_raw = it.menuItem.is_raw,
                            is_available = it.menuItem.is_available,
                            image = it.menuItem.image,
                            qty = it.qty,
                            cess_specific = it.cess_specific,
                            cess_per = it.cess_per.toString(),
                            is_favourite = it.menuItem.is_favourite
                        )
                    }
                    _selectedItems.value = menuItems.associateWith { it.qty as Int }.toMutableMap()
                    _isExistingOrderLoaded.value = true
                    existingOrderId.value =
                        existingItemsForTable.firstOrNull()?.order_master_id
                } else {
                    _selectedItems.value = mutableMapOf()
                }
            } else {
                _selectedItems.value = mutableMapOf()
            }
        }
    }

    fun loadMenuItems(category: String? = null) {
        viewModelScope.launch {
            _menuState.value = MenuUiState.Loading
            tableStatus.value = _selectedTableId.value?.let { tableRepository.getstatus(it) }
            menuRepository.getMenuItems(category).collect { result ->
                result.fold(
                    onSuccess = { menuItems ->
                        _menuState.value = MenuUiState.Success(menuItems)
                        val data = buildList {
                            add("FAVOURITES")
                            addAll(menuItems.map { it.item_cat_name }.distinct())
                        }
                        categories.value = data
//                        categories.value = menuItems.map { it.item_cat_name }.distinct().sorted()
                        selectedCategory.value = categories.value.firstOrNull()
                    },
                    onFailure = { error ->
                        _menuState.value =
                            MenuUiState.Error(error.message ?: "Failed to load menu items")
                    }
                )
            }
        }
    }

    fun setTableId(tableId: Long?) {
        _selectedTableId.value = tableId
    }

    @SuppressLint("SuspiciousIndentation")
    fun addItemToOrder(menuItem: MenuItem) {

        if (_isExistingOrderLoaded.value) {
            val currentItems = _newselectedItems.value.toMutableMap()
            val currentQuantity = currentItems[menuItem] ?: 0
            currentItems[menuItem] = currentQuantity + 1
            _newselectedItems.value = currentItems
        } else {
            val currentItems = _selectedItems.value.toMutableMap()
            val currentQuantity = currentItems[menuItem] ?: 0
            currentItems[menuItem] = currentQuantity + 1
            _selectedItems.value = currentItems
        }

    }

    @SuppressLint("SuspiciousIndentation")
    fun removeItemFromOrder(menuItem: MenuItem) {

        if (_isExistingOrderLoaded.value) {
            val currentItems = _newselectedItems.value.toMutableMap()
            val currentQuantity = currentItems[menuItem] ?: 0
            if (currentQuantity > 1) {
                currentItems[menuItem] = currentQuantity - 1
            } else {
                currentItems.remove(menuItem)
            }
            _newselectedItems.value = currentItems
        } else {
            val currentItems = _selectedItems.value.toMutableMap()
            val currentQuantity = currentItems[menuItem] ?: 0
            if (currentQuantity > 1) {
                currentItems[menuItem] = currentQuantity - 1
            } else {
                currentItems.remove(menuItem)
            }
            _selectedItems.value = currentItems
        }
    }

    fun clearOrder() {
        _newselectedItems.value = mutableMapOf()
        _selectedItems.value = mutableMapOf()
        _isExistingOrderLoaded.value = false
        _orderState.value = OrderUiState.Idle
    }

    fun placeOrder(tableId: Long, tableStatus1: String?) {
        viewModelScope.launch {
            if (_selectedItems.value.isEmpty()) {
                _orderState.value = OrderUiState.Error("No items selected")
                return@launch
            }

            _orderState.value = OrderUiState.Loading

            if (_isExistingOrderLoaded.value) {
                val orderItems = _newselectedItems.value.map { (menuItem, quantity) ->
                    OrderItem(
                        quantity = quantity,
                        menuItem = menuItem,
                    )
                }
                orderRepository.placeOrUpdateOrder(
                    tableId, orderItems,
                    tableStatus1.toString(), existingOrderId.value
                ).collect { result ->
                    result.fold(
                        onSuccess = { order ->
                            val kotItem = orderItems.map { orderItem ->
                                val modifierNames =
                                    _selectedModifiers.value[orderItem.menuItem.menu_item_id]?.map { it.add_on_name }
                                        ?: emptyList()
                                KOTItem(
                                    name = orderItem.menuItem.menu_item_name,
                                    quantity = orderItem.quantity,
                                    category = orderItem.menuItem.kitchen_cat_name,
                                    addOn = modifierNames
                                )
                            }
                            val kotRequest = KOTRequest(
                                tableNumber = if (tableStatus1 != "TAKEAWAY" && tableStatus1 != "DELIVERY") order.table_name else tableStatus1.toString(),
                                kotId = order.kot_number,
                                orderId = order.order_master_id,
                                waiterName = sessionManager.getUser()?.user_name,
                                orderCreatedAt = order.order_create_time,
                                items = kotItem
                            )
                            printKOT(kotRequest)
                        },
                        onFailure = { error ->
                            _orderState.value =
                                OrderUiState.Error(error.message ?: "Failed to place order")
                        }
                    )
                }
            } else {
                val orderItems = _selectedItems.value.map { (menuItem, quantity) ->
                    OrderItem(
                        quantity = quantity,
                        menuItem = menuItem,
                    )
                }
                orderRepository.placeOrUpdateOrder(
                    tableId, orderItems,
                    tableStatus1.toString()
                ).collect { result ->
                    result.fold(
                        onSuccess = { order ->
                            val kotItem = orderItems.map { orderItem ->
                                val modifierNames =
                                    _selectedModifiers.value[orderItem.menuItem.menu_item_id]?.map { it.add_on_name }
                                        ?: emptyList()
                                KOTItem(
                                    name = orderItem.menuItem.menu_item_name,
                                    quantity = orderItem.quantity,
                                    category = orderItem.menuItem.kitchen_cat_name,
                                    addOn = modifierNames
                                )
                            }
                            val kotRequest = KOTRequest(
                                tableNumber = if (tableStatus1 != "TAKEAWAY" && tableStatus.value != "DELIVERY") order.table_name.toString() else tableStatus1.toString(),
                                kotId = order.kot_number,
                                orderId = order.order_master_id,
                                waiterName = sessionManager.getUser()?.user_name,
                                orderCreatedAt = order.order_create_time,
                                items = kotItem
                            )
                            printKOT(kotRequest)
                        },
                        onFailure = { error ->
                            _orderState.value =
                                OrderUiState.Error(error.message ?: "Failed to place order")
                        }
                    )
                }
            }
        }
    }

    private fun printKOT(orderId: KOTRequest) {
        viewModelScope.launch {
            val isKOTEnabled = sessionManager.getGeneralSetting()?.is_kot ?: false
            if (isKOTEnabled) {
                val category = orderId.items.groupBy { it.category }
                for ((category, items) in category) {
                    val kotForCategory = KOTRequest(
                        tableNumber = orderId.tableNumber,
                        kotId = orderId.kotId,
                        orderId = orderId.orderId,
                        waiterName = orderId.waiterName,
                        items = items,
                        orderCreatedAt = orderId.orderCreatedAt,
                        paperWidth = orderId.paperWidth
                    )
                    val ip = orderRepository.getIpAddress(category)
                    orderRepository.printKOT(kotForCategory, ip).collect { result ->
                        result.fold(
                            onSuccess = { printResponse ->
                                val order = Order(
                                    id = 1,
                                    tableId = 0,
                                    items = emptyList(),
                                    totalAmount = 0.0,
                                    status = "PENDING",
                                    isPrinted = true
                                )
                                _orderState.value = OrderUiState.Success(order)

                                _selectedItems.value = emptyMap()
                            },
                            onFailure = { error ->
                                _orderState.value = OrderUiState.Error(
                                    error.message ?: "Failed to print KOT for $category"
                                )
                            }
                        )
                    }
                }
            }else{
                val order = Order(
                    id = 1,
                    tableId = 0,
                    items = emptyList(),
                    totalAmount = 0.0,
                    status = "PENDING",
                    isPrinted = true
                )
                _orderState.value = OrderUiState.Success(order)

                _selectedItems.value = emptyMap()
            }
        }
    }

    fun getOrderTotal(tableStatus: String): Double {
        return _selectedItems.value.entries.sumOf { (menuItem, quantity) ->
            if (tableStatus == "AC")
                menuItem.ac_rate * quantity
            else if (tableStatus == "TAKEAWAY" || tableStatus == "DELIVERY")
                menuItem.parcel_rate * quantity
            else
                menuItem.rate * quantity
        }
    }

    fun showModifierDialog(menuItem: MenuItem) {
        _selectedMenuItemForModifier.value = menuItem
        _showModifierDialog.value = true
        loadModifiersForMenuItem(menuItem.item_cat_id)
    }

    fun hideModifierDialog() {
        _showModifierDialog.value = false
        _selectedMenuItemForModifier.value = null
        _modifierGroups.value = emptyList()
    }

    private fun loadModifierGroups(menuItemId: Long) {
        viewModelScope.launch {
            modifierRepository.getModifierGroupsForMenuItem(menuItemId).collect { result ->
                result.fold(
                    onSuccess = { groups ->
                        _modifierGroups.value = groups
                    },
                    onFailure = { error ->
                        Timber.e(error, "Failed to load modifier groups")
                        _modifierGroups.value = emptyList()
                    }
                )
            }
        }
    }

    fun addMenuItemWithModifiers(menuItem: MenuItem, modifiers: List<Modifiers>) {
        val currentItems = _selectedItems.value.toMutableMap()
        val existingCount = currentItems[menuItem] ?: 0
        currentItems[menuItem] = existingCount + 1
        _selectedItems.value = currentItems
        hideModifierDialog()
    }

    fun loadModifiersForMenuItem(menuItemId: Long) {
        viewModelScope.launch {
            try {
                modifierRepository.getModifierGroupsForMenuItem(menuItemId).collect { result ->
                    result.fold(
                        onSuccess = { modifiers ->
                            _modifierGroups.value = modifiers
                        },
                        onFailure = { error ->
                            Timber.e(error, "Failed to load modifiers for menu item")
                            _modifierGroups.value = emptyList()
                        }
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading modifiers")
                _modifierGroups.value = emptyList()
            }
        }
    }

    // Function to select a modifier for a menu item
    fun selectModifier(menuItemId: Long, modifier: Modifiers) {
        val currentSelection = _selectedModifiers.value.toMutableMap()
        val selectedList = currentSelection[menuItemId]?.toMutableList() ?: mutableListOf()

        if (!selectedList.contains(modifier)) {
            selectedList.add(modifier)
        } else {
            selectedList.remove(modifier)
        }

        currentSelection[menuItemId] = selectedList
        _selectedModifiers.value = currentSelection
    }

    // Function to clear selected modifiers for a menu item
    fun clearSelectedModifiers(menuItem: MenuItem) {
        val currentSelection = _selectedModifiers.value.toMutableMap()
        currentSelection.remove(menuItem.menu_item_id)
        _selectedModifiers.value = currentSelection
    }


}