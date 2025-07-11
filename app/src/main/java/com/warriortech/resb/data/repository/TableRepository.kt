package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.model.Table
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableRepository @Inject constructor(
    private val apiService: ApiService
) {

    fun getAllTables(): Flow<List<Table>> = flow {
        try {
            val response = apiService.getAllTables()
            if (response.isSuccessful) {
                emit(response.body() ?: emptyList())
            } else {
                throw Exception("Failed to fetch tables: ${response.message()}")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun insertTable(table: Table): Table {
        val response = apiService.createTable(table)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to create table")
        } else {
            throw Exception("Failed to create table: ${response.message()}")
        }
    }

    suspend fun updateTable(table: Table): Table {
        val response = apiService.updateTable(table.id, table)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Failed to update table")
        } else {
            throw Exception("Failed to update table: ${response.message()}")
        }
    }

    suspend fun deleteTable(tableId: Int) {
        val response = apiService.deleteTable(tableId)
        if (!response.isSuccessful) {
            throw Exception("Failed to delete table: ${response.message()}")
        }
    }
}