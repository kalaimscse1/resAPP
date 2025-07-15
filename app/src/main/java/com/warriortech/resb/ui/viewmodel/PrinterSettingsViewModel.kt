
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.PrinterRepository
import com.warriortech.resb.model.Printer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PrinterSettingsUiState(
    val printers: List<Printer> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PrinterSettingsViewModel @Inject constructor(
    private val printerRepository: PrinterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrinterSettingsUiState())
    val uiState: StateFlow<PrinterSettingsUiState> = _uiState

    fun loadPrinters() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val printers = printerRepository.getAllPrinters()
                _uiState.value = _uiState.value.copy(
                    printers = printers,
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

    fun addPrinter(printer: Printer) {
        viewModelScope.launch {
            try {
                val newPrinter = printerRepository.createPrinter(printer)
                if (newPrinter != null) {
                    loadPrinters()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updatePrinter(printer: Printer) {
        viewModelScope.launch {
            try {
                val updatedPrinter = printerRepository.updatePrinter(printer)
                if (updatedPrinter != null) {
                    loadPrinters()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deletePrinter(id: Long) {
        viewModelScope.launch {
            try {
                val success = printerRepository.deletePrinter(id)
                if (success) {
                    loadPrinters()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
