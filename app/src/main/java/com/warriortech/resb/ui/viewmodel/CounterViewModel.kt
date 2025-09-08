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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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


    fun loadMenuItems(category: String? = null) {
        viewModelScope.launch {
            _menuState.value = MenuUiState.Loading
            val menus = menuRepository.getMenus().associateBy { it.menu_id }
            menuRepository.getMenuItems(category).collect { result ->
                result.fold(
                    onSuccess = { menuItems ->
                        val showMenu = sessionManager.getGeneralSetting()?.menu_show_in_time==true
                        if (showMenu){
                            val currentTime = getCurrentTimeAsFloat()

                            val filteredMenuItems = menuItems.filter { menuItem ->
                                val menu = menus[menuItem.menu_id]
                                val startTime = menu?.start_time ?: 0f
                                val endTime = menu?.end_time ?: 24f
                                currentTime in startTime..endTime
                            }
                            lastSuccessfulMenuItems = filteredMenuItems
                            _menuState.value = MenuUiState.Success(filteredMenuItems)
                            val data = buildList {
                                add("FAVOURITES")
                                add("ALL")
                                addAll(filteredMenuItems.map { it.item_cat_name }.distinct())
                            }
                            _categories.value = data
//                        categories.value = menuItems.map { it.item_cat_name }.distinct().sorted()
                            selectedCategory.value = categories.value.firstOrNull()
                        }
                        else{
                            lastSuccessfulMenuItems = menuItems
                            _menuState.value = MenuUiState.Success(menuItems)
                            val data = buildList {
                                add("FAVOURITES")
                                add("ALL")
                                addAll(menuItems.map { it.item_cat_name }.distinct())
                            }
                            _categories.value = data
//                        categories.value = menuItems.map { it.item_cat_name }.distinct().sorted()
                            selectedCategory.value = categories.value.firstOrNull()
                        }
                    },
                    onFailure = { error ->
                        MenuUiState.Error(error.message ?: "Failed to load menu items")
                    }
                )
            }
        }
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

    fun addItemToOrder(menuItem: TblMenuItemResponse) {
        val currentItems = _selectedItems.value.toMutableMap()
        val currentQuantity = currentItems[menuItem] ?: 0
        currentItems[menuItem] = currentQuantity + 1
        _selectedItems.value = currentItems
    }

    fun removeItemFromOrder(menuItem: TblMenuItemResponse) {
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

    fun cashPrintBill() {
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
                2, orderItems,
                ""
            ).collect { result ->
                result.fold(
                    onSuccess = { order ->
                        val payment = PaymentMethod("cash", "CASH")
                        val amount = order.sumOf { it.grand_total }
                        billRepository.bill(order.firstOrNull()?.order_master_id?:"",
                            payment,amount,null).collect{ billResult->
                            billResult.fold(
                                onSuccess = {response ->
                                    var sn = 1
                                    val orderDetails = orderRepository.getOrdersByOrderId(response.order_master.order_master_id).body()!!
                                    val counter = sessionManager.getUser()?.counter_name ?: "Counter1"
                                    val billItems = orderDetails.map {detail ->
                                        val menuItem = detail.menuItem
                                        val qty = detail.qty
                                        BillItem(
                                            sn = sn++,
                                            itemName = menuItem.menu_item_name,
                                            qty = qty,
                                            price = menuItem.rate,
                                            amount = qty * menuItem.rate,
                                            sgstPercent = menuItem.tax_percentage.toDouble()/ 2,
                                            cgstPercent = menuItem.tax_percentage.toDouble()/ 2,
                                            igstPercent = if (detail.igst > 0) menuItem.tax_percentage.toDouble() else 0.0,
                                            cessPercent = if (detail.cess > 0) menuItem.cess_per.toDouble() else 0.0,
                                            sgst = detail.sgst,
                                            cgst = detail.cgst,
                                            igst = if (detail.igst> 0) detail.igst else 0.0,
                                            cess = if (detail.cess > 0) detail.cess else 0.0,
                                            cess_specific = if (detail.cess_specific > 0) detail.cess_specific else 0.0
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
                                        custName = "Customer Name",
                                        custNo = "1234567890",
                                        custAddress = "Customer Address",
                                        custGstin = "GSTIN123456",
                                        items = billItems,
                                        subtotal = response.order_amt,
                                        deliveryCharge = 0.0, // Assuming no delivery charge
                                        discount = response.disc_amt,
                                        roundOff = response.round_off,
                                        total = response.grand_total,
                                    )
                                    val isReceipt = sessionManager.getGeneralSetting()?.is_receipt ?: false

                                    if (isReceipt) {
                                        printBill(billDetails,  amount, payment)
                                        loadMenuItems()
                                    }
                                    else{
                                        loadMenuItems()
                                    }
                                },
                                onFailure = { error->
                                    Timber.e(error,"Failed to print bill")
                                }
                            )
                        }
                        orderDetailsResponse.value= order
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

    fun printBill(bill : Bill, amount: Double, paymentMethod: PaymentMethod) {
        viewModelScope.launch {

            val isReceipt = sessionManager.getGeneralSetting()?.is_receipt ?: false

            if (isReceipt) {
                val ip = orderRepository.getIpAddress("COUNTER")
                val printResponse = billRepository.printBill(bill, ip)
                // Optionally, you can reset the payment state after printing
                printResponse.collect { result->
                    result.fold(
                        onSuccess = { message ->
                            delay(2000) // Simulate network delay
                            // Example: If payment is successful
                            val transactionId = UUID.randomUUID().toString()

                            Timber.e(message)
                        },
                        onFailure = { error ->
                            Timber.e(error,"Failed to print bill")
                        }
                    )

                }
            }
            else
            {
                Log.d("Payment", "Payment successful")
//                    delay(2000) // Simulate network delay

                // Example: If payment is successful
                val transactionId = UUID.randomUUID().toString()


            }

        }
    }
}
