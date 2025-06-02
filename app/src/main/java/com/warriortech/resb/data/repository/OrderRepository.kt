package com.warriortech.resb.data.repository


import com.warriortech.resb.model.CreateOrderRequest
import com.warriortech.resb.model.Order
import com.warriortech.resb.model.OrderItem
import com.warriortech.resb.model.OrderStatus
import com.warriortech.resb.model.PrintResponse
import com.warriortech.resb.network.ApiService
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
    suspend fun createOrder(tableId: Int, items: List<OrderItem>): Flow<Result<Order>> = flow {
        try {
            val orderRequest = CreateOrderRequest(tableId, items)
            val response = apiService.createOrder(orderRequest)

            if (response.isSuccessful) {
                val order = response.body()
                if (order != null) {
                    emit(Result.success(order))
                } else {
                    emit(Result.failure(Exception("Failed to create order - empty response")))
                }
            } else {
                emit(Result.failure(Exception("Error creating order: ${response.code()}")))
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
    suspend fun getOrdersByTable(tableId: Int): Flow<Result<List<Order>>> = flow {
        try {
            val response = apiService.getAllOrders()

            if (response.isSuccessful) {
                val allOrders = response.body()
                if (allOrders != null) {
                    // Filter orders for this table
                    val tableOrders = allOrders.filter { it.tableId == tableId }
                    emit(Result.success(tableOrders))
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
    suspend fun printKOT(orderId: Long): Flow<Result<
            PrintResponse>> = flow {
        try {
            val response = apiService.printKOT(orderId)

            if (response.isSuccessful) {
                val printResponse = response.body()
                if (printResponse != null) {
                    emit(Result.success(printResponse))
                } else {
                    emit(Result.failure(Exception("Failed to print KOT - empty response")))
                }
            } else {
                emit(Result.failure(Exception("Error printing KOT: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
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
