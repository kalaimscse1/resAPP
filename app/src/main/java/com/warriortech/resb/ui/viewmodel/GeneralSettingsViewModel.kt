
package com.warriortech.resb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.GeneralSettingsRepository
import com.warriortech.resb.model.GeneralSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeneralSettingsUiState(
    val settings: GeneralSettings? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GeneralSettingsViewModel @Inject constructor(
    private val generalSettingsRepository: GeneralSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GeneralSettingsUiState())
    val uiState: StateFlow<GeneralSettingsUiState> = _uiState

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val settings = generalSettingsRepository.getGeneralSettings()
                _uiState.value = _uiState.value.copy(
                    settings = settings,
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

    fun updateSettings(settings: GeneralSettings) {
        viewModelScope.launch {
            try {
                val updatedSettings = generalSettingsRepository.updateGeneralSettings(settings)
                if (updatedSettings != null) {
                    _uiState.value = _uiState.value.copy(settings = updatedSettings)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
