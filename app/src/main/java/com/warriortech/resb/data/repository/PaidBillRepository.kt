
package com.warriortech.resb.data.repository

import com.warriortech.resb.data.api.ApiService
import com.warriortech.resb.data.common.NetworkMonitor
import com.warriortech.resb.data.common.OfflineFirstRepository
import com.warriortech.resb.model.PaidBill
import com.warriortech.resb.model.PaidBillSummary
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaidBillRepository @Inject constructor(
    private val apiService: ApiService,
    networkMonitor: NetworkMonitor
) : OfflineFirstRepository(networkMonitor) {

    suspend fun getAllPaidBills(): Response<List<PaidBillSummary>> = safeApiCall {
        apiService.getAllPaidBills()
    }

    suspend fun getPaidBillById(billId: Long): Response<PaidBill> = safeApiCall {
        apiService.getPaidBillById(billId)
    }

    suspend fun updatePaidBill(billId: Long, billData: PaidBill): Response<PaidBill> = safeApiCall {
        apiService.updatePaidBill(billId, billData)
    }

    suspend fun deletePaidBill(billId: Long): Response<Unit> = safeApiCall {
        apiService.deletePaidBill(billId)
    }

    suspend fun refundBill(billId: Long, refundAmount: Double, reason: String): Response<PaidBill> = safeApiCall {
        apiService.refundBill(billId, mapOf(
            "refund_amount" to refundAmount,
            "reason" to reason
        ))
    }

    suspend fun searchPaidBills(query: String): Response<List<PaidBillSummary>> = safeApiCall {
        apiService.searchPaidBills(query)
    }
}
