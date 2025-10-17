package com.warriortech.resb.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warriortech.resb.data.repository.AreaRepository
import com.warriortech.resb.model.Area
import com.warriortech.resb.model.TblGroupDetails
import com.warriortech.resb.model.TblGroupNature
import com.warriortech.resb.screens.settings.MenuItemSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailsViewModel @Inject constructor() : ViewModel() {

    private val _groups = mutableStateListOf<TblGroupDetails>()
    val groups: List<TblGroupDetails> get() = _groups

    private val natures = listOf(
        TblGroupNature(1, "Income", true),
        TblGroupNature(2, "Expense", true),
        TblGroupNature(3, "Asset", true),
        TblGroupNature(4, "Liability", true)
    )

    init {
        // sample data
        _groups.addAll(
            listOf(
                TblGroupDetails(
                    1,
                    "G001",
                    "Food",
                    1,
                    "Main",
                    natures[0],
                    "YES",
                    "உணவு",
                    true,
                    "Admin"
                ),
                TblGroupDetails(
                    2,
                    "G002",
                    "Drinks",
                    2,
                    "Main",
                    natures[1],
                    "YES",
                    "பானம்",
                    true,
                    "Admin"
                )
            )
        )
    }

    fun getNatures() = natures

    fun addGroup(group: TblGroupDetails) {
        val nextId = (_groups.maxOfOrNull { it.group_id } ?: 0) + 1
        _groups.add(group.copy(group_id = nextId))
    }

    fun updateGroup(updated: TblGroupDetails) {
        val index = _groups.indexOfFirst { it.group_id == updated.group_id }
        if (index != -1) _groups[index] = updated
    }

    fun deleteGroup(id: Int) {
        _groups.removeAll { it.group_id == id }
    }


}
