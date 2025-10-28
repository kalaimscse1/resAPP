package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.GroupRepository
import com.warriortech.resb.data.repository.LedgerDetailsRepository
import com.warriortech.resb.data.repository.LedgerRepository
import com.warriortech.resb.data.repository.VoucherRepository
import com.warriortech.resb.model.TblGroupDetails
import com.warriortech.resb.model.TblGroupNature
import com.warriortech.resb.model.TblLedgerDetailIdRequest
import com.warriortech.resb.model.TblLedgerDetails
import com.warriortech.resb.model.TblVoucher
import com.warriortech.resb.model.TblVoucherResponse
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
    private val groupRepository: GroupRepository,
    private val voucherRepository: VoucherRepository
) : ViewModel() {

    sealed class LedgerDetailsUiState {
        object Loading : LedgerDetailsUiState()
        data class Success(
            val ledgers: List<TblLedgerDetails>,
            val groups: List<TblGroupNature>
        ) : LedgerDetailsUiState()
        data class Error(val message: String) : LedgerDetailsUiState()
    }

    sealed class TransactionUiState {
        object Idle : TransactionUiState()
        object Loading : TransactionUiState()
        data class Success(val message: String) : TransactionUiState()
        data class Error(val message: String) : TransactionUiState()
    }

    private val _ledgerDetailsState =
        MutableStateFlow<LedgerDetailsUiState>(LedgerDetailsUiState.Loading)
    val ledgerDetailsState = _ledgerDetailsState.asStateFlow()

    private val _transactionState =
        MutableStateFlow<TransactionUiState>(TransactionUiState.Idle)
    val transactionState = _transactionState.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _selectedLedger = MutableStateFlow<List<TblLedgerDetails>>(emptyList())
    val selectedLedger = _selectedLedger.asStateFlow()

    private val _ledgerList = MutableStateFlow<List<TblLedgerDetails>>(emptyList())
    val ledgerList = _ledgerList.asStateFlow()

    private val _totalDebit = MutableStateFlow(0.0)
    val totalDebit = _totalDebit.asStateFlow()

    private val _totalCredit = MutableStateFlow(0.0)
    val totalCredit = _totalCredit.asStateFlow()

    private val _entryNo = MutableStateFlow("")
    val entryNo = _entryNo.asStateFlow()

    private val _voucher = MutableStateFlow<TblVoucherResponse?>(null)
    val voucher: StateFlow<TblVoucherResponse?> = _voucher.asStateFlow()


    init {
        loadData()
    }

   fun loadData() {
        viewModelScope.launch {
            try {
                val ledgers = ledgerRepository.getLedgers().orEmpty()
                val groups = groupRepository.getGroupNatures().orEmpty()
                val entry = ledgerDetailsRepository.getEntryNo()
                val vouch = voucherRepository.getVoucherByCounterId("ACCOUNTS")
                _entryNo.value = entry["entry_no"] ?:""
                _ledgerList.value = ledgers
                _voucher.value = vouch
                val data = buildList {
                    add("ALL")
                    addAll(ledgers.map { it.group.group_nature.g_nature_name }.distinct())
                }
                _categories.value = data
                _ledgerDetailsState.value = LedgerDetailsUiState.Success(ledgers, groups)
            } catch (e: Exception) {
                _ledgerDetailsState.value = LedgerDetailsUiState.Error(e.message ?: "Failed to load data")
            }
        }
    }

    fun addItemToOrder(ledger: TblLedgerDetails) {
        _selectedLedger.value = _selectedLedger.value + ledger

    }

    fun updateAmounts(debit: Double?, credit: Double?) {
        debit?.let { _totalDebit.value += it }
        credit?.let { _totalCredit.value += it }
    }

    fun addLedgerDetails(entry: List<TblLedgerDetailIdRequest>) {
//        if (entry.amount_out == 0.0 && entry.amount_in == 0.0) {
//            _transactionState.value = TransactionUiState.Error("Amount cannot be zero.")
//            return
//        }

        viewModelScope.launch {
            try {
                _transactionState.value = TransactionUiState.Loading
                val response = ledgerDetailsRepository.addLedgerDetails(entry.first())
                if (response != null) {
                    clear()
                    loadData()
                    _transactionState.value =
                        TransactionUiState.Success("Entry Added Successfully")
                } else {
                    _transactionState.value = TransactionUiState.Error("Failed to save entry.")
                }
            } catch (e: Exception) {
                _transactionState.value =
                    TransactionUiState.Error(e.message ?: "Error while saving entry.")
            }
        }
    }

    fun clear() {
        _selectedLedger.value = emptyList()
        _totalDebit.value = 0.0
        _totalCredit.value = 0.0
    }

    private fun recalculateTotals(entry: List<TblLedgerDetailIdRequest>) {
        val totalD = entry.sumOf { it.amount_out }
        val totalC = entry.sumOf { it.amount_in }
        _totalDebit.value = totalD
        _totalCredit.value = totalC
    }
}
