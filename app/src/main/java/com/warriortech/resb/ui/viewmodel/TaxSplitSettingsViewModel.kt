
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.TaxSplitRepository
import com.warriortech.resb.model.TaxSplit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaxSplitSettingsUiState(
    val taxSplits: List<TaxSplit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TaxSplitSettingsViewModel @Inject constructor(
    private val taxSplitRepository: TaxSplitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaxSplitSettingsUiState())
    val uiState: StateFlow<TaxSplitSettingsUiState> = _uiState

    fun loadTaxSplits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val taxSplits = taxSplitRepository.getAllTaxSplits()
                _uiState.value = _uiState.value.copy(
                    taxSplits = taxSplits,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun addTaxSplit(taxSplit: TaxSplit) {
        viewModelScope.launch {
            try {
                val newTaxSplit = taxSplitRepository.createTaxSplit(taxSplit)
                if (newTaxSplit != null) {
                    loadTaxSplits()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateTaxSplit(taxSplit: TaxSplit) {
        viewModelScope.launch {
            try {
                val updatedTaxSplit = taxSplitRepository.updateTaxSplit(taxSplit)
                if (updatedTaxSplit != null) {
                    loadTaxSplits()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteTaxSplit(id: Int) {
        viewModelScope.launch {
            try {
                val success = taxSplitRepository.deleteTaxSplit(id)
                if (success) {
                    loadTaxSplits()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
