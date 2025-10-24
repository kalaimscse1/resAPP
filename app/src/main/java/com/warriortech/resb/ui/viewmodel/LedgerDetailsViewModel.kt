package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.GroupRepository
import com.warriortech.resb.data.repository.LedgerDetailsRepository
import com.warriortech.resb.data.repository.LedgerRepository
import com.warriortech.resb.model.TblGroupDetails
import com.warriortech.resb.model.TblGroupNature
import com.warriortech.resb.model.TblLedgerDetailIdRequest
import com.warriortech.resb.model.TblLedgerDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LedgerDetailsViewModel @Inject constructor(
    private val ledgerDetailsRepository: LedgerDetailsRepository,
    private val ledgerRepository: LedgerRepository,
    private val groupRepository: GroupRepository
) : ViewModel(){
    sealed class LedgerDetailsUiState {
        object Loading : LedgerDetailsUiState()
        data class Success(val ledgers: List<TblLedgerDetails>,
            val groups: List<TblGroupNature>
        ) : LedgerDetailsUiState()
        data class Error(val message: String) : LedgerDetailsUiState()
    }
    sealed class TransactionUiState{
        object Loading : TransactionUiState()
        data class Success(val message:String): TransactionUiState()
        data class Error(val message:String): TransactionUiState()
    }
    private val _legerDetailsState = MutableStateFlow<LedgerDetailsUiState>(LedgerDetailsUiState.Loading)
    val ledgerDetailsState: StateFlow<LedgerDetailsUiState> = _legerDetailsState.asStateFlow()

    private val _transactionState = MutableStateFlow<TransactionUiState>(TransactionUiState.Loading)
    val transactionState: StateFlow<TransactionUiState> = _transactionState.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedLedger = MutableStateFlow<List<TblLedgerDetails>>(emptyList())
    val selectedLedger: StateFlow<List<TblLedgerDetails>> = _selectedLedger

    init {
        viewModelScope.launch {
            val ledgers = ledgerRepository.getLedgers()!!
            val groups = groupRepository.getGroupNatures()!!

            _categories.value = groups.map { it.g_nature_name }.distinct()
            _legerDetailsState.value = LedgerDetailsUiState.Success(ledgers,groups)
        }
    }

    fun addLedgerDetails(ledgerDetails: TblLedgerDetailIdRequest){
        viewModelScope.launch {
            val res = ledgerDetailsRepository.addLedgerDetails(ledgerDetails)
            if (res!=null)
                _transactionState.value = TransactionUiState.Success("Entry Added Successfully")
        }
    }
    fun addItemToOrder(ledgerDetails: TblLedgerDetails){
        _selectedLedger.value = _selectedLedger.value + ledgerDetails

    }

}