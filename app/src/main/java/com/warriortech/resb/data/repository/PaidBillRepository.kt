package com.warriortech.resb.data.repository

import com.warriortech.resb.model.PaidBill
import com.warriortech.resb.model.PaidBillSummary
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.util.NetworkMonitor
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaidBillRepository @Inject constructor(
    private val apiService: ApiService,
    networkMonitor: NetworkMonitor
) : OfflineFirstRepository(networkMonitor) {

    suspend fun getAllPaidBills(): Response<List<PaidBillSummary>> {
        return try {
            apiService.getAllPaidBills(SessionManager.getCompanyCode()?:"")
        }catch (e: Exception) {
            return Response.success(emptyList())
        }

    }

    suspend fun getPaidBillById(billId: Long): Response<PaidBill>  {
        return try {
            apiService.getPaidBillById(billId,SessionManager.getCompanyCode()?:"")
        }catch (e: Exception) {
            Response.success(null)
        }

    }

    suspend fun updatePaidBill(billId: Long, billData: PaidBill): Response<PaidBill> {
        return try {
            apiService.updatePaidBill(billId, billData,SessionManager.getCompanyCode()?:"")
        }catch (e: Exception) {
            Response.success(null)
        }

    }

    suspend fun deletePaidBill(billId: Long): Response<Unit> {
        return try {
            apiService.deletePaidBill(billId,SessionManager.getCompanyCode()?:"")
        }catch (e: Exception) {
            Response.success(Unit)
        }
    }

    suspend fun refundBill(billId: Long, refundAmount: Double, reason: String): Response<PaidBill>  {
        return try {
            apiService.refundBill(billId, mapOf(
                "refund_amount" to refundAmount,
                "reason" to reason
            ),SessionManager.getCompanyCode()?:"")
        }catch (e: Exception) {
            Response.success(null)
        }

    }

    suspend fun searchPaidBills(query: String): Response<List<PaidBillSummary>> {
        return  apiService.searchPaidBills(query,SessionManager.getCompanyCode()?:"")
    }
}
