package com.warriortech.resb.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.GroupRepository
import com.warriortech.resb.data.repository.LedgerRepository
import com.warriortech.resb.model.TblGroupDetails
import com.warriortech.resb.model.TblLedgerDetails
import com.warriortech.resb.model.TblLedgerRequest
import com.warriortech.resb.ui.viewmodel.CounterViewModel.MenuUiState
import com.warriortech.resb.ui.viewmodel.GroupDetailsViewModel.GroupUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val ledgerRepository: LedgerRepository,
    private val groupRepository: GroupRepository
) : ViewModel(){

    sealed class LedgerUiState {
        object Loading : LedgerUiState()
        data class Success(val ledgers: List<TblLedgerDetails>) : LedgerUiState()
        data class Error(val message: String) : LedgerUiState()
    }

    private val _legerState = MutableStateFlow<LedgerUiState>(LedgerUiState.Loading)
    val ledgerState: StateFlow<LedgerUiState> = _legerState.asStateFlow()

    private val _groups = MutableStateFlow< List<TblGroupDetails>>(emptyList())
    val group : StateFlow<List<TblGroupDetails>> = _groups.asStateFlow()

    private val _orderBy = MutableStateFlow<String>("")
    val orderBy: StateFlow<String> = _orderBy.asStateFlow()
    fun loadLedgers() {
        viewModelScope.launch {
            val ledgers = ledgerRepository.getLedgers()
            _legerState.value = LedgerUiState.Success(ledgers ?: emptyList())
        }
    }

    fun addLedger(ledger: TblLedgerRequest) {
        viewModelScope.launch {
            val newLedger = ledgerRepository.createLedger(ledger)
            loadLedgers()
        }
    }

    fun updateLedger(ledgerId: String, ledger: TblLedgerRequest) {
        viewModelScope.launch {
            ledgerRepository.updateLedger(ledgerId, ledger)

        }
    }

    fun getOrderBy() {
        viewModelScope.launch {
            try {
                val response = groupRepository.getOrderBy()
                _orderBy.value = response["order_by"].toString()
            } catch (e: Exception) {
                _legerState.value = LedgerUiState.Error(e.message ?: "Failed to getOrderBy")
            }
        }
    }
    fun deleteLedger(ledgerId: String) {
        viewModelScope.launch {
            ledgerRepository.deleteLedger(ledgerId)
            loadLedgers()
        }
    }

    fun getBankDetails() {
        viewModelScope.launch {
            val bankDetails = ledgerRepository.getBankDetails()
            // Handle bank details as needed
        }
    }

    fun getGroups(){
        viewModelScope.launch {
           val groups = groupRepository.getGroups()!!
            _groups.value = groups
        }
    }
    fun addBankDetail() {
        // Implementation to add bank detail
    }

    fun updateBankDetail() {
        // Implementation to update bank detail
    }

}