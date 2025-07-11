
package com.warriortech.resb.ui.viewmodel

//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.warriortech.resb.data.repository.AreaRepository
//import com.warriortech.resb.model.Area
//import com.warriortech.resb.screens.settings.AreaSettingsUiState
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class AreaSettingsViewModel @Inject constructor(
//    private val areaRepository: AreaRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow<AreaSettingsUiState>(AreaSettingsUiState.Loading)
//    val uiState: StateFlow<AreaSettingsUiState> = _uiState.asStateFlow()
//
//    fun loadAreas() {
//        viewModelScope.launch {
//            try {
//                _uiState.value = AreaSettingsUiState.Loading
//                val areas = areaRepository.getAllAreas()
//                _uiState.value = AreaSettingsUiState.Success(areas)
//            } catch (e: Exception) {
//                _uiState.value = AreaSettingsUiState.Error(e.message ?: "Unknown error")
//            }
//        }
//    }
//
//    fun addArea(area: Area) {
//        viewModelScope.launch {
//            try {
//                areaRepository.insertArea(area)
//                loadAreas() // Refresh the list
//            } catch (e: Exception) {
//                _uiState.value = AreaSettingsUiState.Error(e.message ?: "Failed to add area")
//            }
//        }
//    }
//
//    fun updateArea(area: Area) {
//        viewModelScope.launch {
//            try {
//                areaRepository.updateArea(area)
//                loadAreas() // Refresh the list
//            } catch (e: Exception) {
//                _uiState.value = AreaSettingsUiState.Error(e.message ?: "Failed to update area")
//            }
//        }
//    }
//
//    fun deleteArea(areaId: Int) {
//        viewModelScope.launch {
//            try {
//                areaRepository.deleteArea(areaId)
//                loadAreas() // Refresh the list
//            } catch (e: Exception) {
//                _uiState.value = AreaSettingsUiState.Error(e.message ?: "Failed to delete area")
//            }
//        }
//    }
//}
