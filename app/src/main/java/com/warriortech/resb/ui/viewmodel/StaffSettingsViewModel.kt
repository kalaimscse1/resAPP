package com.warriortech.resb.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.StaffRepository
import com.warriortech.resb.model.TblStaff
import com.warriortech.resb.util.getCurrentDateModern
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
        data class Success(val staff: List<TblStaff>) : UiState()
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun addStaff(name: String, phone: String, email: String, role: String, hireDate: String) {
        viewModelScope.launch {
            try {
                val staff = TblStaff(
                    staff_id = 0, // Assuming ID is auto-generated
                    staff_name = name,
                    contact_no = phone,
                    address = "", // Assuming address is not required for add
                    user_name = email,
                    password = "", // Assuming password is not required for add
                    role_id = 0, // Assuming role_id is not required for add
                    role = role,
                    last_login = getCurrentDateModern(), // Assuming last_login is not required for add
                    is_block = false, // Assuming is_block is not required for add
                    counter_id = 1, // Assuming counter_id is not required for add
                    counter_name = "", // Assuming counter_name is not required for add
                    is_active = 1 // Assuming is_active is not required for add
                )
                staffRepository.insertStaff(staff)
                loadStaff()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to add staff")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateStaff(id: Long, name: String, phone: String, email: String, role: String, hireDate: String) {
        viewModelScope.launch {
            try {
                val staff = TblStaff(
                    staff_id = id,
                    staff_name = name,
                    contact_no = phone,
                    address = "", // Assuming address is not required for update
                    user_name = email,
                    password = "", // Assuming password is not required for update
                    role_id = 0, // Assuming role_id is not required for update
                    role = role,
                    last_login = getCurrentDateModern(), // Assuming last_login is not required for update
                    is_block = false, // Assuming is_block is not required for update
                    counter_id = 1, // Assuming counter_id is not required for update
                    counter_name = "", // Assuming counter_name is not required for update
                    is_active = 1 // Assuming is_active is not required for update
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
