package com.warriortech.resb.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.warriortech.resb.data.local.dao.TableDao
import com.warriortech.resb.model.TblBillingRequest
import com.warriortech.resb.model.TblBillingResponse
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.ui.viewmodel.PaymentMethod
import com.warriortech.resb.util.NetworkMonitor
import com.warriortech.resb.util.getCurrentDateModern
import com.warriortech.resb.util.getCurrentTimeModern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BillRepository@Inject constructor(
    private val apiService: ApiService,
    networkMonitor: NetworkMonitor,
    private val sessionManager: SessionManager
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
    suspend fun placeBill(orderMasterId: Long,paymentMethod: PaymentMethod,receivedAmt: Double
    ): Flow<Result<TblBillingResponse>> = flow {
        try {
            val billNo= apiService.getBillNoByCounterId(sessionManager.getUser()?.counter_id!!,sessionManager.getCompanyCode()?:"").body()
            val voucher= apiService.getVoucherByCounterId(sessionManager.getUser()?.counter_id!!,sessionManager.getCompanyCode()?:"").body()
            val orderMaster= apiService.getOpenOrderDetailsForTable(orderMasterId,sessionManager.getCompanyCode()?:"").body()!!
            val request = TblBillingRequest(
                bill_no = billNo?.get("bill_no")!!,
                bill_date = getCurrentDateModern(),
                bill_create_time = getCurrentTimeModern(),
                order_master_id = orderMasterId,
                voucher_id = voucher?.voucher_id ?: 0L,
                staff_id = sessionManager.getUser()?.staff_id ?: 0L,
                customer_id = 1L,
                order_amt = orderMaster.sumOf { it.total },
                disc_amt = 0.0,
                tax_amt = orderMaster.sumOf { it.tax_amount },
                cess = orderMaster.sumOf { it.cess },
                cess_specific = orderMaster.sumOf { it.cess_specific },
                delivery_amt = 0.0,
                grand_total = orderMaster.sumOf { it.grand_total },
                round_off = 0.0,
                rounded_amt = 0.0,
                cash = if (paymentMethod.name == "CASH") orderMaster.sumOf { it.grand_total} else 0.0,
                card = if (paymentMethod.name == "CARD") orderMaster.sumOf { it.grand_total} else 0.0,
                upi = if (paymentMethod.name == "UPI") orderMaster.sumOf { it.grand_total} else 0.0,
                due = if (paymentMethod.name == "DUE") orderMaster.sumOf { it.grand_total} else 0.0,
                others = if (paymentMethod.name == "OTHERS") orderMaster.sumOf { it.grand_total} else 0.0,
                received_amt = if (paymentMethod.name != "DUE") receivedAmt else 0.0,
                pending_amt = if (paymentMethod.name == "DUE") orderMaster.sumOf { it.grand_total} else 0.0,
                change = if (paymentMethod.name == "CASH") receivedAmt - orderMaster.sumOf { it.grand_total} else 0.0,
                note = "",
                is_active = 1L
            )
            val response = apiService.addPayment(request,sessionManager.getCompanyCode()?:"")
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