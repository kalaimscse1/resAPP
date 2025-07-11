
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Staff
import com.warriortech.resb.model.TblStaff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepository @Inject constructor() {
    private val _staff = MutableStateFlow<List<TblStaff>>(emptyList())
    val staff: Flow<List<TblStaff>> = _staff.asStateFlow()

    init {
        // Initialize with sample data
        _staff.value = listOf(
            TblStaff(
                1, "John Doe", "9876543210", "john@example.com", "john@example.com",
                password = TODO(),
                role_id = TODO(),
                role = TODO(),
                last_login = TODO(),
                is_block = TODO(),
                counter_id = TODO(),
                counter_name = TODO(),
                is_active = TODO()
            ),
            TblStaff(
                2, "Jane Smith", "9876543211", "jane@example.com", "jane@example.com",
                password = TODO(),
                role_id = TODO(),
                role = TODO(),
                last_login = TODO(),
                is_block = TODO(),
                counter_id = TODO(),
                counter_name = TODO(),
                is_active = TODO()
            ),
            TblStaff(
                3, "Mike Johnson", "9876543212", "mike@example.com", "mike@example.com",
                password = TODO(),
                role_id = TODO(),
                role = TODO(),
                last_login = TODO(),
                is_block = TODO(),
                counter_id = TODO(),
                counter_name = TODO(),
                is_active = TODO()
            )
        )
    }

    suspend fun getAllStaff(): List<TblStaff> {
        return _staff.value
    }

    suspend fun addStaff(staff: TblStaff) {
        val newId = (_staff.value.maxOfOrNull { it.staff_id } ?: 0) + 1
        val newStaff = staff.copy(staff_id = newId)
        _staff.value = _staff.value + newStaff
    }

    suspend fun updateStaff(staff: TblStaff) {
        _staff.value = _staff.value.map { if (it.staff_id == staff.staff_id) staff else it }
    }

    suspend fun deleteStaff(staffId: Long) {
        _staff.value = _staff.value.filter { it.staff_id != staffId }
    }

    fun insertStaff(staff: TblStaff) {}
}