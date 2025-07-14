
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.RestaurantProfileRepository
import com.warriortech.resb.model.RestaurantProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RestaurantProfileUiState(
    val profile: RestaurantProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RestaurantProfileViewModel @Inject constructor(
    private val restaurantProfileRepository: RestaurantProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantProfileUiState())
    val uiState: StateFlow<RestaurantProfileUiState> = _uiState

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val profile = restaurantProfileRepository.getRestaurantProfile()
                _uiState.value = _uiState.value.copy(
                    profile = profile,
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

    fun updateProfile(profile: RestaurantProfile) {
        viewModelScope.launch {
            try {
                val updatedProfile = restaurantProfileRepository.updateRestaurantProfile(profile)
                if (updatedProfile != null) {
                    _uiState.value = _uiState.value.copy(profile = updatedProfile)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
