
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.CounterRepository
import com.warriortech.resb.model.Counter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CounterSettingsUiState(
    val counters: List<Counter> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CounterSettingsViewModel @Inject constructor(
    private val counterRepository: CounterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CounterSettingsUiState())
    val uiState: StateFlow<CounterSettingsUiState> = _uiState

    fun loadCounters() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val counters = counterRepository.getAllCounters()
                _uiState.value = _uiState.value.copy(
                    counters = counters,
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

    fun addCounter(counter: Counter) {
        viewModelScope.launch {
            try {
                val newCounter = counterRepository.createCounter(counter)
                if (newCounter != null) {
                    loadCounters()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateCounter(counter: Counter) {
        viewModelScope.launch {
            try {
                val updatedCounter = counterRepository.updateCounter(counter)
                if (updatedCounter != null) {
                    loadCounters()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteCounter(id: Int) {
        viewModelScope.launch {
            try {
                val success = counterRepository.deleteCounter(id)
                if (success) {
                    loadCounters()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
