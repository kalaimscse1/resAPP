package com.warriortech.resb.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.BillRepository
import com.warriortech.resb.data.repository.OrderRepository
import com.warriortech.resb.data.repository.calculateGst
import com.warriortech.resb.data.repository.calculateGstAndCess
import com.warriortech.resb.model.Bill
import com.warriortech.resb.model.BillItem
import com.warriortech.resb.model.KOTRequest
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.Order
import com.warriortech.resb.model.TblMenuItemResponse
import com.warriortech.resb.model.TblOrderDetailsResponse
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.service.PrintService
import com.warriortech.resb.ui.viewmodel.MenuViewModel.OrderUiState
import com.warriortech.resb.util.CurrencySettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

// --- Data Models (Place in your 'model' package) ---
// These are illustrative. Define them based on your actual needs.

data class PaymentMethod(
    val id: String,
    val name: String, // e.g., "Cash", "Credit Card", "UPI"
    val iconResId: Int? = null // Optional: for displaying an icon
)

// Represents the state of a payment attempt
sealed interface PaymentProcessingState {
    object Idle : PaymentProcessingState
    object Processing : PaymentProcessingState
    data class Success(val order:PaidOrder, val transactionId: String) : PaymentProcessingState
    data class Error(val message: String) : PaymentProcessingState
}

// Represents the overall UI state for Billing and Payment
data class BillingPaymentUiState(
    // Billing Details (from your existing ViewModel)
    val billedItems: Map<TblMenuItemResponse, Int> = emptyMap(),
    val tableStatus: String = "TABLE", // Default GST
    val discountFlat: Double = 0.0,
    val cessSpecific: Double=0.0,
    val cessAmount: Double = 0.0, // Cess percentage if applicable
    val subtotal: Double = 0.0,
    val taxAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val orderDetails: List<TblOrderDetailsResponse> = emptyList(),
    val orderMasterId: String? = null,
    val discount: Double = 0.0, // Flat discount amount
    val selectedKotNumber: Int? = null,
    // Payment Details
    val availablePaymentMethods: List<PaymentMethod> = emptyList(),
    val selectedPaymentMethod: PaymentMethod? = null,
    val amountToPay: Double = 0.0, // Could be same as totalAmount or partial
    val paymentProcessingState: PaymentProcessingState = PaymentProcessingState.Idle,
    val amountReceived: Double = 0.0,
    val changeAmount: Double = 0.0,
    // General
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val billRepository: BillRepository,
    private val orderRepository: OrderRepository,
    private val printService: PrintService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BillingPaymentUiState())
    val uiState: StateFlow<BillingPaymentUiState> = _uiState.asStateFlow()


    private val _originalOrderDetails = MutableStateFlow<List<TblOrderDetailsResponse>>(emptyList())
    private val _filteredOrderDetails = MutableStateFlow<List<TblOrderDetailsResponse>>(emptyList())

    init {
        // Load initial data like available payment methods
        loadAvailablePaymentMethods()
        CurrencySettings.update(symbol = sessionManager.getRestaurantProfile()?.currency?:"", decimals = sessionManager.getRestaurantProfile()?.decimal_point?.toInt() ?: 2)
    }

    /**
     * Central function to recalc totals based on billed items
     */
    private fun recalcTotals(items: Map<TblMenuItemResponse, Int>): BillingPaymentUiState {
        val subtotal = items.entries.sumOf { (menuItem, qty) -> menuItem.rate * qty }
        val taxAmount = items.entries.sumOf { (menuItem, qty) ->
            val gst = calculateGst(menuItem.actual_rate, menuItem.tax_percentage.toDouble(), true,menuItem.tax_percentage.toDouble()/2,menuItem.tax_percentage.toDouble()/2)
            Log.d("GSTCALC", "recalcTotals: $gst ${menuItem.actual_rate}")
            gst.gstAmount * qty
//            uiState.value.taxAmount * qty
        }

        Log.d("GSTCALC", "recalcTotals: $taxAmount")
        val cessAmount = items.entries.sumOf { (menuItem, qty) ->
            if (menuItem.is_inventory ==1L && menuItem.cess_specific!=0.00)
            {
                menuItem.cess_specific * qty
            }
            else 0.0
        }
        val cessSpecific = items.entries.sumOf { (menuItem, qty) ->
            if (menuItem.is_inventory ==1L) {
                (menuItem.actual_rate * qty) * (menuItem.cess_per.toDoubleOrNull() ?: 0.0) / 100.0
            } else 0.0
        }

        val discountFlat = _uiState.value.discountFlat
        val totalAmount = subtotal + taxAmount + cessAmount + cessSpecific - discountFlat

        return _uiState.value.copy(
            billedItems = items,
            subtotal = subtotal,
            taxAmount = taxAmount,
            cessAmount = cessAmount,
            cessSpecific = cessSpecific,
            totalAmount = totalAmount,
            amountToPay = totalAmount
        )
    }

    fun setBillingDetailsFromOrderResponse(
        orderDetails: List<TblOrderDetailsResponse>,
        orderMasterId: String
    ) {
        viewModelScope.launch {
            val order = orderRepository.getOrdersByOrderId(orderMasterId)
            if (order.body() != null) {
                val orderDetailsResponse = order.body()!!

                // This function sets billing details from an existing order response
                // Set billing details from TblOrderDetailsResponse

                _originalOrderDetails.value = orderDetailsResponse
                _filteredOrderDetails.value = orderDetailsResponse
                val menuItems=orderDetailsResponse.map{

                    TblMenuItemResponse(
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
                        is_favourite = it.menuItem.is_favourite,
                        menu_item_code = it.menuItem.menu_item_code,
                        menu_id = it.menuItem.menu_id,
                        menu_name = it.menuItem.menu_name,
                        is_active = it.menuItem.is_active,
                        preparation_time = it.menuItem.preparation_time,
                        actual_rate = it.rate
                    )
                }
                // Convert TblOrderDetailsResponse to Map<MenuItem, Int> for existing billing logic
                val itemsMap = menuItems.associateWith { it.qty }.toMutableMap()
                var tableStatus = "TABLE" // Default

                // Calculate totals from order details
                val subtotal = orderDetailsResponse.sumOf { it.total }
                val taxAmount = orderDetailsResponse.sumOf { it.tax_amount }
                val cessAmount = orderDetailsResponse.sumOf { if (it.cess>0) it.cess else 0.0 }
                val cessSpecific = orderDetailsResponse.sumOf { if (it.cess_specific>0) it.cess_specific else 0.0 }
                val totalAmount = subtotal + taxAmount + cessAmount + cessSpecific

                if (cessAmount>0.0){
                    _uiState.update { currentState ->
                        currentState.copy(
                            billedItems = itemsMap,
                            tableStatus = tableStatus,
                            subtotal = subtotal,
                            taxAmount = taxAmount,
                            totalAmount = totalAmount,
                            amountToPay = totalAmount,
                            orderDetails = orderDetails,
                            orderMasterId = orderMasterId,
                            cessAmount = cessAmount,
                            cessSpecific = cessSpecific
                        )
                    }
                }else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            billedItems = itemsMap,
                            tableStatus = tableStatus,
                            subtotal = subtotal,
                            taxAmount = taxAmount,
                            totalAmount = totalAmount,
                            amountToPay = totalAmount,
                            orderDetails = orderDetails,
                            orderMasterId = orderMasterId
                        )
                    }
                }

            } else {
                _uiState.update { it.copy(errorMessage = "Order not found") }
            }
        }

    }

    fun filterByKotNumber(kotNumber: Int) {
        val filtered = if (kotNumber == -1) {
            _originalOrderDetails.value
        } else {
            _originalOrderDetails.value.filter { it.kot_number == kotNumber }
        }

        _filteredOrderDetails.value = filtered

        val itemsMap = mutableMapOf<TblMenuItemResponse, Int>()
        filtered.forEach { detail ->
            val existingQty = itemsMap[detail.menuItem] ?: 0
            itemsMap[detail.menuItem] = existingQty + detail.qty
        }

        _uiState.value = recalcTotals(itemsMap).copy(
            selectedKotNumber = if (kotNumber == -1) null else kotNumber
        )
    }

    fun updateKotItem(
        orderDetailId: Long,
        newQuantity: Int,
        newRate: Double
    ) {
        // Update order detail
        val updatedDetails = _originalOrderDetails.value.map { detail ->
            if (detail.order_details_id == orderDetailId) {
                detail.copy(
                    qty = newQuantity,
                    rate = newRate,
                    total = newQuantity * newRate
                )
            } else detail
        }
        _originalOrderDetails.value = updatedDetails

        // Build billed items map again
        val itemsMap = mutableMapOf<TblMenuItemResponse, Int>()
        updatedDetails.forEach { detail ->
            val menuItem = detail.menuItem.copy(rate = detail.rate, qty = detail.qty)
            itemsMap[menuItem] = detail.qty
        }

        // Recalculate totals
        _uiState.value = recalcTotals(itemsMap)
    }

    fun updateItemQuantity(menuItem: TblMenuItemResponse, newQuantity: Int) {
        val currentItems = _uiState.value.billedItems.toMutableMap()
        if (newQuantity > 0) {
            currentItems[menuItem] = newQuantity
        } else {
            currentItems.remove(menuItem)
        }
        _uiState.value = recalcTotals(currentItems)
    }

    fun removeItem(menuItem: TblMenuItemResponse) {
        val currentItems = _uiState.value.billedItems.toMutableMap()
        currentItems.remove(menuItem)
        _uiState.value = recalcTotals(currentItems)
    }

    fun updateTaxPercentage(tax: Double) {
        _uiState.update { currentState ->
            val newTaxAmount = calculateTaxAmount(currentState.subtotal, tax)
            val newTotalAmount = calculateTotal(currentState.subtotal, newTaxAmount, currentState.discountFlat)
            currentState.copy(
                taxAmount = newTaxAmount,
                totalAmount = newTotalAmount,
                amountToPay = newTotalAmount
            )
        }
    }

    fun updateDiscountFlat(discount: Double) {
        _uiState.update { currentState ->
            val newTotalAmount = calculateTotal(currentState.subtotal, currentState.taxAmount, discount)
            currentState.copy(
                discountFlat = discount,
                totalAmount = newTotalAmount,
                amountToPay = newTotalAmount
            )
        }
    }

    private fun calculateTaxAmount(subtotal: Double, taxPercentage: Double): Double {
        return subtotal * (taxPercentage / 100)
    }

    private fun calculateTotal(subtotal: Double, taxAmount: Double, discountFlat: Double): Double {
        return subtotal + taxAmount - discountFlat
    }

    // --- Payment Functions ---

    private fun loadAvailablePaymentMethods() {
        // In a real app, fetch this from a repository or remote source
        _uiState.update {
            it.copy(
                availablePaymentMethods = listOf(
                    PaymentMethod("cash", "CASH"),
                    PaymentMethod("card", "CARD"),
                    PaymentMethod("upi", "UPI"),
                    PaymentMethod("due", "DUE"),
                    PaymentMethod("others", "OTHERS")
                )
            )
        }
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = method) }
    }

    fun updateAmountToPay(amount: Double) {
        // Add validation if needed (e.g., amount <= total due)
        _uiState.update { it.copy(amountToPay = amount) }
    }
    fun updateOrderMasterId(orderId: String) {
        // Add validation if needed (e.g., amount <= total due)
        _uiState.update { it.copy(orderMasterId = orderId) }
    }

    fun processPayment() {
        val currentState = _uiState.value
        val paymentMethod = currentState.selectedPaymentMethod
        val amount = currentState.amountToPay

        if (paymentMethod == null) {
            _uiState.update { it.copy(errorMessage = "Please select a payment method.") }
            return
        }
        if (amount <= 0) {
            _uiState.update { it.copy(errorMessage = "Payment amount must be greater than zero.") }
            return
        }

        _uiState.update { it.copy(paymentProcessingState = PaymentProcessingState.Processing, errorMessage = null) }
        viewModelScope.launch {

                // --- Simulate Payment Processing ---
                // In a real app, this would involve:
                // 1. Calling a payment gateway SDK or your backend API.
                // 2. Handling success/failure responses.
                // 3. If successful, creating an Order record and saving it.

                billRepository.bill(
                    orderMasterId = currentState.orderMasterId ?:"",
                    paymentMethod = paymentMethod,
                    receivedAmt = currentState.amountToPay,
                    customer = null // Assuming no customer details for now
                ).collect { result->
                    result.fold(
                        onSuccess = { response ->
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
//                            billRepository.updateTablAndOrderStatus(
//                                orderMasterId = response.order_master.order_master_id,
//                                tableId = response.order_master.table_id
//                            )
                            val data = currentState
                            val isReceipt = sessionManager.getGeneralSetting()?.is_receipt ?: false

                            if (isReceipt) {
                                printBill(billDetails, data, amount, paymentMethod)
                            }
                            else{
                                delay(2000) // Simulate network delay

                                // Example: If payment is successful
                                val transactionId = UUID.randomUUID().toString()
                                val paidOrder = PaidOrder(
                                    // orderId = generateNewOrderId(), // From repository
                                    items = currentState.billedItems,
                                    tableStatus = currentState.tableStatus,
                                    subtotal = currentState.subtotal,
                                    taxAmount = currentState.taxAmount,
                                    discount = currentState.discountFlat,
                                    totalAmount = currentState.totalAmount,
                                    paidAmount = amount,
                                    paymentMethod = paymentMethod.name,
                                    transactionId = transactionId,
                                    timestamp = System.currentTimeMillis()
                                )
                                Log.d("Payment", "Payment successful$paidOrder")

                                _uiState.update {
                                    it.copy(paymentProcessingState = PaymentProcessingState.Success(paidOrder, transactionId))
                                }

                            }
                            // Handle successful payment response
                            // For example, you might want to update the UI or save the order
                            Log.d("Payment", "Payment successful")
                        },
                        onFailure = { error ->
                            // Handle payment failure
                            Log.e("Payment", "Payment failed: ${error.message}")
                            _uiState.update {
                                it.copy(paymentProcessingState = PaymentProcessingState.Error("Payment failed: ${error.message}"))
                            }
                        }
                    )
                }
                // orderRepository.saveOrder(paidOrder) // Save the order
        }
    }
     fun printBill(bill : Bill, currentState: BillingPaymentUiState, amount: Double, paymentMethod: PaymentMethod) {
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
                                val paidOrder = PaidOrder(
                                    // orderId = generateNewOrderId(), // From repository
                                    items = currentState.billedItems,
                                    tableStatus = currentState.tableStatus,
                                    subtotal = currentState.subtotal,
                                    taxAmount = currentState.taxAmount,
                                    discount = currentState.discountFlat,
                                    totalAmount = currentState.totalAmount,
                                    paidAmount = amount,
                                    paymentMethod = paymentMethod.name,
                                    transactionId = transactionId,
                                    timestamp = System.currentTimeMillis()
                                )
                                _uiState.update {
                                    it.copy(paymentProcessingState = PaymentProcessingState.Success(paidOrder, transactionId))
                                }
                            },
                            onFailure = { error ->
                                _uiState.update { it.copy(errorMessage = "Failed to print bill: ${error.message}") }
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
                    val paidOrder = PaidOrder(
                        // orderId = generateNewOrderId(), // From repository
                        items = currentState.billedItems,
                        tableStatus = currentState.tableStatus,
                        subtotal = currentState.subtotal,
                        taxAmount = currentState.taxAmount,
                        discount = currentState.discountFlat,
                        totalAmount = currentState.totalAmount,
                        paidAmount = amount,
                        paymentMethod = paymentMethod.name,
                        transactionId = transactionId,
                        timestamp = System.currentTimeMillis()
                    )
                    Log.d("Payment", "Payment successful$paidOrder")
                    _uiState.update {
                        it.copy(paymentProcessingState = PaymentProcessingState.Success(paidOrder, transactionId))
                    }
                }

        }
    }


    fun updatePaymentMethod(paymentMethodName: String) {
        val paymentMethod = _uiState.value.availablePaymentMethods.find { it.name == paymentMethodName }
        _uiState.update { it.copy(selectedPaymentMethod = paymentMethod) }
    }

    fun resetPaymentState() {
        _uiState.update { it.copy(paymentProcessingState = PaymentProcessingState.Idle, errorMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun updateAmountReceived(amount: Double) {
        _uiState.update { currentState ->
            val change = amount - currentState.totalAmount
            currentState.copy(
                amountReceived = amount,
                changeAmount = maxOf(0.0, change)
            )
        }
    }

    fun updateDiscount(discount: Double) {
        _uiState.value = _uiState.value.copy(discount = discount)
    }

//    fun updateItemQuantity(menuItem: TblMenuItemResponse, newQuantity: Int) {
//        val currentItems = _uiState.value.billedItems.toMutableMap()
//        if (newQuantity > 0) {
//            currentItems[menuItem] = newQuantity
//        } else {
//            currentItems.remove(menuItem)
//        }
//        _uiState.value = _uiState.value.copy(billedItems = currentItems)
//    }
//
//    fun removeItem(menuItem: TblMenuItemResponse) {
//        val currentItems = _uiState.value.billedItems.toMutableMap()
//        currentItems.remove(menuItem)
//        _uiState.value = _uiState.value.copy(billedItems = currentItems)
//    }
}

// Assume Order model exists (simplified)
data class PaidOrder(
    // val orderId: String,
    val items: Map<TblMenuItemResponse, Int>,
    val tableStatus: String,
    val subtotal: Double,
    val taxAmount: Double,
    val discount: Double,
    val totalAmount: Double,
    val paidAmount: Double,
    val paymentMethod: String,
    val transactionId: String,
    val timestamp: Long
)