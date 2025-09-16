package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.PaidBillRepository
import com.warriortech.resb.model.PaidBill
import com.warriortech.resb.model.PaidBillSummary
import com.warriortech.resb.util.Resulable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaidBillsUiState(
    val bills: List<PaidBillSummary> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val selectedBill: PaidBill? = null,
    val showDeleteDialog: Boolean = false,
    val billToDelete: PaidBillSummary? = null,
    val searchQuery: String = "",
    val isSearching: Boolean = false
)

@HiltViewModel
class PaidBillsViewModel @Inject constructor(
    private val paidBillRepository: PaidBillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaidBillsUiState())
    val uiState: StateFlow<PaidBillsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var searchJob: Job? = null

    init {
        loadPaidBills()
    }

    /**
     * Load all paid bills
     */
    fun loadPaidBills() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            paidBillRepository.getAllPaidBills().collect { result ->
                when (result) {
                    is Resulable.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resulable.Success -> {
                        _uiState.update { 
                            it.copy(
                                bills = result.data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Resulable.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Refresh paid bills
     */
    fun refreshPaidBills() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            
            paidBillRepository.getAllPaidBills().collect { result ->
                when (result) {
                    is Resulable.Loading -> {
                        _uiState.update { it.copy(isRefreshing = true) }
                    }
                    is Resulable.Success -> {
                        _uiState.update { 
                            it.copy(
                                bills = result.data,
                                isRefreshing = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Resulable.Error -> {
                        _uiState.update { 
                            it.copy(
                                isRefreshing = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Search paid bills
     */
    fun searchPaidBills(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        
        if (query.isBlank()) {
            loadPaidBills()
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, errorMessage = null) }
            
            paidBillRepository.searchPaidBills(query).collect { result ->
                when (result) {
                    is Resulable.Loading -> {
                        _uiState.update { it.copy(isSearching = true) }
                    }
                    is Resulable.Success -> {
                        _uiState.update { 
                            it.copy(
                                bills = result.data,
                                isSearching = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Resulable.Error -> {
                        _uiState.update { 
                            it.copy(
                                isSearching = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Show delete confirmation dialog
     */
    fun showDeleteDialog(bill: PaidBillSummary) {
        _uiState.update { 
            it.copy(
                showDeleteDialog = true,
                billToDelete = bill
            )
        }
    }

    /**
     * Hide delete confirmation dialog
     */
    fun hideDeleteDialog() {
        _uiState.update { 
            it.copy(
                showDeleteDialog = false,
                billToDelete = null
            )
        }
    }

    /**
     * Delete a paid bill
     */
    fun deletePaidBill(billId: Long) {
        viewModelScope.launch {
            paidBillRepository.deletePaidBill(billId).collect { result ->
                when (result) {
                    is Resulable.Loading -> {
                        // Keep loading state minimal for delete operation
                    }
                    is Resulable.Success -> {
                        _uiState.update { 
                            it.copy(
                                showDeleteDialog = false,
                                billToDelete = null,
                                errorMessage = null
                            )
                        }
                        // Refresh the list after successful deletion
                        loadPaidBills()
                    }
                    is Resulable.Error -> {
                        _uiState.update { 
                            it.copy(
                                showDeleteDialog = false,
                                billToDelete = null,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Clear search
     */
    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
        loadPaidBills()
    }

    override fun onCleared() {
        super.onCleared()
        loadJob?.cancel()
        searchJob?.cancel()
    }
}