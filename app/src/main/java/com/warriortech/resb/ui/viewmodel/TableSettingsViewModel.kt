
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.TableRepository
import com.warriortech.resb.model.Table
import com.warriortech.resb.screens.settings.TableSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableSettingsViewModel @Inject constructor(
    private val tableRepository: TableRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TableSettingsUiState>(TableSettingsUiState.Loading)
    val uiState: StateFlow<TableSettingsUiState> = _uiState.asStateFlow()

    fun loadTables() {
        viewModelScope.launch {
            try {
                _uiState.value = TableSettingsUiState.Loading
                val tables = tableRepository.getAllTables()
                _uiState.value = TableSettingsUiState.Success(tables)
            } catch (e: Exception) {
                _uiState.value = TableSettingsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addTable(table: Table) {
        viewModelScope.launch {
            try {
                tableRepository.insertTable(table)
                loadTables()
            } catch (e: Exception) {
                _uiState.value = TableSettingsUiState.Error(e.message ?: "Failed to add table")
            }
        }
    }

    fun updateTable(table: Table) {
        viewModelScope.launch {
            try {
                tableRepository.updateTable(table)
                loadTables()
            } catch (e: Exception) {
                _uiState.value = TableSettingsUiState.Error(e.message ?: "Failed to update table")
            }
        }
    }

    fun deleteTable(tableId: Int) {
        viewModelScope.launch {
            try {
                tableRepository.deleteTable(tableId)
                loadTables()
            } catch (e: Exception) {
                _uiState.value = TableSettingsUiState.Error(e.message ?: "Failed to delete table")
            }
        }
    }
}
