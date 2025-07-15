
package com.warriortech.resb.data.repository


import com.warriortech.resb.model.*
import com.warriortech.resb.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val apiService: ApiService
) {

    // Area management
    suspend fun getAllAreas(): List<Area> {
        return try {
            apiService.getAllAreas().body()!!
        } catch (e: Exception) {
            throw Exception("Failed to fetch areas: ${e.message}")
        }
    }

    suspend fun insertArea(area: Area): Long {
        return try {
            val response = apiService.createArea(area)
            response.body()?.area_id ?: 0L
        } catch (e: Exception) {
            throw Exception("Failed to create area: ${e.message}")
        }
    }


    suspend fun deleteArea(areaId: Long) {
        try {
            apiService.deleteArea(areaId)
        } catch (e: Exception) {
            throw Exception("Failed to delete area: ${e.message}")
        }
    }

    // Table management
    suspend fun getAllTables(): List<Table> {
        return try {
            apiService.getAllTables().body()!!
        } catch (e: Exception) {
            throw Exception("Failed to fetch tables: ${e.message}")
        }
    }

    suspend fun deleteTable(tableId: Long) {
        try {
            apiService.deleteTable(tableId)
        } catch (e: Exception) {
            throw Exception("Failed to delete table: ${e.message}")
        }
    }

}
