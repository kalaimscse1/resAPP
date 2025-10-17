package com.warriortech.resb.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.BillRepository
import com.warriortech.resb.data.repository.OrderRepository
import com.warriortech.resb.model.Bill
import com.warriortech.resb.model.BillItem
import com.warriortech.resb.model.TblBillingResponse
import com.warriortech.resb.network.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class PaidBillsUiState {
    object Loading : PaidBillsUiState()
    data class Success(val bills: List<TblBillingResponse>) : PaidBillsUiState()
    data class Error(val message: String) : PaidBillsUiState()
    object Idle : PaidBillsUiState()
}

@HiltViewModel
class PaidBillsViewModel @Inject constructor(
    private val billRepository: BillRepository,
    private val sessionManager: SessionManager,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaidBillsUiState>(PaidBillsUiState.Idle)
    val uiState: StateFlow<PaidBillsUiState> = _uiState.asStateFlow()

    private val _selectedBill = MutableStateFlow<TblBillingResponse?>(null)
    val selectedBill: StateFlow<TblBillingResponse?> = _selectedBill.asStateFlow()

    fun loadPaidBills(fromDate: String, toDate: String) {
        viewModelScope.launch {
            try {
                _uiState.value = PaidBillsUiState.Loading
                val tenantId = sessionManager.getCompanyCode() ?: ""
                val response = billRepository.getPaidBills(tenantId, fromDate, toDate)

                response.collect { result ->
                    result.onSuccess { bills ->
                        _uiState.value = PaidBillsUiState.Success(bills)
                    }.onFailure { error ->
                        _uiState.value = PaidBillsUiState.Error(error.message ?: "Unknown error")
                        Log.e("PaidBillsViewModel", "Error loading paid bills", error)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = PaidBillsUiState.Error(e.message ?: "Unknown error")
                Log.e("PaidBillsViewModel", "Exception loading paid bills", e)
            }
        }
    }

    fun selectBill(bill: TblBillingResponse) {
        _selectedBill.value = bill
    }

    fun deleteBill(billNo: String) {
        viewModelScope.launch {
            try {
                // Implement delete logic here
                // You would call billRepository.deleteBill(billNo)
                // For now, just refresh the list
                val currentState = _uiState.value
                if (currentState is PaidBillsUiState.Success) {
                    val updatedBills = currentState.bills.filter { it.bill_no != billNo }
                    _uiState.value = PaidBillsUiState.Success(updatedBills)
                }
            } catch (e: Exception) {
                _uiState.value = PaidBillsUiState.Error("Failed to delete bill: ${e.message}")
                Log.e("PaidBillsViewModel", "Error deleting bill", e)
            }
        }
    }

    fun printBill(billNo: String) {
        viewModelScope.launch {
            try {
                // Implement print logic here
                // You would call billRepository.printBill(billNo)
                val tamil = sessionManager.getGeneralSetting()?.tamil_receipt_print ?: false
                val bill = billRepository.getPaymentByBillNo(billNo)
                var sn = 1
                val orderDetails =
                    orderRepository.getOrdersByOrderId(bill?.order_master?.order_master_id ?: "")
                        .body()!!
                val counter =
                    sessionManager.getUser()?.counter_name ?: "Counter1"
                val billItems = orderDetails.map { detail ->
                    val menuItem = detail.menuItem
                    val qty = detail.qty
                    BillItem(
                        sn = sn++,
                        itemName = if (tamil) menuItem.menu_item_name_tamil else menuItem.menu_item_name,
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
                    billNo = bill?.bill_no ?: "",
                    date = bill?.bill_date.toString(),
                    time = bill?.bill_create_time.toString(),
                    orderNo = bill?.order_master?.order_master_id ?: "",
                    counter = counter,
                    tableNo = bill?.order_master?.table_name ?: "",
                    custName = bill?.customer?.customer_name ?: "",
                    custNo = bill?.customer?.contact_no ?: "",
                    custAddress = bill?.customer?.address ?: "",
                    custGstin = bill?.customer?.gst_no ?: "",
                    items = billItems,
                    subtotal = bill?.order_amt ?: 0.0,
                    deliveryCharge = 0.0, // Assuming no delivery charge
                    discount = bill?.disc_amt ?: 0.0,
                    roundOff = bill?.round_off ?: 0.0,
                    total = bill?.grand_total ?: 0.0,
                )

                    val ip = orderRepository.getIpAddress("COUNTER")
                    val printResponse = billRepository.printBill(billDetails, ip)
                    printResponse.collect { result ->
                        result.fold(
                            onSuccess = { message ->
                                Timber.e(message)
                            },
                            onFailure = { error ->
                                Timber.e(error, "Failed to print bill")
                            }
                        )
                    }

            } catch (e: Exception) {
                _uiState.value = PaidBillsUiState.Error("Failed to print bill: ${e.message}")
                Log.e("PaidBillsViewModel", "Error printing bill", e)
            }
        }
    }

    fun sendBillViaWhatsApp() {

    }

    fun clearSelection() {
        _selectedBill.value = null
    }

    fun clearError() {
        if (_uiState.value is PaidBillsUiState.Error) {
            _uiState.value = PaidBillsUiState.Idle
        }
    }
}
