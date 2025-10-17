package com.warriortech.resb.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.LedgerRepository
import com.warriortech.resb.model.TblLedgerDetails
import com.warriortech.resb.ui.viewmodel.CounterViewModel.MenuUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val ledgerRepository: LedgerRepository
) : ViewModel(){

    sealed class LedgerUiState {
        object Loading : LedgerUiState()
        data class Success(val ledgers: List<TblLedgerDetails>) : LedgerUiState()
        data class Error(val message: String) : LedgerUiState()
    }

    private val _legerState = MutableStateFlow<LedgerUiState>(LedgerUiState.Loading)
    val ledgerState: StateFlow<LedgerUiState> = _legerState.asStateFlow()

    private val _groups = mutableStateListOf<TblLedgerDetails>()
    val groups: List<TblLedgerDetails> get() = _groups

    fun loadLedgers() {
        viewModelScope.launch {
            val ledgers = ledgerRepository.getLedgers()
            ledgers?.let {
                _groups.clear()
                _groups.addAll(it)
            }
        }
    }

    fun addLedger(ledger: TblLedgerDetails) {
        viewModelScope.launch {
            val newLedger = ledgerRepository.createLedger(ledger)
            newLedger?.let {
                _groups.add(it)
            }
        }
    }

    fun updateLedger(ledgerId: String, ledger: TblLedgerDetails) {
        viewModelScope.launch {
            val result = ledgerRepository.updateLedger(ledgerId, ledger)
            if (result != null && result > 0) {
                val index = _groups.indexOfFirst { it.ledger_code == ledgerId }
                if (index != -1) {
                    _groups[index] = ledger
                }
            }
        }
    }

    fun getBankDetails() {
        viewModelScope.launch {
            val bankDetails = ledgerRepository.getBankDetails()
            // Handle bank details as needed
        }
    }

    fun addBankDetail() {
        // Implementation to add bank detail
    }

    fun updateBankDetail() {
        // Implementation to update bank detail
    }

}