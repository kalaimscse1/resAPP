package com.warriortech.resb.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.warriortech.resb.model.Bill
import com.warriortech.resb.model.TblBillingRequest
import com.warriortech.resb.model.TblBillingResponse
import com.warriortech.resb.model.TblCustomer
import com.warriortech.resb.model.TblLedgerDetailIdRequest
import com.warriortech.resb.model.TblLedgerDetails
import com.warriortech.resb.model.TblLedgerRequest
import com.warriortech.resb.model.TblOrderDetailsResponse
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.ui.viewmodel.payment.PaymentMethod
import com.warriortech.resb.util.NetworkMonitor
import com.warriortech.resb.util.PrinterHelper
import com.warriortech.resb.util.getCurrentDateModern
import com.warriortech.resb.util.getCurrentTimeModern
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BillRepository @Inject constructor(
    private val apiService: ApiService,
    networkMonitor: NetworkMonitor,
    private val sessionManager: SessionManager,
    private val printerHelper: PrinterHelper
) : OfflineFirstRepository(networkMonitor) {

    fun getPaidBills(
        tenantId: String,
        fromDate: String,
        toDate: String
    ): Flow<Result<List<TblBillingResponse>>> = flow {
        try {
            val response = apiService.getSalesReport(tenantId, fromDate, toDate)
            if (response.isSuccessful) {
                response.body()?.let { bills ->
                    emit(Result.success(bills))
                } ?: emit(Result.failure(Exception("No data received")))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun getUnpaidBills(
        tenantId: String,
        fromDate: String,
        toDate: String
    ): Flow<Result<List<TblBillingResponse>>> = flow {
        try {
            val response = apiService.getUnPaidBills(tenantId, fromDate, toDate)
            if (response.isSuccessful) {
                response.body()?.let { bills ->
                    emit(Result.success(bills))
                } ?: emit(Result.failure(Exception("No data received")))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun bill(
        orderMasterId: String,
        paymentMethod: PaymentMethod,
        receivedAmt: Double,
        customer: TblCustomer,
        billNo: String,
        cash: Double = 0.0,
        card: Double = 0.0,
        upi: Double = 0.0,
        voucherType: String
    ): Flow<Result<TblBillingResponse>> = flow {
        if (billNo != "--") {
            apiService.resetDue(billNo, sessionManager.getCompanyCode() ?: "")
        }
        if (paymentMethod.name == "DUE" && customer.customer_id == 1L) {
            emit(Result.failure(Exception("Please select a customer for due payment")))
            return@flow
        }
        var ledgers: TblLedgerDetails? = null
        if (paymentMethod.name == "DUE") {
            val ledgerRequest = TblLedgerRequest(
                ledger_name = customer.customer_name,
                ledger_fullname = customer.customer_name,
                order_by = 0,
                group_id = 17,
                address = customer.address,
                address1 = "",
                place = "",
                pincode = 0,
                country = "",
                contact_no = customer.contact_no,
                email = customer.email_address,
                gst_no = customer.gst_no,
                pan_no = "",
                state_code = "",
                state_name = "",
                sac_code = "",
                igst_status = if (customer.igst_status) "YES" else "NO",
                opening_balance = "",
                due_date = getCurrentDateModern(),
                bank_details = "NO",
                tamil_text = "",
                distance = 0.0
            )
          ledgers =  apiService.createLedger(ledgerRequest,sessionManager.getCompanyCode()?:"").body()!!
        }
        val billNo = if (paymentMethod.name == "DUE") apiService.getBillNoByCounterId(
            sessionManager.getUser()?.counter_id!!,
            "DUE",
            sessionManager.getCompanyCode() ?: ""
        ) else if (voucherType == "DUE")
            apiService.getBillNoByCounterId(
                sessionManager.getUser()?.counter_id!!,
                "DUE",
                sessionManager.getCompanyCode() ?: ""
            )
        else apiService.getBillNoByCounterId(
            sessionManager.getUser()?.counter_id!!,
            "BILL",
            sessionManager.getCompanyCode() ?: ""
        )
        val voucher = if (paymentMethod.name == "DUE") apiService.getVoucherByCounterId(
            sessionManager.getUser()?.counter_id!!,
            sessionManager.getCompanyCode() ?: "",
            "DUE"
        ).body() else if (voucherType == "DUE")
            apiService.getVoucherByCounterId(
                sessionManager.getUser()?.counter_id!!,
                sessionManager.getCompanyCode() ?: "",
                "DUE"
            ).body()
        else
            apiService.getVoucherByCounterId(
                sessionManager.getUser()?.counter_id!!,
                sessionManager.getCompanyCode() ?: "",
                "BILL"
            ).body()
        var order: List<TblOrderDetailsResponse> = emptyList()
        val orderMaster = apiService.getOpenOrderDetailsForTable(
            orderMasterId,
            sessionManager.getCompanyCode() ?: ""
        )
        if (orderMaster.isSuccessful) {
            order = orderMaster.body()!!
        }
        val request = TblBillingRequest(
            bill_no = billNo["bill_no"] ?: "",
            bill_date = getCurrentDateModern(),
            bill_create_time = getCurrentTimeModern(),
            order_master_id = orderMasterId,
            voucher_id = voucher?.voucher_id ?: 0L,
            staff_id = sessionManager.getUser()?.staff_id ?: 0L,
            customer_id = customer.customer_id,
            order_amt = order.sumOf { it.total },
            disc_amt = 0.0,
            tax_amt = order.sumOf { it.tax_amount },
            cess = order.sumOf { it.cess },
            cess_specific = order.sumOf { it.cess_specific },
            delivery_amt = 0.0,
            grand_total = order.sumOf { it.grand_total },
            round_off = 0.0,
            rounded_amt = order.sumOf { it.grand_total },
            cash = if (paymentMethod.name == "CASH") receivedAmt else if (paymentMethod.name == "OTHERS") cash else 0.0,
            card = if (paymentMethod.name == "CARD") receivedAmt else if (paymentMethod.name == "OTHERS") card else 0.0,
            upi = if (paymentMethod.name == "UPI") receivedAmt else if (paymentMethod.name == "OTHERS") upi else 0.0,
            due = if (paymentMethod.name == "DUE") receivedAmt else 0.0,
            others = 0.0,
            received_amt = if (paymentMethod.name == "DUE") 0.0 else receivedAmt,
            pending_amt = if (paymentMethod.name == "DUE") receivedAmt else 0.0,
//                change = if (paymentMethod.name == "CASH") receivedAmt - orderMaster.sumOf { it.grand_total } else 0.0,
            change = 0.0,
            note = "",
            is_active = 1L
        )
        var ledger: List<TblLedgerDetailIdRequest> = emptyList()
        ledger = when (paymentMethod.name) {
            "CASH" -> {
                listOf(
                    TblLedgerDetailIdRequest(
                        id = 5,
                        bill_no = billNo["bill_no"] ?: "",
                        date = getCurrentDateModern(),
                        time = getCurrentTimeModern(),
                        party_member = voucher?.voucher_name ?: "",
                        party_id = 1,
                        member = voucher?.voucher_id.toString(),
                        member_id = billNo["bill_no"] ?: "",
                        purpose = "SALES BY CASH",
                        amount_in = receivedAmt,
                        amount_out = 0.0
                    )
                )
            }

            "CARD" -> {
                listOf(
                    TblLedgerDetailIdRequest(
                        id = 5,
                        bill_no = billNo["bill_no"] ?: "",
                        date = getCurrentDateModern(),
                        time = getCurrentTimeModern(),
                        party_member = voucher?.voucher_name ?: "",
                        party_id = 2,
                        member = voucher?.voucher_id.toString(),
                        member_id = billNo["bill_no"] ?: "",
                        purpose = "SALES BY CARD",
                        amount_in = receivedAmt,
                        amount_out = 0.0
                    )
                )
            }

            "UPI" -> {
                listOf(
                    TblLedgerDetailIdRequest(
                        id = 5,
                        bill_no = billNo["bill_no"] ?: "",
                        date = getCurrentDateModern(),
                        time = getCurrentTimeModern(),
                        party_member = voucher?.voucher_name ?: "",
                        party_id = 3,
                        member = voucher?.voucher_id.toString(),
                        member_id = billNo["bill_no"] ?: "",
                        purpose = "SALES BY UPI",
                        amount_in = receivedAmt,
                        amount_out = 0.0
                    )
                )
            }

            "DUE" -> {
                listOf(
                    TblLedgerDetailIdRequest(
                        id = 5,
                        bill_no = billNo["bill_no"] ?: "",
                        date = getCurrentDateModern(),
                        time = getCurrentTimeModern(),
                        party_member = voucher?.voucher_name ?: "",
                        party_id = ledgers?.ledger_id?.toLong()?:0,
                        member = voucher?.voucher_id.toString(),
                        member_id = billNo["bill_no"] ?: "",
                        purpose = "DUE",
                        amount_in = receivedAmt,
                        amount_out = 0.0
                    )
                )
            }

            "OTHERS" -> {
                listOf(
                    TblLedgerDetailIdRequest(
                        id = 5,
                        bill_no = billNo["bill_no"] ?: "",
                        date = getCurrentDateModern(),
                        time = getCurrentTimeModern(),
                        party_member = voucher?.voucher_name ?: "",
                        party_id = 1,
                        member = voucher?.voucher_id.toString(),
                        member_id = billNo["bill_no"] ?: "",
                        purpose = "Sales By Upi",
                        amount_in = cash,
                        amount_out = 0.0
                    ),
                    TblLedgerDetailIdRequest(
                        id = 5,
                        bill_no = billNo["bill_no"] ?: "",
                        date = getCurrentDateModern(),
                        time = getCurrentTimeModern(),
                        party_member = voucher?.voucher_name ?: "",
                        party_id = 2,
                        member = voucher?.voucher_id.toString(),
                        member_id = billNo["bill_no"] ?: "",
                        purpose = "SALES BY CARD",
                        amount_in = card,
                        amount_out = 0.0
                    ),
                    TblLedgerDetailIdRequest(
                        id = 5,
                        bill_no = billNo["bill_no"] ?: "",
                        date = getCurrentDateModern(),
                        time = getCurrentTimeModern(),
                        party_member = voucher?.voucher_name ?: "",
                        party_id = 3,
                        member = voucher?.voucher_id.toString(),
                        member_id = billNo["bill_no"] ?: "",
                        purpose = "SALES BY UPI",
                        amount_in = upi,
                        amount_out = 0.0
                    )
                )
            }

            else -> {
                listOf(
                    TblLedgerDetailIdRequest(
                        id = 5,
                        bill_no = billNo["bill_no"] ?: "",
                        date = getCurrentDateModern(),
                        time = getCurrentTimeModern(),
                        party_member = voucher?.voucher_name ?: "",
                        party_id = 1,
                        member = voucher?.voucher_id.toString(),
                        member_id = billNo["bill_no"] ?: "",
                        purpose = "Sales By Upi",
                        amount_in = receivedAmt,
                        amount_out = 0.0
                    )
                )
            }
        }
        val check = apiService.checkBillExists(orderMasterId, sessionManager.getCompanyCode() ?: "")
        val checkExist = check.body()?.data == true
        if (checkExist) {
            val response = apiService.addPayment(request, sessionManager.getCompanyCode() ?: "")
            if (response.isSuccessful) {
                val res = response.body()!!
                apiService.updateTableAvailability(
                    res.order_master.table_id,
                    "AVAILABLE",
                    sessionManager.getCompanyCode() ?: ""
                )
                apiService.updateOrderStatus(
                    orderMasterId,
                    "COMPLETED",
                    sessionManager.getCompanyCode() ?: ""
                )
                if (customer.igst_status)
                    apiService.updateIgstForOrderDetails(
                        orderMasterId,
                        sessionManager.getCompanyCode() ?: ""
                    )
                else
                    apiService.updateGstForOrderDetails(
                        orderMasterId,
                        sessionManager.getCompanyCode() ?: ""
                    )
                apiService.saveAllLedgerDetails(ledger, sessionManager.getCompanyCode() ?: "")
                emit(Result.success(res))
            } else {
                emit(Result.failure(Exception("Error: ${response.message()}")))
            }
        } else {
            emit(Result.failure(Exception(check.body()?.message ?: "Something went wrong")))
        }
    }

    fun printBill(bill: Bill, ipAddress: String): Flow<Result<String>> = flow {
        try {
            val response = apiService.printReceipt(bill, sessionManager.getCompanyCode() ?: "")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val bytes = body.bytes()
                    var mess = ""
                    printerHelper.printViaTcp(ipAddress, data = bytes) { success, message ->
                        mess = message
                    }
                    emit(Result.success(mess))
                } else {
                    emit(Result.failure(Exception("Print successful but response body was empty.")))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.failure(Exception("Failed to print bill. Code: ${response.code()}, Error: $errorBody")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun fetchBillPreview(bill: Bill): Bitmap? {
        val response = apiService.getBillPreview(bill, sessionManager.getCompanyCode() ?: "")
        return if (response.isSuccessful) {
            response.body()?.byteStream()?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        } else null
    }

    suspend fun getPaymentByBillNo(billNo: String): TblBillingResponse? {
        val response = apiService.getPaymentByBillNo(billNo, sessionManager.getCompanyCode() ?: "")
        return if (response.isSuccessful) {
            response.body()
        } else null
    }

    suspend fun updateBill(billNo: String, orderMasterId: String) {
        val bill =
            apiService.getPaymentByBillNo(billNo, sessionManager.getCompanyCode() ?: "").body()!!
        var order: List<TblOrderDetailsResponse> = emptyList()
        val orderMaster = apiService.getOpenOrderDetailsForTable(
            orderMasterId,
            sessionManager.getCompanyCode() ?: ""
        )
        if (orderMaster.isSuccessful) {
            order = orderMaster.body()!!
        }
        val request = TblBillingRequest(
            bill_no = billNo,
            bill_date = bill.bill_date,
            bill_create_time = bill.bill_create_time,
            order_master_id = orderMasterId,
            voucher_id = bill.voucher.voucher_id,
            staff_id = bill.staff.staff_id,
            customer_id = bill.customer.customer_id,
            order_amt = order.sumOf { it.total },
            disc_amt = 0.0,
            tax_amt = order.sumOf { it.tax_amount },
            cess = order.sumOf { it.cess },
            cess_specific = order.sumOf { it.cess_specific },
            delivery_amt = 0.0,
            grand_total = order.sumOf { it.grand_total },
            round_off = 0.0,
            rounded_amt = order.sumOf { it.grand_total },
            cash = if (bill.cash > 0.0) order.sumOf { it.grand_total } else 0.0,
            card = if (bill.card > 0.0) order.sumOf { it.grand_total } else 0.0,
            upi = if (bill.upi > 0.0) order.sumOf { it.grand_total } else 0.0,
            due = if (bill.due > 0.0) order.sumOf { it.grand_total } else 0.0,
            others = 0.0,
            received_amt = if (bill.due > 0.0) 0.0 else order.sumOf { it.grand_total },
            pending_amt = if (bill.due > 0.0) order.sumOf { it.grand_total } else 0.0,
//                change = if (paymentMethod.name == "CASH") receivedAmt - orderMaster.sumOf { it.grand_total } else 0.0,
            change = 0.0,
            note = "",
            is_active = 1L
        )
        val ledgerDetails = apiService.getLedgerDetailsByEntryNo(
            billNo, sessionManager.getCompanyCode() ?: ""
        ).body()!!.filter { it.ledger_details_id.toInt() % 2 != 0 }
        val ledgerRequest = ledgerDetails.map {
            TblLedgerDetailIdRequest(
                ledger_details_id = it.ledger_details_id,
                id = it.ledger.ledger_id.toLong(),
                bill_no = billNo,
                date = it.date,
                time = it.time,
                party_member = it.party_member,
                party_id = it.party.ledger_id.toLong(),
                member = it.member,
                member_id = it.member_id,
                purpose = it.purpose,
                amount_in = request.grand_total,
                amount_out = 0.0
            )
        }
        apiService.updateAllLedgerDetails(ledgerRequest, sessionManager.getCompanyCode() ?: "")
        apiService.updateByBillNo(billNo, request, sessionManager.getCompanyCode() ?: "")

    }

    suspend fun deleteBill(billNo: String): Int? {
        val response = apiService.deleteByBillNo(billNo, sessionManager.getCompanyCode() ?: "")
        return if (response.isSuccessful) {
            response.body()
        } else
            null
    }
}