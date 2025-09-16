package com.warriortech.resb.data.repository

import com.warriortech.resb.model.PaidBill
import com.warriortech.resb.model.PaidBillSummary
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.util.NetworkMonitor
import com.warriortech.resb.util.Resulable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.data.local.SessionManager
import com.warriortech.resb.model.PaidBill
import com.warriortech.resb.model.PaidBillSummary
import com.warriortech.resb.util.NetworkMonitor
import com.warriortech.resb.util.Resulable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaidBillRepository @Inject constructor(
    private val apiService: ApiService,
    networkMonitor: NetworkMonitor,
    private val sessionManager: SessionManager
) : OfflineFirstRepository(networkMonitor) {

    /**
     * Get all paid bills
     */
    fun getAllPaidBills(): Flow<Resulable<List<PaidBillSummary>>> = flow {
        emit(Resulable.Loading)
        try {
            val response = apiService.getAllPaidBills(sessionManager.getCompanyCode() ?: "")
            if (response.isSuccessful) {
                response.body()?.let { bills ->
                    emit(Resulable.Success(bills))
                } ?: emit(Resulable.Error("No data received"))
            } else {
                emit(Resulable.Error("Failed to fetch paid bills: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resulable.Error(e.message ?: "Unknown error occurred"))
        }
    }

    /**
     * Get paid bill by ID
     */
    fun getPaidBillById(billId: Long): Flow<Resulable<PaidBill>> = flow {
        emit(Resulable.Loading)
        try {
            val response = apiService.getPaidBillById(billId, sessionManager.getCompanyCode() ?: "")
            if (response.isSuccessful) {
                response.body()?.let { bill ->
                    emit(Resulable.Success(bill))
                } ?: emit(Resulable.Error("Bill not found"))
            } else {
                emit(Resulable.Error("Failed to fetch bill details: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resulable.Error(e.message ?: "Unknown error occurred"))
        }
    }

    /**
     * Update paid bill
     */
    fun updatePaidBill(billId: Long, billData: PaidBill): Flow<Resulable<PaidBill>> = flow {
        emit(Resulable.Loading)
        try {
            val response = apiService.updatePaidBill(billId, billData, sessionManager.getCompanyCode() ?: "")
            if (response.isSuccessful) {
                response.body()?.let { updatedBill ->
                    emit(Resulable.Success(updatedBill))
                } ?: emit(Resulable.Error("Update failed"))
            } else {
                emit(Resulable.Error("Failed to update bill: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resulable.Error(e.message ?: "Unknown error occurred"))
        }
    }

    /**
     * Delete paid bill
     */
    fun deletePaidBill(billId: Long): Flow<Resulable<Unit>> = flow {
        emit(Resulable.Loading)
        try {
            val response = apiService.deletePaidBill(billId, sessionManager.getCompanyCode() ?: "")
            if (response.isSuccessful) {
                emit(Resulable.Success(Unit))
            } else {
                emit(Resulable.Error("Failed to delete bill: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resulable.Error(e.message ?: "Unknown error occurred"))
        }
    }

    /**
     * Search paid bills
     */
    fun searchPaidBills(query: String): Flow<Resulable<List<PaidBillSummary>>> = flow {
        emit(Resulable.Loading)
        try {
            val response = apiService.searchPaidBills(query, sessionManager.getCompanyCode() ?: "")
            if (response.isSuccessful) {
                response.body()?.let { bills ->
                    emit(Resulable.Success(bills))
                } ?: emit(Resulable.Error("No results found"))
            } else {
                emit(Resulable.Error("Search failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resulable.Error(e.message ?: "Unknown error occurred"))
        }
    }
}