package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.model.MenuItem // Assuming MenuItem model
// Import other necessary models like PaymentMethod, TransactionResult, etc.
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID // For generating a unique transaction ID
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
    data class Success(val order: com.warriortech.resb.ui.viewmodel.Order, val transactionId: String) : PaymentProcessingState
    data class Error(val message: String) : PaymentProcessingState
}

// Represents the overall UI state for Billing and Payment
data class BillingPaymentUiState(
    // Billing Details (from your existing ViewModel)
    val billedItems: Map<MenuItem, Int> = emptyMap(),
    val tableStatus: String = "TABLE", // e.g., "AC", "TAKEAWAY"
    val taxPercentage: Double = 5.0, // Default GST
    val discountFlat: Double = 0.0,
    val subtotal: Double = 0.0,
    val taxAmount: Double = 0.0,
    val totalAmount: Double = 0.0,

    // Payment Details
    val availablePaymentMethods: List<PaymentMethod> = emptyList(),
    val selectedPaymentMethod: PaymentMethod? = null,
    val amountToPay: Double = 0.0, // Could be same as totalAmount or partial
    val paymentProcessingState: PaymentProcessingState = PaymentProcessingState.Idle,

    // General
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
    // Inject Repositories or UseCases for fetching payment methods, processing payments, saving orders
    // private val paymentRepository: PaymentRepository,
    // private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BillingPaymentUiState())
    val uiState: StateFlow<BillingPaymentUiState> = _uiState.asStateFlow()

    init {
        // Load initial data like available payment methods
        loadAvailablePaymentMethods()

        // If billing details are passed via navigation, load them
        // Example: val orderId = savedStateHandle.get<Long>("orderId")
        // if (orderId != null) { loadBillingDetailsForOrder(orderId) }
    }

    // --- Billing Functions (Adapted from your existing ViewModel) ---
    fun setBillingDetails(
        items: Map<MenuItem, Int>,
        status: String,
        discount: Double? = null, // Allow null to use current or default
        tax: Double? = null
    ) {
        _uiState.update { currentState ->
            val newDiscount = discount ?: currentState.discountFlat
            val newTaxPercentage = tax ?: currentState.taxPercentage

            val newSubtotal = calculateSubtotal(items, status)
            val newTaxAmount = calculateTaxAmount(newSubtotal, newTaxPercentage)
            val newTotalAmount = calculateTotal(newSubtotal, newTaxAmount, newDiscount)

            currentState.copy(
                billedItems = items,
                tableStatus = status,
                discountFlat = newDiscount,
                taxPercentage = newTaxPercentage,
                subtotal = newSubtotal,
                taxAmount = newTaxAmount,
                totalAmount = newTotalAmount,
                amountToPay = newTotalAmount // Default amount to pay is the total
            )
        }
    }

    fun updateTaxPercentage(tax: Double) {
        _uiState.update { currentState ->
            val newTaxAmount = calculateTaxAmount(currentState.subtotal, tax)
            val newTotalAmount = calculateTotal(currentState.subtotal, newTaxAmount, currentState.discountFlat)
            currentState.copy(
                taxPercentage = tax,
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

    private fun calculateSubtotal(items: Map<MenuItem, Int>, tableStatus: String): Double {
        return items.entries.sumOf { (item, qty) ->
            val rate = when (tableStatus) {
                "AC" -> item.ac_rate
                "TAKEAWAY", "DELIVERY" -> item.parcel_rate
                else -> item.rate
            }
            qty * rate
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
                    PaymentMethod("cash", "Cash"),
                    PaymentMethod("card", "Credit/Debit Card"),
                    PaymentMethod("upi", "UPI / QR Code"),
                    PaymentMethod("due", "Credit/Due"),
                    PaymentMethod("others", "Others")
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

    fun resetPaymentState() {
        _uiState.update { it.copy(paymentProcessingState = PaymentProcessingState.Idle, errorMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
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