package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.model.Staff
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getAllStaff(): List<Staff> {
        val response = apiService.getAllStaff()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch staff: ${response.message()}")
        }
    }

    suspend fun insertStaff(staff: Staff): Staff {
        val response = apiService.createStaff(staff)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to create staff")
        } else {
            throw Exception("Failed to create staff: ${response.message()}")
        }
    }

    suspend fun updateStaff(staff: Staff): Staff {
        val response = apiService.updateStaff(staff.id, staff)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to update staff")
        } else {
            throw Exception("Failed to update staff: ${response.message()}")
        }
    }

    suspend fun deleteStaff(staffId: Int) {
        val response = apiService.deleteStaff(staffId)
        if (!response.isSuccessful) {
            throw Exception("Failed to delete staff: ${response.message()}")
        }
    }
}