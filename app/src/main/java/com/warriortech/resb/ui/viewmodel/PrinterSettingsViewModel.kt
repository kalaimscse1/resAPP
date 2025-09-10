package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.PrinterRepository
import com.warriortech.resb.model.Printer
import com.warriortech.resb.model.TblPrinterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterSettingsViewModel @Inject constructor(
    private val printerRepository: PrinterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PrinterSettingsUiState>(PrinterSettingsUiState.Loading)
    val uiState: StateFlow<PrinterSettingsUiState> = _uiState.asStateFlow()

    sealed class PrinterSettingsUiState {
        object Loading : PrinterSettingsUiState()
        data class Success(val printers: List<TblPrinterResponse>) : PrinterSettingsUiState()
        data class Error(val message: String) : PrinterSettingsUiState()
    }

    fun loadPrinters() {
        viewModelScope.launch {
            _uiState.value = PrinterSettingsUiState.Loading
            try {
                val printers = printerRepository.getAllPrinters()
                _uiState.value = PrinterSettingsUiState.Success(printers)
            } catch (e: Exception) {
                _uiState.value = PrinterSettingsUiState.Error(e.message ?: "Unknown error")
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
                _uiState.value = PrinterSettingsUiState.Error(e.message ?: "Unknown error")
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
                _uiState.value = PrinterSettingsUiState.Error(e.message ?: "Unknown error")
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
                _uiState.value = PrinterSettingsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
