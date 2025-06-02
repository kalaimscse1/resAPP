package com.warriortech.resb.data.remote

import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.Order
import com.warriortech.resb.model.OrderItem
import com.warriortech.resb.model.Table
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit interface for the Restaurant API
 */
interface RestaurantApiService {

    // Table endpoints
    @GET("tables")
    suspend fun getAllTables(): List<Table>
    
    @GET("tables/tables/section/{is_ac}")
    suspend fun getTablesBySection(@Path("is_ac") section: String): List<Table>
    
    @PUT("tables/{id}/status")
    suspend fun updateTableStatus(
        @Path("id") tableId: Long, 
        @Query("status") status: String
    ): Response<Void>
    
    // Menu item endpoints
    @GET("menu-items")
    suspend fun getAllMenuItems(): List<MenuItem>
    
    @GET("menu-items/category/{category}")
    suspend fun getMenuItemsByCategory(@Path("category") category: String): List<MenuItem>
    
    // Order endpoints
    @GET("orders")
    suspend fun getAllOrders(): List<Order>
    
    @GET("orders/table/{tableId}")
    suspend fun getOrdersByTableId(@Path("tableId") tableId: Long): List<Order>
    
    @GET("orders/table/{tableId}/active")
    suspend fun getActiveOrderByTableId(@Path("tableId") tableId: Long): Order?
    
    @POST("orders")
    suspend fun createOrder(
        @Body order: Order,
        @Body items: List<OrderItem>
    ): Response<Order>
    
    @PUT("orders/{id}")
    suspend fun updateOrder(
        @Path("id") orderId: Long,
        @Body order: Order
    ): Response<Void>
    
    @PUT("orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") orderId: Long,
        @Query("status") status: String
    ): Response<Void>
    
    // Order item endpoints
    @GET("order-items/order/{orderId}")
    suspend fun getOrderItemsByOrderId(@Path("orderId") orderId: Long): List<OrderItem>
    
    @PUT("order-items/{id}")
    suspend fun updateOrderItem(
        @Path("id") orderItemId: Long,
        @Body orderItem: OrderItem
    ): Response<Void>
}