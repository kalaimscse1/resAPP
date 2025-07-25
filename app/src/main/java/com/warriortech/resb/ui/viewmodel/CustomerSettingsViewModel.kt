package com.warriortech.resb.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.CustomerRepository
import com.warriortech.resb.model.Customer
import com.warriortech.resb.util.getCurrentDateModern
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerSettingsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    sealed class UiState {
        object Loading : UiState()
        data class Success(val customers: List<Customer>) : UiState()
        data class Error(val message: String) : UiState()
    }

    fun loadCustomers() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val customers = customerRepository.getAllCustomers()
                _uiState.value = UiState.Success(customers)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addCustomer(name: String, phone: String, email: String, address: String) {
        viewModelScope.launch {
            try {
                val customer = Customer(
                    customer_id = 0,
                    customer_name = name,
                    customer_phone = phone,
                    customer_email = email,
                    customer_address = address,
                    created_date = getCurrentDateModern(),
                    is_active = true
                )
                customerRepository.insertCustomer(customer)
                loadCustomers()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to add customer")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateCustomer(id: Long, name: String, phone: String, email: String, address: String) {
        viewModelScope.launch {
            try {
                val customer = Customer(
                    customer_id = 0,
                    customer_name = name,
                    customer_phone = phone,
                    customer_email = email,
                    customer_address = address,
                    created_date = getCurrentDateModern(),
                    is_active = true
                )
                customerRepository.updateCustomer(customer)
                loadCustomers()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to update customer")
            }
        }
    }

    fun deleteCustomer(id: Long) {
        viewModelScope.launch {
            try {
                customerRepository.deleteCustomer(id)
                loadCustomers()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to delete customer")
            }
        }
    }
}
