package com.warriortech.resb.data.repository

import com.warriortech.resb.model.TblStaff
import com.warriortech.resb.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StaffRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getAllStaff(): List<TblStaff> {
        val response = apiService.getAllStaff()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch staff: ${response.message()}")
        }
    }

    suspend fun insertStaff(staff: TblStaff): TblStaff {
        val response = apiService.createStaff(staff)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to create staff")
        } else {
            throw Exception("Failed to create staff: ${response.message()}")
        }
    }

    suspend fun updateStaff(staff: TblStaff): TblStaff {
        val response = apiService.updateStaff(staff.staff_id, staff)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to update staff")
        } else {
            throw Exception("Failed to update staff: ${response.message()}")
        }
    }

    suspend fun deleteStaff(staffId: Long) {
        val response = apiService.deleteStaff(staffId)
        if (!response.isSuccessful) {
            throw Exception("Failed to delete staff: ${response.message()}")
        }
    }
}