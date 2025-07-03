package com.warriortech.resb.data.repository


import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
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
    suspend fun placeOrUpdateOrder(
        tableId: Long,
        itemsToPlace: List<OrderItem>,
        tableStatus: String,
        existingOpenOrderMasterId: Int? = null // Allow passing it if already known
    ): Flow<Result<TblOrderResponse>> = flow {
        if (itemsToPlace.isEmpty()) {
            emit(Result.failure(IllegalArgumentException("Cannot place an order with no items.")))
            return@flow
        }

        try {
            var currentOrderMasterId = existingOpenOrderMasterId
            var orderMasterResponse: TblOrderResponse? = null

            // 1. Check for/Determine existing open OrderMaster ID for the table
            if (currentOrderMasterId == null && tableStatus != "TAKEAWAY" && tableStatus != "DELIVERY") {
                // Try to find an open order for this table
                // Assuming getOpenOrderMasterForTable returns a TblOrderResponse or similar with order_master_id
                // If it returns null, no open order exists.
                val openOrderResponse = apiService.getOpenOrderMasterForTable(tableId) // YOU NEED TO IMPLEMENT/DEFINE THIS
                if (openOrderResponse.isSuccessful && openOrderResponse.body() != null) {
                    currentOrderMasterId = openOrderResponse.body()!!.order_master_id
                    orderMasterResponse = openOrderResponse.body() // Store the existing order master response
                }
            }

            val tableInfo = apiService.getTablesByStatus(tableId) // Assuming this gets details like seating_capacity, table_name

            // 2. If no existing open OrderMaster, create a new one
            if (currentOrderMasterId == null) {
                val newOrderMasterApiId = apiService.getOrderNo() // Get new Order Master ID from API
                val orderRequest = OrderMaster(
                    order_date = getCurrentDateModern(),
                    order_create_time = getCurrentTimeModern(),
                    order_completed_time = "", // Will be empty for new/running orders
                    staff_id = SessionManager.getUser()?.staff_id ?: 1,
                    is_dine_in = tableStatus != "TAKEAWAY" && tableStatus != "DELIVERY",
                    is_take_away = tableStatus == "TAKEAWAY",
                    is_delivery = tableStatus == "DELIVERY",
                    table_id = tableId,
                    no_of_person = tableInfo.seating_capacity,
                    waiter_request_status = true,
                    kitchen_response_status = true, // Assuming KOT is being sent
                    order_status = "RUNNING",
                    is_merge = false,
                    is_active = 1,
                    order_master_id = newOrderMasterApiId["order_master_id"], // Use ID from getOrderNo
                    is_delivered = false
                )
                Log.d("TableStatus", "New OrderMaster ID: $tableStatus,${tableStatus != "TAKEAWAY" && tableStatus != "DELIVERY"}")
                val response = apiService.createOrder(orderRequest)
                if (response.isSuccessful && response.body() != null) {
                    orderMasterResponse = response.body()
                    currentOrderMasterId = orderMasterResponse!!.order_master_id
                    // Update table availability only if a new order is created for a dine-in table
                    if (orderRequest.is_dine_in) {
                        apiService.updateTableAvailability(tableId, "OCCUPIED")
                    }
                } else {
                    emit(Result.failure(Exception("Error creating new OrderMaster: ${response.code()}, ${response.errorBody()?.string()}")))
                    return@flow
                }
            } else{
                // If currentOrderMasterId was passed or found, but we don't have the TblOrderResponse object yet
                // You might need an API endpoint to fetch OrderMaster details by its ID if not already available
                // For now, let's assume if currentOrderMasterId is not null, it's valid.
                // The TblOrderResponse is mainly used to emit success, so we might need to construct a minimal one or fetch it.
                // This part depends on what TblOrderResponse should contain when updating.
                // For simplicity, let's assume we proceed and the success emission will primarily focus on the KOT.
                // Fetch the order master details if we only have the ID
                val masterResponse = apiService.getOrderMasterById(currentOrderMasterId.toLong()) // YOU MIGHT NEED THIS ENDPOINT
                if (masterResponse.isSuccessful && masterResponse.body() != null) {
                    orderMasterResponse = masterResponse.body()
                } else {
                    emit(Result.failure(Exception("Could not retrieve details for existing OrderMaster ID: $currentOrderMasterId")))
                    return@flow
                }
            }


            // 3. Create OrderDetails for the items being placed (new or added)
            val newKotNumberMap = apiService.getKotNo() // Get a KOT number for this batch of items
            val newKotNumber = newKotNumberMap["kot_number"]

            if (currentOrderMasterId == null || newKotNumber == null) {
                emit(Result.failure(Exception("Failed to obtain OrderMaster ID or KOT number.")))
                return@flow
            }


            val orderDetailsList = itemsToPlace.map { item ->
                val pricePerUnit = when (tableStatus) {
                    "AC" -> item.menuItem.ac_rate
                    "PARCEL", "DELIVERY" -> item.menuItem.parcel_rate
                    else -> item.menuItem.rate
                }
                val tax = apiService.getTaxSplit(item.menuItem.tax_id)
                val cgst = tax[0].tax_split_percentage
                val sgst= tax[1].tax_split_percentage
                val totalAmountForTaxCalc = pricePerUnit * item.quantity
                val taxAmount = calculateGst(totalAmountForTaxCalc, item.menuItem.tax_percentage.toDouble(), true)
                val cgstAmount = calculateGst(totalAmountForTaxCalc, cgst.toDouble(), true)
                val sgstAmount = calculateGst(totalAmountForTaxCalc, sgst.toDouble(), true)
                val cess = if (item.menuItem.cess_specific!=0.00) calculateGstAndCess(totalAmountForTaxCalc, item.menuItem.tax_percentage.toDouble(), item.menuItem.cess_per.toDouble(), true,cessSpecific = item.menuItem.cess_specific)
                else calculateGstAndCess(totalAmountForTaxCalc , item.menuItem.tax_percentage.toDouble(), item.menuItem.cess_per.toDouble(), true)
                OrderDetails(
                    order_master_id = currentOrderMasterId, // Link to existing or new OrderMaster
                    order_details_id = 0, // Backend should generate this or handle it
                    kot_number = newKotNumber, // KOT number for this specific batch
                    menu_item_id = item.menuItem.menu_item_id,
                    rate = if (item.menuItem.is_inventory !=1L) taxAmount.basePrice else cess.basePrice, // Rate per unit (base price)
                    qty = item.quantity,
                    total = if (item.menuItem.is_inventory !=1L) taxAmount.basePrice else cess.basePrice, // Total base price for this item line
                    tax_id = item.menuItem.tax_id,
                    tax_name = item.menuItem.tax_name,
                    tax_amount = if (item.menuItem.is_inventory !=1L) taxAmount.gstAmount else cess.gstAmount,
                    sgst_per = if (tableStatus!="DELIVERY")sgst.toDouble() else 0.0,
                    sgst = if (item.menuItem.is_inventory !=1L) {if (tableStatus!="DELIVERY") sgstAmount.gstAmount else 0.0} else{if (tableStatus!="DELIVERY") cess.gstAmount / 2 else 0.0},
                    cgst_per = if (tableStatus!="DELIVERY") cgst.toDouble() else 0.0,// Assuming SGST/CGST are half of total GST
                    cgst = if (item.menuItem.is_inventory !=1L) {if (tableStatus!="DELIVERY") cgstAmount.gstAmount else 0.0} else{if (tableStatus!="DELIVERY") cess.gstAmount / 2 else 0.0}, // Adjust if your backend calculates differently
                    igst_per = if (tableStatus=="DELIVERY")item.menuItem.tax_percentage.toDouble() else 0.0,
                    igst =  if (tableStatus=="DELIVERY") taxAmount.gstAmount else 0.0 ,
                    cess_per = if (item.menuItem.is_inventory ==1L)item.menuItem.cess_per.toDouble() else 0.0,
                    cess = if (item.menuItem.is_inventory ==1L && item.menuItem.cess_specific!=0.00) cess.cessAmount else 0.0,
                    cess_specific = if (item.menuItem.is_inventory ==1L&& item.menuItem.cess_specific!=0.00) item.menuItem.cess_specific else 0.0 ,
                    grand_total = if (item.menuItem.is_inventory ==1L && item.menuItem.cess_specific!=0.00) cess.totalPrice else taxAmount.totalPrice,
                    prepare_status = true, // Item needs preparation
                    item_add_mode = existingOpenOrderMasterId != null, // True if adding to an existing order
                    is_flag = false,
                    merge_order_nos = "",
                    merge_order_tables = "", // Name of the current table
                    merge_pax = 0, // Pax of the current table
                    is_active = 1
                )
            }

            val detailsResponse = apiService.createOrderDetails(orderDetailsList)

            if (detailsResponse.isSuccessful) {
                // If TblOrderResponse needs to be the master order, ensure orderMasterResponse is not null
                if (orderMasterResponse != null) {
                    // Update the KOT number on the response object to reflect the latest KOT generated
                    // Note: TblOrderResponse might represent the master, which can have multiple KOTs.
                    // This assignment implies the response primarily reflects the latest action.
                    orderMasterResponse.kot_number = newKotNumber
                    emit(Result.success(orderMasterResponse))
                } else {
                    // This case should ideally be handled earlier by ensuring orderMasterResponse is fetched/created
                    emit(Result.failure(Exception("Order details created, but failed to package final response.")))
                }
            } else {
                emit(Result.failure(Exception("Error creating OrderDetails: ${detailsResponse.code()}, ${detailsResponse.errorBody()?.string()}")))
            }

        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    // --- Helper function placeholder in ApiService (you need to define the actual endpoint) ---
    // In ApiService interface:
    // @GET("api/your_endpoint/table/{table_id}/open_order_master")
    // suspend fun getOpenOrderMasterForTable(@Path("table_id") tableId: Long): Response<TblOrderResponse>
    //
    // @GET("api/your_endpoint/order_master/{order_master_id}")
    // suspend fun getOrderMasterById(@Path("order_master_id") orderMasterId: Int): Response<TblOrderResponse>
    // ---

    /**
     * Get open order items for a specific table to display in UI.
     * This needs to be robust to fetch all items belonging to any open OrderMaster for the table.
     */
    suspend fun getOpenOrderItemsForTable(tableId: Long): List<TblOrderDetailsResponse> {
        // This implementation needs to correctly find the OPEN OrderMaster(s) for the table
        // and then fetch all its details.
        // The current implementation seems to get *an* order_master_id and then its details.
        // It needs to be specific to "OPEN" orders for the given table.

        // Step 1: Find the open OrderMaster ID(s) for the table.
        // Let's assume you have an endpoint that returns the TblOrderResponse for an open order.
        val openOrderMasterResponse = apiService.getOpenOrderMasterForTable(tableId) // Assuming this is defined

        if (openOrderMasterResponse.isSuccessful) {
            val orderMaster = openOrderMasterResponse.body()
            if (orderMaster != null && orderMaster.order_status == "RUNNING") { // Check if it's actually open
                val orderMasterId = orderMaster.order_master_id

                // Step 2: Fetch all OrderDetails for that OrderMaster ID.
                // Assuming getOpenOrderDetailsForTable actually means "getAllOrderDetailsForOrderMaster"
                val orderDetailsResponse = apiService.getOpenOrderDetailsForTable(orderMasterId?.toInt()) // YOU MAY NEED TO RENAME/CREATE THIS
                if (orderDetailsResponse.isSuccessful && orderDetailsResponse.body() != null) {
                    return orderDetailsResponse.body()!!
                }
            }
        }
        return emptyList() // Return empty if no open order or error
    }


    /**
     * Get all orders
     * Our backend doesn't have a filter by table yet, so we get all orders and filter client-side
     */
    suspend fun getAllOrders(): List<TblOrderResponse> {
        try {
            val response = apiService.getAllOrders()

            if (response.isSuccessful) {
                val orders = response.body()
                if (orders != null) {

                    return orders
                } else {
                    return emptyList()
                }
            } else {
                return emptyList()
            }
        } catch (e: Exception) {
            return emptyList()
        }
        return emptyList()
    }

    suspend fun getRunningOrderAmount(orderId: Long): Map<String, Double> {
        val response = apiService.getRunningOrderAmount(orderId)
        if (response.isSuccessful) {
            return response.body() ?: emptyMap()
        }
        return emptyMap()
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
    suspend fun getActiveOrdersByTable(tableId: Int): Flow<Result<List<TblOrderResponse>>> = flow {
        try {
            val response = apiService.getAllOrders()

            if (response.isSuccessful) {
                val allOrders = response.body()
                if (allOrders != null) {
                    // Filter active orders for this table
                    val activeOrders = allOrders.filter {
                        it.table_id.toInt() == tableId &&
                                it.order_status != OrderStatus.COMPLETED.name &&
                                it.order_status != OrderStatus.CANCELLED.name
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
}
data class GstResult(val basePrice: Double, val gstAmount: Double, val totalPrice: Double)

fun calculateGst(amount: Double, gstRate: Double, isInclusive: Boolean): GstResult {
    return if (isInclusive) {
        val basePrice = amount / (1 + gstRate / 100)
        val gstAmount = basePrice * gstRate / 100
        val totalPrice = basePrice + gstAmount
        GstResult(basePrice, gstAmount, totalPrice)
    } else {
        val gstAmount = amount * gstRate / 100
        val totalPrice = amount + gstAmount
        GstResult(amount, gstAmount, totalPrice)
    }
}

data class GstCessResult(
    val basePrice: Double,
    val gstAmount: Double,
    val cessAmount: Double,
    val totalPrice: Double
)
fun calculateGstAndCess(amount: Double, gstRate: Double, cessRate: Double, isInclusive: Boolean,cessSpecific:Double?=0.0): GstCessResult {
    return if (cessSpecific!=null) {
        if (isInclusive) {
            val amount = amount - cessSpecific
            val totalRate = gstRate + cessRate
            val basePrice = amount / ( 1 + totalRate / 100)
            val gstAmount = basePrice * gstRate / 100
            val cessAmount = basePrice * cessRate / 100
            val totalPrice = basePrice + gstAmount + cessAmount +cessSpecific
            GstCessResult(basePrice, gstAmount, cessAmount, totalPrice)
        } else {
            val gstAmount = amount * gstRate / 100
            val cessAmount = amount * cessRate / 100
            val totalPrice = amount + gstAmount + cessAmount+cessSpecific
            GstCessResult(amount, gstAmount, cessAmount, totalPrice)
        }
    } else{
        if (isInclusive) {
            val totalRate = gstRate + cessRate
            val basePrice = amount / ( 1 + totalRate / 100)
            val gstAmount = basePrice * gstRate / 100
            val cessAmount = basePrice * cessRate / 100
            val totalPrice = basePrice + gstAmount + cessAmount
            GstCessResult(basePrice, gstAmount, cessAmount, totalPrice)
        } else {
            val gstAmount = amount * gstRate / 100
            val cessAmount = amount * cessRate / 100
            val totalPrice = amount + gstAmount + cessAmount
            GstCessResult(amount, gstAmount, cessAmount, totalPrice)
        }
    }
}