
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.VoucherRepository
import com.warriortech.resb.model.Voucher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VoucherSettingsUiState(
    val vouchers: List<Voucher> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VoucherSettingsViewModel @Inject constructor(
    private val voucherRepository: VoucherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoucherSettingsUiState())
    val uiState: StateFlow<VoucherSettingsUiState> = _uiState

    fun loadVouchers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val vouchers = voucherRepository.getAllVouchers()
                _uiState.value = _uiState.value.copy(
                    vouchers = vouchers,
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

    fun addVoucher(voucher: Voucher) {
        viewModelScope.launch {
            try {
                val newVoucher = voucherRepository.createVoucher(voucher)
                if (newVoucher != null) {
                    loadVouchers()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateVoucher(voucher: Voucher) {
        viewModelScope.launch {
            try {
                val updatedVoucher = voucherRepository.updateVoucher(voucher)
                if (updatedVoucher != null) {
                    loadVouchers()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteVoucher(id: Int) {
        viewModelScope.launch {
            try {
                val success = voucherRepository.deleteVoucher(id)
                if (success) {
                    loadVouchers()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
