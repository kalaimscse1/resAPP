package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.VoucherRepository
import com.warriortech.resb.model.TblCounter
import com.warriortech.resb.model.TblVoucher
import com.warriortech.resb.model.TblVoucherRequest
import com.warriortech.resb.model.TblVoucherResponse
import com.warriortech.resb.model.Voucher
import com.warriortech.resb.ui.viewmodel.CounterSettingsViewModel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoucherSettingsViewModel @Inject constructor(
    private val voucherRepository: VoucherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VoucherSettingsUiState>(
        VoucherSettingsUiState.Loading)
    val uiState: StateFlow<VoucherSettingsUiState> = _uiState.asStateFlow()

    sealed class VoucherSettingsUiState {
        object Loading : VoucherSettingsUiState()
        data class Success(val vouchers: List<TblVoucherResponse>) : VoucherSettingsUiState()
        data class Error(val message: String) : VoucherSettingsUiState()
    }

    fun loadVouchers() {
        viewModelScope.launch {
            _uiState.value = VoucherSettingsUiState.Loading
            try {
                val vouchers = voucherRepository.getAllVouchers()
                _uiState.value = VoucherSettingsUiState.Success(vouchers.filter { it.voucher_name !="--" })
            } catch (e: Exception) {
                _uiState.value = VoucherSettingsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addVoucher(voucher: TblVoucherRequest) {
        viewModelScope.launch {
            try {
                val newVoucher = voucherRepository.createVoucher(voucher)
                if (newVoucher != null) {
                    loadVouchers()
                }
            } catch (e: Exception) {
                _uiState.value = VoucherSettingsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateVoucher(voucher: TblVoucherRequest) {
        viewModelScope.launch {
            try {
                val updatedVoucher = voucherRepository.updateVoucher(voucher)
                if (updatedVoucher != null) {
                    loadVouchers()
                }
            } catch (e: Exception) {
                _uiState.value = VoucherSettingsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteVoucher(id: Long) {
        viewModelScope.launch {
            try {
                val success = voucherRepository.deleteVoucher(id)
                if (success) {
                    loadVouchers()
                }
            } catch (e: Exception) {
                _uiState.value = VoucherSettingsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
