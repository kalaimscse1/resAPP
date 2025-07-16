
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.PaidBillRepository
import com.warriortech.resb.model.PaidBill
import com.warriortech.resb.model.PaidBillSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaidBillsUiState(
    val isLoading: Boolean = false,
    val paidBills: List<PaidBillSummary> = emptyList(),
    val selectedBill: PaidBill? = null,
    val searchQuery: String = "",
    val filteredBills: List<PaidBillSummary> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showDeleteDialog: Boolean = false,
    val showRefundDialog: Boolean = false,
    val billToDelete: PaidBillSummary? = null,
    val billToRefund: PaidBillSummary? = null
)

@HiltViewModel
class PaidBillsViewModel @Inject constructor(
    private val paidBillRepository: PaidBillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaidBillsUiState())
    val uiState: StateFlow<PaidBillsUiState> = _uiState.asStateFlow()

    init {
        loadPaidBills()
    }

    fun loadPaidBills() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val response = paidBillRepository.getAllPaidBills()
                if (response.isSuccessful) {
                    val bills = response.body() ?: emptyList()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            paidBills = bills,
                            filteredBills = bills
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load paid bills: ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error loading paid bills: ${e.message}"
                    )
                }
            }
        }
    }

    fun searchBills(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        
        val filtered = if (query.isBlank()) {
            _uiState.value.paidBills
        } else {
            _uiState.value.paidBills.filter { bill ->
                bill.billNo.contains(query, ignoreCase = true) ||
                bill.customerName.contains(query, ignoreCase = true) ||
                bill.paymentMethod.contains(query, ignoreCase = true)
            }
        }
        
        _uiState.update { it.copy(filteredBills = filtered) }
    }

    fun loadBillDetails(billId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val response = paidBillRepository.getPaidBillById(billId)
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedBill = response.body()
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load bill details: ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error loading bill details: ${e.message}"
                    )
                }
            }
        }
    }

    fun showDeleteDialog(bill: PaidBillSummary) {
        _uiState.update {
            it.copy(showDeleteDialog = true, billToDelete = bill)
        }
    }

    fun hideDeleteDialog() {
        _uiState.update {
            it.copy(showDeleteDialog = false, billToDelete = null)
        }
    }

    fun showRefundDialog(bill: PaidBillSummary) {
        _uiState.update {
            it.copy(showRefundDialog = true, billToRefund = bill)
        }
    }

    fun hideRefundDialog() {
        _uiState.update {
            it.copy(showRefundDialog = false, billToRefund = null)
        }
    }

    fun deleteBill(billId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val response = paidBillRepository.deletePaidBill(billId)
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Bill deleted successfully",
                            showDeleteDialog = false,
                            billToDelete = null
                        )
                    }
                    loadPaidBills() // Refresh the list
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to delete bill: ${response.message()}",
                            showDeleteDialog = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error deleting bill: ${e.message}",
                        showDeleteDialog = false
                    )
                }
            }
        }
    }

    fun refundBill(billId: Long, refundAmount: Double, reason: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val response = paidBillRepository.refundBill(billId, refundAmount, reason)
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Bill refunded successfully",
                            showRefundDialog = false,
                            billToRefund = null
                        )
                    }
                    loadPaidBills() // Refresh the list
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to refund bill: ${response.message()}",
                            showRefundDialog = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error refunding bill: ${e.message}",
                        showRefundDialog = false
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(errorMessage = null, successMessage = null)
        }
    }
}
