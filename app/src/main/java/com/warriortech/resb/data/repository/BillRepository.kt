package com.warriortech.resb.data.repository

import android.util.Log
import com.warriortech.resb.model.Bill
import com.warriortech.resb.model.TblBillingRequest
import com.warriortech.resb.model.TblBillingResponse
import com.warriortech.resb.model.TblCounter
import com.warriortech.resb.model.TblCustomer
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.ui.viewmodel.PaymentMethod
import com.warriortech.resb.util.NetworkMonitor
import com.warriortech.resb.util.PrinterHelper
import com.warriortech.resb.util.getCurrentDateModern
import com.warriortech.resb.util.getCurrentTimeModern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BillRepository@Inject constructor(
    private val apiService: ApiService,
    networkMonitor: NetworkMonitor,
    private val sessionManager: SessionManager,
    private val printerHelper: PrinterHelper
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

    fun bill(orderMasterId: String,paymentMethod: PaymentMethod,receivedAmt: Double,customer: TblCustomer?): Flow<Result<TblBillingResponse>> =flow{
        try {
            val billNo = apiService.getBillNoByCounterId(sessionManager.getUser()?.counter_id!!, sessionManager.getCompanyCode()?:"")
            val voucher = apiService.getVoucherByCounterId(sessionManager.getUser()?.counter_id!!, sessionManager.getCompanyCode()?:"", "BILL").body()
            val orderMaster = apiService.getOpenOrderDetailsForTable(orderMasterId, sessionManager.getCompanyCode()?:"").body()!!
            val request = TblBillingRequest(
                bill_no = billNo["bill_no"] ?: "",
                bill_date = getCurrentDateModern(),
                bill_create_time = getCurrentTimeModern(),
                order_master_id = orderMasterId,
                voucher_id = voucher?.voucher_id ?: 0L,
                staff_id = sessionManager.getUser()?.staff_id ?: 0L,
                customer_id = customer?.customer_id ?: 1L,
                order_amt = orderMaster.sumOf { it.total },
                disc_amt = 0.0,
                tax_amt = orderMaster.sumOf { it.tax_amount },
                cess = orderMaster.sumOf { it.cess },
                cess_specific = orderMaster.sumOf { it.cess_specific },
                delivery_amt = 0.0,
                grand_total = orderMaster.sumOf { it.grand_total },
                round_off = 0.0,
                rounded_amt = 0.0,
                cash = if (paymentMethod.name == "CASH") orderMaster.sumOf { it.grand_total } else 0.0,
                card = if (paymentMethod.name == "CARD") orderMaster.sumOf { it.grand_total } else 0.0,
                upi = if (paymentMethod.name == "UPI") orderMaster.sumOf { it.grand_total } else 0.0,
                due = if (paymentMethod.name == "DUE") orderMaster.sumOf { it.grand_total } else 0.0,
                others = if (paymentMethod.name == "OTHERS") orderMaster.sumOf { it.grand_total } else 0.0,
                received_amt = if (paymentMethod.name != "DUE") receivedAmt else 0.0,
                pending_amt = if (paymentMethod.name == "DUE") orderMaster.sumOf { it.grand_total } else 0.0,
                change = if (paymentMethod.name == "CASH") receivedAmt - orderMaster.sumOf { it.grand_total } else 0.0,
                note = "",
                is_active = 1L
            )
            val response = apiService.addPayment(request,sessionManager.getCompanyCode()?:"")
            if (response.isSuccessful) {
                val billResponse = response.body()
                Log.d("BILLTAG", "placeBill: $billResponse")
                if (billResponse!=null) {
                    emit(Result.success(billResponse))
                } else {
                    emit(Result.failure(Exception("Failed to create bill: Response body is null.")))
                    return@flow
                }
            } else {
                emit(Result.failure(Exception("Error: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    suspend fun placeBill(orderMasterId: String,paymentMethod: PaymentMethod,receivedAmt: Double,
                          customer: TblCustomer?): Flow<Result<TblBillingResponse>> = flow {
            val billNo= apiService.getBillNoByCounterId(sessionManager.getUser()?.counter_id!!,sessionManager.getCompanyCode()?:"")
            val voucher= apiService.getVoucherByCounterId(sessionManager.getUser()?.counter_id!!,sessionManager.getCompanyCode()?:"","BILL").body()
            val orderMaster= apiService.getOpenOrderDetailsForTable(orderMasterId,sessionManager.getCompanyCode()?:"").body()!!
            val request = TblBillingRequest(
                bill_no = billNo["bill_no"] ?: "",
                bill_date = getCurrentDateModern(),
                bill_create_time = getCurrentTimeModern(),
                order_master_id = orderMasterId,
                voucher_id = voucher?.voucher_id ?: 0L,
                staff_id = sessionManager.getUser()?.staff_id ?: 0L,
                customer_id = customer?.customer_id ?: 1L,
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
                val billResponse = response.body()
                Log.d("BILLTAG", "placeBill: $billResponse")
                if (billResponse!=null) {
                    emit(Result.success(billResponse))
                } else {
                    emit(Result.failure(Exception("Failed to create bill: Response body is null.")))
                    return@flow
                }
            } else {
                emit(Result.failure(Exception("Error: ${response.message()}")))
            }

    }

    suspend fun getCounter(): TblCounter{
        return apiService.getCounterById(sessionManager.getUser()?.counter_id ?: 0L,sessionManager.getCompanyCode()?:"")
    }

    suspend fun updateTablAndOrderStatus(orderMasterId: String, tableId: Long) {
        apiService.updateTableAvailability(tableId, "AVAILABLE", sessionManager.getCompanyCode()?:"")
        apiService.updateOrderStatus(orderMasterId, "COMPLETED", sessionManager.getCompanyCode()?:"")

    }
    suspend fun printBill(bill: Bill,ipAddress: String): Flow<Result<String>> = flow {
        try {
            val response = apiService.printReceipt(bill, sessionManager.getCompanyCode()?:"")

            if (response.isSuccessful) {
                val printResponse = response.body()

                var mess = ""
                if (printResponse != null) {
                    printerHelper.printViaTcp(ipAddress, data = printResponse){ success, message ->
                        mess = if (success) {
                            message
                        } else {
                            message
                        }
                    }
                    emit(Result.success(mess)) // Emit the successful PrintResponse object
                } else {
                    // Successful response but empty body - this might be an error case depending on your API
                    emit(Result.failure(Exception("KOT print successful but response body was empty.")))
                }
            } else {
                // API call failed (e.g., 4xx, 5xx error)
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.failure(Exception("Failed to print KOT. Code: ${response.code()}, Error: $errorBody")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}