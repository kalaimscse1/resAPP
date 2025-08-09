package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.StaffRepository
import com.warriortech.resb.model.TblStaff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StaffUiState(
    val staff: List<TblStaff> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val staffRepository: StaffRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StaffUiState())
    val uiState: StateFlow<StaffUiState> = _uiState.asStateFlow()

    init {
        loadStaff()
    }

    private fun loadStaff() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val staff = staffRepository.getAllStaff()
                _uiState.value = _uiState.value.copy(
                    staff = staff,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun addStaff(name: String, role: String, email: String, phone: String) {
        viewModelScope.launch {
            try {
                val staff = TblStaff(
                    staff_id = 1,
                    staff_name = name,
                    contact_no = phone,
                    address = "",
                    user_name = email,
                    password ="",
                    role_id = 1,
                    role = role,
                    last_login = "",
                    is_block = false,
                    counter_id = 1,
                    counter_name = "",
                    area_id = 1,
                    area_name = "",
                    is_active = 1
                )
                staffRepository.insertStaff(staff)
                loadStaff()
                _uiState.value = _uiState.value.copy(successMessage = "Staff added successfully")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun updateStaff(staff: TblStaff) {
        viewModelScope.launch {
            try {
                staffRepository.updateStaff(staff)
                loadStaff()
                _uiState.value = _uiState.value.copy(successMessage = "Staff updated successfully")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteStaff(staffId: Long) {
        viewModelScope.launch {
            try {
                staffRepository.deleteStaff(staffId)
                loadStaff()
                _uiState.value = _uiState.value.copy(successMessage = "Staff deleted successfully")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}
