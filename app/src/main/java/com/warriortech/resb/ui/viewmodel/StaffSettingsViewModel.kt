
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.StaffRepository
import com.warriortech.resb.model.Staff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffSettingsViewModel @Inject constructor(
    private val staffRepository: StaffRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiState {
        object Loading : UiState()
        data class Success(val staff: List<Staff>) : UiState()
        data class Error(val message: String) : UiState()
    }

    fun loadStaff() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val staff = staffRepository.getAllStaff()
                _uiState.value = UiState.Success(staff)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addStaff(name: String, phone: String, email: String, role: String, hireDate: String) {
        viewModelScope.launch {
            try {
                val staff = Staff(
                    id = 0,
                    name = name,
                    phone = phone,
                    email = email,
                    role = role,
                    hireDate = hireDate
                )
                staffRepository.insertStaff(staff)
                loadStaff()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to add staff")
            }
        }
    }

    fun updateStaff(id: Long, name: String, phone: String, email: String, role: String, hireDate: String) {
        viewModelScope.launch {
            try {
                val staff = Staff(
                    id = id,
                    name = name,
                    phone = phone,
                    email = email,
                    role = role,
                    hireDate = hireDate
                )
                staffRepository.updateStaff(staff)
                loadStaff()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to update staff")
            }
        }
    }

    fun deleteStaff(id: Long) {
        viewModelScope.launch {
            try {
                staffRepository.deleteStaff(id)
                loadStaff()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to delete staff")
            }
        }
    }
}
