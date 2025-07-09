package com.warriortech.resb.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.warriortech.resb.data.local.dao.TableDao
import com.warriortech.resb.model.TblBillingRequest
import com.warriortech.resb.model.TblBillingResponse
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.util.NetworkMonitor
import com.warriortech.resb.util.getCurrentDateModern
import com.warriortech.resb.util.getCurrentTimeModern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BillRepository@Inject constructor(
    private val apiService: ApiService,
    networkMonitor: NetworkMonitor
) : OfflineFirstRepository(networkMonitor) {

    // Implement methods for bill-related operations here
    // For example, fetching bills, creating new bills, etc.

    // Example method to fetch all bills
//    suspend fun getAllBills() = safeApiCall {
//        apiService.getAllBills()
//    }
//
//    // Example method to create a new bill
//    suspend fun createBill(billData: Map<String, Any>) = safeApiCall {
//        apiService.createBill(billData)
//    }
//    suspend fun placeBill()= safeApiCall {
//        apiService.placeBill()
//    }

    // Add more methods as needed for your application
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun placeBill(orderMasterId: Long): Flow<Result<TblBillingResponse>> = flow {
        try {
            val billNo= apiService.getBillNoByCounterId(SessionManager.getUser()?.counter_id!!).body()
            val voucher= apiService.getVoucherByCounterId(SessionManager.getUser()?.counter_id!!).body()
            val request = TblBillingRequest(
                bill_no = billNo?.get("bill_no")!!,
                bill_date = getCurrentDateModern(),
                bill_create_time = getCurrentTimeModern(),
                order_master_id = orderMasterId,
                voucher_id = voucher?.voucher_id ?: 0L,
                staff_id = SessionManager.getUser()?.staff_id ?: 0L,
                customer_id = 1L,
                order_amt = 0.0,
                disc_amt = 0.0,
                tax_amt = 0.0,
                cess = 0.0,
                cess_specific = 0.0,
                delivery_amt = 0.0,
                grand_total = 0.0,
                round_off = 0.0,
                rounded_amt = 0.0,
                cash = 0.0,
                card = 0.0,
                upi = 0.0,
                due = 0.0,
                others = 0.0,
                received_amt = 0.0,
                pending_amt = 0.0,
                change = 0.0,
                note = "",
                is_active = 1L
            )
            val response = apiService.addPayment(request)
            if (response.isSuccessful) {
                response.body()?.let { billResponse ->
                    emit(Result.success(billResponse))
                } ?: emit(Result.failure(Exception("No data found")))
            } else {
                emit(Result.failure(Exception("Error: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }

    }
}