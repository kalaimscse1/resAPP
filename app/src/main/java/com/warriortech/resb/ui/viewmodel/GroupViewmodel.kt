package com.warriortech.resb.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.AreaRepository
import com.warriortech.resb.data.repository.GroupRepository
import com.warriortech.resb.model.Area
import com.warriortech.resb.model.TblGroupDetails
import com.warriortech.resb.model.TblGroupNature
import com.warriortech.resb.model.TblGroupRequest
import com.warriortech.resb.model.TblLedgerDetails
import com.warriortech.resb.screens.settings.MenuItemSettingsUiState
import com.warriortech.resb.ui.viewmodel.LedgerViewModel.LedgerUiState
import com.warriortech.resb.ui.viewmodel.MenuCategorySettingsViewModel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    sealed class GroupUiState {
        object Loading : GroupUiState()
        data class Success(val groups: List<TblGroupDetails>) : GroupUiState()
        data class Error(val message: String) : GroupUiState()
    }

    private val _groupState = MutableStateFlow<GroupUiState>(GroupUiState.Loading)
    val groupState: StateFlow<GroupUiState> = _groupState.asStateFlow()
    private val _groups = MutableStateFlow<List<TblGroupDetails>>(emptyList())
    val groups = _groups.asStateFlow()

    private val _groupNatures = MutableStateFlow<List<TblGroupNature>>(emptyList())
    val groupNatures = _groupNatures.asStateFlow()

    private val _orderBy = MutableStateFlow<String>("")
    val orderBy: StateFlow<String> = _orderBy.asStateFlow()
    fun loadGroups(){
        viewModelScope.launch {
            groupRepository.getGroups().let {
                _groups.value = it ?: emptyList()
                _groupState.value = GroupUiState.Success(it ?: emptyList())
            }
        }
    }

    fun getOrderBy() {
        viewModelScope.launch {
            try {
                val response = groupRepository.getOrderBy()
                _orderBy.value = response["order_by"].toString()
            } catch (e: Exception) {
                _groupState.value = GroupUiState.Error(e.message ?: "Failed to getOrderBy")
            }
        }
    }

    fun loadGroupNature(){
        viewModelScope.launch {
            groupRepository.getGroupNatures().let {
                _groupNatures.value = it ?: emptyList()
            }
        }
    }

    fun addGroup(group: TblGroupRequest) {
        viewModelScope.launch {
            groupRepository.createGroup(group)
        }
    }

    fun updateGroup(group_id: Int,group : TblGroupRequest){
        viewModelScope.launch {
            groupRepository.updateGroup(group_id.toLong(),group)
        }
    }

    fun deleteGroup(group_id: Int){
        viewModelScope.launch {
            groupRepository.deleteGroup(group_id.toLong())

        }
    }

}
