
package com.warriortech.resb.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.BillRepository
import com.warriortech.resb.model.TblBillingResponse
import com.warriortech.resb.network.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    private val sessionManager: SessionManager
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

    fun clearSelection() {
        _selectedBill.value = null
    }

    fun clearError() {
        if (_uiState.value is PaidBillsUiState.Error) {
            _uiState.value = PaidBillsUiState.Idle
        }
    }
}
