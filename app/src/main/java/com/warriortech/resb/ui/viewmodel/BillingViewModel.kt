package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.BillRepository
import com.warriortech.resb.data.repository.OrderRepository
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.TblOrderDetailsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

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
    data class Success(val order:Order, val transactionId: String) : PaymentProcessingState
    data class Error(val message: String) : PaymentProcessingState
}

// Represents the overall UI state for Billing and Payment
data class BillingPaymentUiState(
    // Billing Details (from your existing ViewModel)
    val billedItems: Map<MenuItem, Int> = emptyMap(),
    val tableStatus: String = "TABLE", // Default GST
    val discountFlat: Double = 0.0,
    val cessSpecific: Double=0.0,
    val cessAmount: Double = 0.0, // Cess percentage if applicable
    val subtotal: Double = 0.0,
    val taxAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val orderDetails: List<TblOrderDetailsResponse> = emptyList(),
    val orderMasterId: Long? = null,
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
    private val printService: com.warriortech.resb.service.PrintService
) : ViewModel() {

    private val _uiState = MutableStateFlow(BillingPaymentUiState())
    val uiState: StateFlow<BillingPaymentUiState> = _uiState.asStateFlow()

    private val _originalOrderDetails = MutableStateFlow<List<TblOrderDetailsResponse>>(emptyList())
    private val _filteredOrderDetails = MutableStateFlow<List<TblOrderDetailsResponse>>(emptyList())

    init {
        // Load initial data like available payment methods
        loadAvailablePaymentMethods()
    }


    fun setBillingDetailsFromOrderResponse(
        orderDetails: List<TblOrderDetailsResponse>,
        orderMasterId: Long
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
                        cess_per = it.cess_per.toString()
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
            _originalOrderDetails.value // Show all items
        } else {
            _originalOrderDetails.value.filter { it.kot_number == kotNumber }
        }

        _filteredOrderDetails.value = filtered

        // Recalculate billing details for filtered items
        val itemsMap = mutableMapOf<MenuItem, Int>()
        filtered.forEach { detail ->
            val existingQty = itemsMap[detail.menuItem] ?: 0
            itemsMap[detail.menuItem] = existingQty + detail.qty
        }

        val subtotal = filtered.sumOf { it.total }
        val taxAmount = filtered.sumOf { it.tax_amount }
        val totalAmount = subtotal + taxAmount

        _uiState.update { currentState ->
            currentState.copy(
                billedItems = itemsMap,
                subtotal = subtotal,
                taxAmount = taxAmount,
                totalAmount = totalAmount,
                amountToPay = totalAmount,
                selectedKotNumber = if (kotNumber == -1) null else kotNumber
            )
        }
    }

    fun updateKotItem(
        orderDetailId: Long,
        newQuantity: Int,
        newRate: Double
    ) {
        // Update specific order detail item
        val updatedDetails = _originalOrderDetails.value.map { detail ->
            if (detail.order_details_id == orderDetailId) {
                detail.copy(
                    qty = newQuantity,
                    rate = newRate,
                    total = newQuantity * newRate
                )
            } else {
                detail
            }
        }

        _originalOrderDetails.value = updatedDetails

        // Refresh filtered details and billing calculation
        val currentKot = _uiState.value.selectedKotNumber
        if (currentKot != null) {
            filterByKotNumber(currentKot)
        } else {
            filterByKotNumber(-1) // Show all
        }
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
                    PaymentMethod("online", "ONLINE"),
                    PaymentMethod("others", "OTHERS")
                    // Add more methods
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
            try {
                // --- Simulate Payment Processing ---
                // In a real app, this would involve:
                // 1. Calling a payment gateway SDK or your backend API.
                // 2. Handling success/failure responses.
                // 3. If successful, creating an Order record and saving it.
                kotlinx.coroutines.delay(2000) // Simulate network delay

                // Example: If payment is successful
                val transactionId = UUID.randomUUID().toString()
                val paidOrder = Order(
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
                // orderRepository.saveOrder(paidOrder) // Save the order

                _uiState.update {
                    it.copy(paymentProcessingState = PaymentProcessingState.Success(paidOrder, transactionId))
                }

            } catch (e: Exception) {
                // Log the exception e
                _uiState.update {
                    it.copy(paymentProcessingState = PaymentProcessingState.Error("Payment failed: ${e.message}"))
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
}

// Assume Order model exists (simplified)
data class Order(
    // val orderId: String,
    val items: Map<MenuItem, Int>,
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