package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.LedgerDetailsRepository
import com.warriortech.resb.model.TblLedgerDetailsIdResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DayEntryReportViewmodel @Inject constructor(
    private val ledgerDetailsRepository: LedgerDetailsRepository,
) : ViewModel() {
    sealed class DayEntryUiState {
        object Loading : DayEntryUiState()
        data class Success(
            val ledgers: List<TblLedgerDetailsIdResponse>
        )
        data class Error(val message: String) : DayEntryUiState()
    }

    private val _ledgerDetailsState =
        MutableStateFlow<DayEntryUiState>(DayEntryUiState.Loading)
    val ledgerDetailsState = _ledgerDetailsState.asStateFlow()

    fun loadData(ledgerId: Long){
        viewModelScope.launch {
            try{

            }catch (e: Exception) {

            }
        }
    }

}