
package com.warriortech.resb.data.repository


import com.warriortech.resb.model.*
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val apiService: ApiService
) {

    // Area management
    suspend fun getAllAreas(): List<Area> {
        return try {
            apiService.getAllAreas(SessionManager.getCompanyCode()?:"").body()!!
        } catch (e: Exception) {
            throw Exception("Failed to fetch areas: ${e.message}")
        }
    }

    suspend fun insertArea(area: Area): Long {
        return try {
            val response = apiService.createArea(area,SessionManager.getCompanyCode()?:"")
            response.body()?.area_id ?: 0L
        } catch (e: Exception) {
            throw Exception("Failed to create area: ${e.message}")
        }
    }


    suspend fun deleteArea(areaId: Long) {
        try {
            apiService.deleteArea(areaId,SessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            throw Exception("Failed to delete area: ${e.message}")
        }
    }

    // Table management
    suspend fun getAllTables(): List<Table> {
        return try {
            apiService.getAllTables(SessionManager.getCompanyCode()?:"").body()!!
        } catch (e: Exception) {
            throw Exception("Failed to fetch tables: ${e.message}")
        }
    }

    suspend fun deleteTable(tableId: Long) {
        try {
            apiService.deleteTable(tableId,SessionManager.getCompanyCode()?:"")
        } catch (e: Exception) {
            throw Exception("Failed to delete table: ${e.message}")
        }
    }

}
