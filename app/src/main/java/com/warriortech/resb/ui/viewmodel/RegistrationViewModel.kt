
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.RegistrationRepository
import com.warriortech.resb.model.RegistrationRequest
import com.warriortech.resb.model.RestaurantProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    private val _registrationResult = MutableStateFlow<String?>(null)
    val registrationResult: StateFlow<String?> = _registrationResult.asStateFlow()

    fun updateCompanyMasterCode(value: String) {
        _uiState.value = _uiState.value.copy(companyMasterCode = value)
    }

    fun updateCompanyName(value: String) {
        _uiState.value = _uiState.value.copy(companyName = value)
    }

    fun updateOwnerName(value: String) {
        _uiState.value = _uiState.value.copy(ownerName = value)
    }

    fun updateAddress1(value: String) {
        _uiState.value = _uiState.value.copy(address1 = value)
    }

    fun updateAddress2(value: String) {
        _uiState.value = _uiState.value.copy(address2 = value)
    }

    fun updatePlace(value: String) {
        _uiState.value = _uiState.value.copy(place = value)
    }

    fun updatePincode(value: String) {
        _uiState.value = _uiState.value.copy(pincode = value)
    }

    fun updateContactNo(value: String) {
        _uiState.value = _uiState.value.copy(contactNo = value)
    }

    fun updateMailId(value: String) {
        _uiState.value = _uiState.value.copy(mailId = value)
    }

    fun updateCountry(value: String) {
        _uiState.value = _uiState.value.copy(country = value)
    }

    fun updateState(value: String) {
        _uiState.value = _uiState.value.copy(state = value)
    }

    fun updateYear(value: String) {
        _uiState.value = _uiState.value.copy(year = value)
    }

    fun updateDatabaseName(value: String) {
        _uiState.value = _uiState.value.copy(databaseName = value)
    }

    fun updateOrderPlan(value: String) {
        _uiState.value = _uiState.value.copy(orderPlan = value)
    }

    fun updateInstallDate(value: LocalDate) {
        _uiState.value = _uiState.value.copy(installDate = value)
    }

    fun updateSubscriptionDays(value: String) {
        val days = value.toLongOrNull() ?: 0L
        _uiState.value = _uiState.value.copy(subscriptionDays = days)
    }

    fun updateExpiryDate(value: String) {
        _uiState.value = _uiState.value.copy(expiryDate = value)
    }

    fun updateIsBlock(value: Boolean) {
        _uiState.value = _uiState.value.copy(isBlock = value)
    }

    fun clearRegistrationResult() {
        _registrationResult.value = null
    }

    fun loadCompanyCode(){
        viewModelScope.launch {
            val companyCode = registrationRepository.getCompanyCode()
            _uiState.value = _uiState.value.copy(companyMasterCode = companyCode["company_master_code"].toString())
        }
    }
    fun registerCompany() {
        val state = _uiState.value
        
        if (!validateForm(state)) {
            _registrationResult.value = "Please fill all required fields"
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val request = RegistrationRequest(
                    company_master_code = state.companyMasterCode,
                    company_name = state.companyName,
                    owner_name = state.ownerName,
                    address1 = state.address1,
                    address2 = state.address2,
                    place = state.place,
                    pincode = state.pincode,
                    contact_no = state.contactNo,
                    mail_id = state.mailId,
                    country = state.country,
                    state = state.state,
                    year = state.year,
                    database_name = state.databaseName,
                    order_plan = state.orderPlan,
                    install_date = state.installDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    subscription_days = state.subscriptionDays,
                    expiry_date = state.expiryDate,
                    is_block = state.isBlock
                )

                val response = registrationRepository.registerCompany(request)
                _registrationResult.value = if (response.success) {
                    val data = response.data
                    val profile = RestaurantProfile(
                        company_code = data?.company_master_code ?: "",
                        company_name = data?.company_name ?: "",
                        owner_name = data?.owner_name ?: "",
                        address1 = data?.address1 ?: "",
                        address2 = data?.address2 ?: "",
                        place = data?.place ?: "",
                        pincode = data?.pincode ?: "",
                        contact_no = data?.contact_no ?: "",
                        mail_id = data?.mail_id ?: "",
                        country = data?.country ?: "",
                        state = data?.state ?: "",
                        currency = "Rs",
                        tax_no = "",
                        decimal_point = 2L
                    )
                    registrationRepository.addRestaurantProfile(profile)
                    "Registration successful!"
                } else {
                    response.message
                }
            } catch (e: Exception) {
                _registrationResult.value = "Registration failed: ${e.message}"
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun validateForm(state: RegistrationUiState): Boolean {
        return state.companyMasterCode.isNotBlank() &&
                state.companyName.isNotBlank() &&
                state.ownerName.isNotBlank() &&
                state.address1.isNotBlank() &&
                state.address2.isNotBlank() &&
                state.place.isNotBlank() &&
                state.pincode.isNotBlank() &&
                state.contactNo.isNotBlank() &&
                state.mailId.isNotBlank() &&
                state.country.isNotBlank() &&
                state.state.isNotBlank() &&
                state.year.isNotBlank() &&
                state.databaseName.isNotBlank() &&
                state.orderPlan.isNotBlank() &&
                state.expiryDate.isNotBlank()
    }
}

data class RegistrationUiState(
    val companyMasterCode: String = "",
    val companyName: String = "",
    val ownerName: String = "",
    val address1: String = "",
    val address2: String = "",
    val place: String = "",
    val pincode: String = "",
    val contactNo: String = "",
    val mailId: String = "",
    val country: String = "",
    val state: String = "",
    val year: String = "",
    val databaseName: String = "",
    val orderPlan: String = "",
    val installDate: LocalDate = LocalDate.now(),
    val subscriptionDays: Long = 0L,
    val expiryDate: String = "",
    val isBlock: Boolean = false,
    val isLoading: Boolean = false
)
