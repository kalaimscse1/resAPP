
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.TaxRepository
import com.warriortech.resb.model.Tax
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaxSettingsUiState(
    val taxes: List<Tax> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TaxSettingsViewModel @Inject constructor(
    private val taxRepository: TaxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaxSettingsUiState())
    val uiState: StateFlow<TaxSettingsUiState> = _uiState

    fun loadTaxes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val taxes = taxRepository.getAllTaxes()
                _uiState.value = _uiState.value.copy(
                    taxes = taxes,
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

    fun addTax(tax: Tax) {
        viewModelScope.launch {
            try {
                val newTax = taxRepository.createTax(tax)
                if (newTax != null) {
                    loadTaxes()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateTax(tax: Tax) {
        viewModelScope.launch {
            try {
                val updatedTax = taxRepository.updateTax(tax)
                if (updatedTax != null) {
                    loadTaxes()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteTax(id: Long) {
        viewModelScope.launch {
            try {
                val success = taxRepository.deleteTax(id)
                if (success) {
                    loadTaxes()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
