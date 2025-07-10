
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Staff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepository @Inject constructor() {
    private val _staff = MutableStateFlow<List<Staff>>(emptyList())
    val staff: Flow<List<Staff>> = _staff.asStateFlow()

    init {
        // Initialize with sample data
        _staff.value = listOf(
            Staff(1, "John Doe", "Manager", "john@example.com", "9876543210"),
            Staff(2, "Jane Smith", "Waiter", "jane@example.com", "9876543211"),
            Staff(3, "Mike Johnson", "Chef", "mike@example.com", "9876543212")
        )
    }

    suspend fun getAllStaff(): List<Staff> {
        return _staff.value
    }

    suspend fun addStaff(staff: Staff) {
        val newId = (_staff.value.maxOfOrNull { it.id } ?: 0) + 1
        val newStaff = staff.copy(id = newId)
        _staff.value = _staff.value + newStaff
    }

    suspend fun updateStaff(staff: Staff) {
        _staff.value = _staff.value.map { if (it.id == staff.id) staff else it }
    }

    suspend fun deleteStaff(staffId: Long) {
        _staff.value = _staff.value.filter { it.id != staffId }
    }
}
package com.warriortech.resb.data.repository

import com.warriortech.resb.model.Staff
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepository @Inject constructor() {

    private val staff = mutableListOf<Staff>()

    suspend fun getAllStaff(): List<Staff> {
        return staff.toList()
    }

    suspend fun insertStaff(staffMember: Staff): Long {
        val newId = (staff.maxOfOrNull { it.id } ?: 0) + 1
        val newStaff = staffMember.copy(id = newId)
        staff.add(newStaff)
        return newId
    }

    suspend fun updateStaff(staffMember: Staff) {
        val index = staff.indexOfFirst { it.id == staffMember.id }
        if (index != -1) {
            staff[index] = staffMember
        }
    }

    suspend fun deleteStaff(id: Long) {
        staff.removeAll { it.id == id }
    }

    suspend fun getStaffById(id: Long): Staff? {
        return staff.find { it.id == id }
    }
}
