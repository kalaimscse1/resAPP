package com.warriortech.resb.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.BillRepository
import com.warriortech.resb.data.repository.MenuItemRepository
import com.warriortech.resb.data.repository.ModifierRepository
import com.warriortech.resb.data.repository.OrderRepository
import com.warriortech.resb.model.Bill
import com.warriortech.resb.model.BillItem
import com.warriortech.resb.model.Counters
import com.warriortech.resb.model.KOTItem
import com.warriortech.resb.model.KOTRequest
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.Modifiers
import com.warriortech.resb.model.OrderItem
import com.warriortech.resb.model.TblMenuItemResponse
import com.warriortech.resb.model.TblOrderDetailsResponse
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.ui.viewmodel.MenuViewModel.OrderUiState
import com.warriortech.resb.util.getCurrentTimeAsFloat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import com.warriortech.resb.ui.viewmodel.BillingViewModel
import com.warriortech.resb.util.CurrencySettings

@HiltViewModel
class CounterViewModel @Inject constructor(
    private val menuRepository: MenuItemRepository,
    private val orderRepository: OrderRepository,
    private val billRepository: BillRepository,
    private val modifierRepository: ModifierRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _menuState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val menuState: StateFlow<MenuUiState> = _menuState.asStateFlow()

    private val _selectedItems = MutableStateFlow<Map<TblMenuItemResponse, Int>>(emptyMap())
    val selectedItems: StateFlow<Map<TblMenuItemResponse, Int>> = _selectedItems.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    val selectedCategory = MutableStateFlow<String?>(null)
    
    private val _currentCounter = MutableStateFlow<Counters?>(null)
    val currentCounter: StateFlow<Counters?> = _currentCounter.asStateFlow()

    // Cache for menu items to avoid repeated API calls
    private var cachedMenuItems: List<TblMenuItemResponse>? = null
    private var lastLoadTime = 0L
    private val cacheValidityMs = 5 * 60 * 1000L // 5 minutes

    private val _selectedMenuItemForModifier = MutableStateFlow<TblMenuItemResponse?>(null)
    val selectedMenuItemForModifier: StateFlow<TblMenuItemResponse?> =
        _selectedMenuItemForModifier.asStateFlow()

    private val _showModifierDialog = MutableStateFlow<Boolean>(false)
    val showModifierDialog: StateFlow<Boolean> = _showModifierDialog.asStateFlow()

    private val _modifierGroups = MutableStateFlow<List<Modifiers>>(emptyList())
    val modifierGroups: StateFlow<List<Modifiers>> = _modifierGroups.asStateFlow()

    private val _selectedModifiers = MutableStateFlow<Map<Long, List<Modifiers>>>(emptyMap())
    val selectedModifiers: StateFlow<Map<Long, List<Modifiers>>> = _selectedModifiers.asStateFlow()

    val orderDetailsResponse = MutableStateFlow<List<TblOrderDetailsResponse>>(emptyList())
    val orderId = MutableStateFlow<String?>(null)


    sealed class MenuUiState {
        object Loading : MenuUiState()
        data class Success(val menuItems: List<TblMenuItemResponse>) : MenuUiState()
        data class Error(val message: String) : MenuUiState()
    }

    init {
        CurrencySettings.update(symbol = sessionManager.getRestaurantProfile()?.currency?:"", decimals = sessionManager.getRestaurantProfile()?.decimal_point?.toInt() ?: 2)

    }
//    fun loadMenuItems() {
//        viewModelScope.launch {
//            try {
//                _menuState.value = MenuUiState.Loading
//                val menuItems = menuRepository.getMenuItems()
//                menuItems.collect{
//                        result->
//                    result.fold(
//                        onSuccess = { menuItems ->
//                            _menuState.value = MenuUiState.Success(menuItems)
//
//                            // Extract unique categories
//                            val data = buildList {
//                                add("FAVOURITES")
//                                addAll(menuItems.map { it.item_cat_name }.distinct())
//                            }
//                            _categories.value = data
//
//                            // Set first category as selected if available
//                            if (data.isNotEmpty() && selectedCategory.value == null) {
//                                selectedCategory.value = data.first()
//                            }
//                        },
//                        onFailure = { error ->
//                            _menuState.value =
//                                MenuUiState.Error(error.message ?: "Failed to load menu items")
//                        }
//
//                    )
//                }
//            } catch (e: Exception) {
//                _menuState.value = MenuUiState.Error(e.message ?: "Failed to load menu items")
//            }
//        }
//    }

    private fun isCacheValid(): Boolean {
        return cachedMenuItems != null && 
               (System.currentTimeMillis() - lastLoadTime) < cacheValidityMs
    }

    fun loadMenuItems(category: String? = null) {
        // Use cached data if available and valid
        if (isCacheValid() && category == null) {
            cachedMenuItems?.let { menuItems ->
                _menuState.value = MenuUiState.Success(menuItems)
                updateCategories(menuItems)
                return
            }
        }

        viewModelScope.launch {
            _menuState.value = MenuUiState.Loading
            
            withContext(Dispatchers.IO) {
                try {
                    val menus = menuRepository.getMenus().associateBy { it.menu_id }
                    menuRepository.getMenuItems(category).collect { result ->
                        result.fold(
                            onSuccess = { menuItems ->
                                val processedItems = withContext(Dispatchers.Default) {
                                    processMenuItems(menuItems, menus)
                                }
                                
                                withContext(Dispatchers.Main) {
                                    cachedMenuItems = processedItems
                                    lastLoadTime = System.currentTimeMillis()
                                    lastSuccessfulMenuItems = processedItems
                                    _menuState.value = MenuUiState.Success(processedItems)
                                    updateCategories(processedItems)
                                }
                            },
                            onFailure = { error ->
                                withContext(Dispatchers.Main) {
                                    _menuState.value = MenuUiState.Error(error.message ?: "Failed to load menu items")
                                }
                            }
                        )
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _menuState.value = MenuUiState.Error(e.message ?: "Failed to load menu items")
                    }
                }
            }
        }
    }

    private suspend fun processMenuItems(
        menuItems: List<TblMenuItemResponse>,
        menus: Map<Long, com.warriortech.resb.model.Menu>
    ): List<TblMenuItemResponse> = withContext(Dispatchers.Default) {
        val showMenu = sessionManager.getGeneralSetting()?.menu_show_in_time == true
        
        if (showMenu) {
            val currentTime = getCurrentTimeAsFloat()
            menuItems.filter { menuItem ->
                val menu = menus[menuItem.menu_id]
                val startTime = menu?.start_time ?: 0f
                val endTime = menu?.end_time ?: 24f
                currentTime in startTime..endTime
            }
        } else {
            menuItems
        }
    }

    private fun updateCategories(menuItems: List<TblMenuItemResponse>) {
        val data = buildList {
            add("FAVOURITES")
            add("ALL")
            addAll(menuItems.map { it.item_cat_name }.distinct())
        }
        _categories.value = data
        if (selectedCategory.value == null) {
            selectedCategory.value = data.firstOrNull()
        }
    }

    // Optimized item operations with debouncing
    private var lastAddTime = 0L
    private val debounceMs = 100L

    fun addItemToOrder(menuItem: TblMenuItemResponse) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAddTime < debounceMs) return
        lastAddTime = currentTime
        
        val currentItems = _selectedItems.value.toMutableMap()
        val currentQuantity = currentItems[menuItem] ?: 0
        currentItems[menuItem] = currentQuantity + 1
        _selectedItems.value = currentItems
    }

    fun removeItemFromOrder(menuItem: TblMenuItemResponse) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAddTime < debounceMs) return
        lastAddTime = currentTime
        
        val currentItems = _selectedItems.value.toMutableMap()
        val currentQuantity = currentItems[menuItem] ?: 0
        
        if (currentQuantity > 1) {
            currentItems[menuItem] = currentQuantity - 1
        } else {
            currentItems.remove(menuItem)
        }
        
        _selectedItems.value = currentItems
    }

    fun showModifierDialog(menuItem: TblMenuItemResponse) {
        _selectedMenuItemForModifier.value = menuItem
        _showModifierDialog.value = true
        loadModifiersForMenuItem(menuItem.item_cat_id)
    }

    fun hideModifierDialog() {
        _showModifierDialog.value = false
        _selectedMenuItemForModifier.value = null
        _modifierGroups.value = emptyList()
    }

    fun addMenuItemWithModifiers(menuItem: TblMenuItemResponse, modifiers: List<Modifiers>) {
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

    fun clearOrder() {
        _selectedItems.value = emptyMap()
    }
    
    private var lastSuccessfulMenuItems: List<TblMenuItemResponse> = emptyList()
    
    fun resetToSuccessState() {
        // Reset to success state with the last known menu items
        if (lastSuccessfulMenuItems.isNotEmpty()) {
            _menuState.value = MenuUiState.Success(lastSuccessfulMenuItems)
        } else {
            // If no previous menu items, reload them
            loadMenuItems()
        }
    }
    
    fun setCurrentCounter(counter: Counters) {
        _currentCounter.value = counter
    }

    fun getOrderTotal(): Double {
        return _selectedItems.value.entries.sumOf { (menuItem, quantity) ->
            menuItem.rate * quantity
        }
    }


    fun placeOrder(tableId: Long, tableStatus1: String?) {
        val list = mutableListOf<TblOrderDetailsResponse>()
        viewModelScope.launch {
            if (_selectedItems.value.isEmpty()) {

                _menuState.value = MenuUiState.Error("No items selected")
                return@launch
            }
                val orderItems = _selectedItems.value.map { (menuItem, quantity) ->
                    OrderItem(
                        quantity = quantity,
                        menuItem = menuItem,
                    )
                }
            _menuState.value = MenuUiState.Loading
                orderRepository.placeOrUpdateOrders(
                    tableId, orderItems,
                    tableStatus1.toString()
                ).collect { result ->
                    result.fold(
                        onSuccess = { order ->
                            list.addAll(order)
                            orderDetailsResponse.value= order
                            orderId.value = order.firstOrNull()?.order_master_id
                            _selectedItems.value = emptyMap() // Clear selected items after placing order
                        },
                        onFailure = { error ->
                            _menuState.value =
                                MenuUiState.Error(error.message ?: "Failed to place order")
                        }
                    )
                }
        }
    }

    // Optimized cash print bill with better error handling
    fun cashPrintBill() {
        viewModelScope.launch {
            if (_selectedItems.value.isEmpty()) {
                _menuState.value = MenuUiState.Error("No items selected")
                return@launch
            }
            
            withContext(Dispatchers.IO) {
                try {
                    val orderItems = _selectedItems.value.map { (menuItem, quantity) ->
                        OrderItem(quantity = quantity, menuItem = menuItem)
                    }
                    
                    withContext(Dispatchers.Main) {
                        _menuState.value = MenuUiState.Loading
                    }
                    
                    orderRepository.placeOrUpdateOrders(2, orderItems, "").collect { result ->
                        result.fold(
                            onSuccess = { order ->
                                val payment = PaymentMethod("cash", "CASH")
                                val amount = order.sumOf { it.grand_total }
                                
                                billRepository.bill(
                                    order.firstOrNull()?.order_master_id ?: "",
                                    payment, amount, null
                                ).collect { billResult ->
                                    billResult.fold(
                                        onSuccess = { response ->
                                            processBillResponse(response, amount, payment)
                                        },
                                        onFailure = { error ->
                                            Timber.e(error, "Failed to process bill")
                                            withContext(Dispatchers.Main) {
                                                _menuState.value = MenuUiState.Error("Failed to process payment")
                                            }
                                        }
                                    )
                                }
                            },
                            onFailure = { error ->
                                withContext(Dispatchers.Main) {
                                    _menuState.value = MenuUiState.Error(error.message ?: "Failed to place order")
                                }
                            }
                        )
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _menuState.value = MenuUiState.Error("Network error: ${e.message}")
                    }
                }
            }
        }
    }

    private suspend fun processBillResponse(
        response: com.warriortech.resb.model.TblBillingResponse,
        amount: Double,
        payment: PaymentMethod
    ) = withContext(Dispatchers.IO) {
        try {
            val orderDetails = orderRepository.getOrdersByOrderId(response.order_master.order_master_id).body()!!
            val counter = sessionManager.getUser()?.counter_name ?: "Counter1"
            
            val billItems = orderDetails.mapIndexed { index, detail ->
                val menuItem = detail.menuItem
                val qty = detail.qty
                BillItem(
                    sn = index + 1,
                    itemName = menuItem.menu_item_name,
                    qty = qty,
                    price = menuItem.rate,
                    basePrice = detail.rate,
                    amount = qty * menuItem.rate,
                    sgstPercent = menuItem.tax_percentage.toDouble() / 2,
                    cgstPercent = menuItem.tax_percentage.toDouble() / 2,
                    igstPercent = if (detail.igst > 0) menuItem.tax_percentage.toDouble() else 0.0,
                    cessPercent = if (detail.cess > 0) menuItem.cess_per.toDouble() else 0.0,
                    sgst = detail.sgst,
                    cgst = detail.cgst,
                    igst = if (detail.igst > 0) detail.igst else 0.0,
                    cess = if (detail.cess > 0) detail.cess else 0.0,
                    cess_specific = if (detail.cess_specific > 0) detail.cess_specific else 0.0,
                    taxPercent = menuItem.tax_percentage.toDouble(),
                    taxAmount = detail.tax_amount
                )
            }
            
            val billDetails = Bill(
                company_code = sessionManager.getCompanyCode() ?: "",
                billNo = response.bill_no,
                date = response.bill_date.toString(),
                time = response.bill_create_time.toString(),
                orderNo = response.order_master.order_master_id,
                counter = counter,
                tableNo = response.order_master.table_name,
                custName = "",
                custNo = "",
                custAddress = "",
                custGstin = "",
                items = billItems,
                subtotal = response.order_amt,
                deliveryCharge = 0.0,
                discount = response.disc_amt,
                roundOff = response.round_off,
                total = response.grand_total,
            )
            
            val isReceipt = sessionManager.getGeneralSetting()?.is_receipt ?: false
            
            if (isReceipt) {
                printBill(billDetails, amount, payment)
            }
            
            withContext(Dispatchers.Main) {
                orderDetailsResponse.value = orderDetails
                _selectedItems.value = emptyMap()
                _menuState.value = MenuUiState.Success(lastSuccessfulMenuItems)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to process bill response")
            withContext(Dispatchers.Main) {
                _menuState.value = MenuUiState.Error("Failed to process bill")
            }
        }
    }

    fun printBill(bill : Bill, amount: Double, paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val ip = orderRepository.getIpAddress("COUNTER")
                    billRepository.printBill(bill, ip).collect { result ->
                        result.fold(
                            onSuccess = { message ->
                                Timber.d("Bill printed successfully: $message")
                            },
                            onFailure = { error ->
                                Timber.e(error, "Failed to print bill")
                            }
                        )
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Print operation failed")
                }
            }
        }
    }
}