package com.warriortech.resb.data.repository


import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.warriortech.resb.model.KOTRequest
import com.warriortech.resb.model.Order
import com.warriortech.resb.model.OrderDetails
import com.warriortech.resb.model.OrderItem
import com.warriortech.resb.model.OrderMaster
import com.warriortech.resb.model.OrderStatus
import com.warriortech.resb.model.TblOrderDetailsResponse
import com.warriortech.resb.model.TblOrderResponse
import com.warriortech.resb.network.ApiService
import com.warriortech.resb.network.SessionManager
import com.warriortech.resb.util.getCurrentDateModern
import com.warriortech.resb.util.getCurrentTimeModern
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Order-related API operations
 * Updated to work with the Kotlin Mini App backend
 */
@Singleton
class OrderRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Create a new order
     * @param tableId The table ID
     * @param items List of order items
     */
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createOrder(tableId: Long, items: List<OrderItem>,tableStatus:String): Flow<Result<TblOrderResponse>> = flow {
        try {
            val table= apiService.getTablesByStatus(tableId)
            val order_master_id= apiService.getOrderNo()
            val orderRequest = OrderMaster(
                order_date = getCurrentDateModern(),
                order_create_time = getCurrentTimeModern(),
                order_completed_time = "",
                staff_id = SessionManager.getUser()?.staff_id ?: 1,
                is_dine_in = tableStatus != "TAKEAWAY",
                is_take_away = tableStatus == "TAKEAWAY",
                is_delivery = tableStatus == "DELIVERY",
                table_id = tableId,
                no_of_person = table.seating_capacity,
                waiter_request_status = true,
                kitchen_response_status = true,
                order_status = "RUNNING",
                is_merge = false,
                is_active = 1,
                order_master_id = order_master_id["order_master_id"],
                is_delivered = false
            )
            val kot_number=apiService.getKotNo()
            val response = apiService.createOrder(orderRequest)

            if (response.isSuccessful) {
                val order = response.body()
                val orderDetails=items.map {
                    val taxAmount=calculateGst(if (tableStatus=="AC")
                        it.menuItem.ac_rate * it.quantity
                    else if (tableStatus=="PARCEL"||tableStatus=="DELIVERY")
                        it.menuItem.parcel_rate * it.quantity
                    else
                        it.menuItem.rate * it.quantity,it.menuItem.tax_percentage.toDouble(),true)
                    OrderDetails(
                        order_master_id = response.body()?.order_master_id,
                        order_details_id = 1,
                        kot_number = kot_number["kot_number"],
                        menu_item_id = it.menuItem.menu_item_id,
                        rate = taxAmount.basePrice,
                        qty = it.quantity,
                        total = taxAmount.basePrice * it.quantity,
                        tax_id = it.menuItem.tax_id ,
                        tax_amount = taxAmount.gstAmount,
                        sgst = 1.0,
                        cgst = 1.0,
                        grand_total = taxAmount.totalPrice,
                        prepare_status = true,
                        item_add_mode = false,
                        is_flag = false,
                        merge_order_nos = "",
                        merge_order_tables = table.table_name,
                        merge_pax = 2,
                        is_active = 1,
                    )
                }
                apiService.createOrderDetails(orderDetails)

                apiService.updateTableAvailability(tableId,"OCCUPIED")


                if (order != null) {
                    order.kot_number=kot_number["kot_number"]
                    emit(Result.success(order))
                } else {
                    emit(Result.failure(Exception("Failed to create order - empty response")))
                }
            } else {
                emit(Result.failure(Exception("Error creating order: ${response.code()}, ${response.errorBody()?.string()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get all orders
     * Our backend doesn't have a filter by table yet, so we get all orders and filter client-side
     */
    suspend fun getAllOrders(): Flow<Result<List<Order>>> = flow {
        try {
            val response = apiService.getAllOrders()

            if (response.isSuccessful) {
                val orders = response.body()
                if (orders != null) {

                    emit(Result.success(orders))
                } else {
                    emit(Result.failure(Exception("No orders data received")))
                }
            } else {
                emit(Result.failure(Exception("Error fetching orders: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Get orders for a specific table
     * Filters orders client-side
     */
    suspend fun getOrdersByTable(tableId: Long): List<TblOrderDetailsResponse> {

            val response = apiService.getOpenOrderItemsForTable(tableId)
            var order=0

            if (response.isSuccessful) {
                val allOrders = response.body()
                val orderno = allOrders?.get("order_master_id")
                if (allOrders != null && orderno != null) {
                    order=orderno
// Filter orders for this table
//                    val tableOrders = allOrders.filter { it.tableId == tableId }
                }
            }
            val orderDetails = apiService.getOpenOrderDetailsForTable(order)
            if(orderDetails.isSuccessful){
                return orderDetails.body()!!
            }
        return emptyList()
    }

    /**
     * Get active orders for a specific table
     * Filters orders client-side to those not in COMPLETED or CANCELLED status
     */
    suspend fun getActiveOrdersByTable(tableId: Int): Flow<Result<List<Order>>> = flow {
        try {
            val response = apiService.getAllOrders()

            if (response.isSuccessful) {
                val allOrders = response.body()
                if (allOrders != null) {
                    // Filter active orders for this table
                    val activeOrders = allOrders.filter {
                        it.tableId == tableId &&
                                it.status != OrderStatus.COMPLETED.name &&
                                it.status != OrderStatus.CANCELLED.name
                    }
                    emit(Result.success(activeOrders))
                } else {
                    emit(Result.failure(Exception("No orders data received")))
                }
            } else {
                emit(Result.failure(Exception("Error fetching orders: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Print a KOT for an order
     */
// Assuming KOTRequest, ApiService, and PrintResponse are defined elsewhere
// data class KOTRequest(val id: String) // Placeholder
// interface ApiService { // Placeholder
//    suspend fun printKOT(orderId: KOTRequest): retrofit2.Response<PrintResponse>
// }
// data class PrintResponse(val message: String, val status: String) // Placeholder

    suspend fun printKOT(orderId: KOTRequest): Flow<Result<String>> = flow { // Changed Flow type to Flow<Result<PrintResponse>>
        try {
            val response = apiService.printKOT(orderId)

            if (response.isSuccessful) {
                val printResponse = response.body()
                val message = printResponse?.get("message")
                if (printResponse != null) {
                    emit(Result.success(message.toString())) // Emit the successful PrintResponse object
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
            // Catch any other exceptions (network issues, etc.)
            emit(Result.failure(Exception("Error printing KOT: ${e.message}", e)))
        }
    }

    /**
     * Update an order's status
     */
    suspend fun updateOrderStatus(orderId: Int, newStatus: OrderStatus): Flow<Result<Order>> = flow {
        try {
            val statusUpdate = mapOf("status" to newStatus.name)
            val response = apiService.updateOrderStatus(orderId, statusUpdate)

            if (response.isSuccessful) {
                val updatedOrder = response.body()
                if (updatedOrder != null) {
                    emit(Result.success(updatedOrder))
                } else {
                    emit(Result.failure(Exception("Failed to update order status - empty response")))
                }
            } else {
                emit(Result.failure(Exception("Error updating order status: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun getOpenOrderItemsForTable(orderId: Long) {}
}
data class GstResult(val basePrice: Double, val gstAmount: Double, val totalPrice: Double)

fun calculateGst(amount: Double, gstRate: Double, isInclusive: Boolean): GstResult {
    return if (isInclusive) {
        val gstAmount = amount * gstRate / (100 + gstRate)
        val basePrice = amount - gstAmount
        GstResult(basePrice, gstAmount, amount)
    } else {
        val gstAmount = amount * gstRate / 100
        val totalPrice = amount + gstAmount
        GstResult(amount, gstAmount, totalPrice)
    }
}
