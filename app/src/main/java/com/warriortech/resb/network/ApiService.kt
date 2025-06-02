package com.warriortech.resb.network

import com.warriortech.resb.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * API service interface for network operations
 * Includes endpoints with offline caching policies
 */
interface ApiService {
    
    /**
     * Authentication
     */

    @POST("auth/staff")
    suspend fun login(
//        @Field("companyCode") companyCode: String,
        @Body request: LoginRequest
    ): ApiResponse<AuthResponse>

    /**
     * Table Management
     */
    @GET("table/table/getTablesByIsActive")
    suspend fun getAllTables(): List<Table>

    @GET("table/table/section/{area_id}")
    suspend fun getTablesBySection(@Path("area_id") section: Long): List<Table>

    @PUT("tables/{id}/status")
    suspend fun updateTableStatus(
        @Path("id") tableId: Long,
        @Query("status") status: String
    ): Response<Void>

    @GET("table/area/getAreasByIsActive")
    suspend fun getAllAreas(): List<Area>
    // Menu item endpoints
    @GET("menu-items")
    suspend fun getAllMenuItems(): List<MenuItem>

    @GET("menu-items/category/{category}")
    suspend fun getMenuItemsByCategory(@Path("category") category: String): List<MenuItem>
    @GET("menu/menuItem/getMenuItemsByIsActiveApp")
    suspend fun getMenuItems(): Response<List<MenuItem>>

    // Order endpoints
    @POST("api/orders")
    suspend fun createOrder(@Body orderRequest: CreateOrderRequest): Response<Order>

    @GET("api/orders")
    suspend fun getAllOrders(): Response<List<Order>>

    @POST("api/orders/{orderId}/print")
    suspend fun printKOT(@Path("orderId") orderId: Long): Response<PrintResponse>

    @PUT("api/orders/{orderId}")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: Int,
        @Body statusUpdate: Map<String, String>
    ): Response<Order>


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
    /**
     * Analytics
     */
//    @GET("analytics/sales/daily")
//    suspend fun getDailySales(@Query("date") date: String): Response<SalesData>
//
//    @GET("analytics/sales/weekly")
//    suspend fun getWeeklySales(@Query("startDate") startDate: String): Response<SalesData>
//
//    @GET("analytics/sales/monthly")
//    suspend fun getMonthlySales(@Query("month") month: Int, @Query("year") year: Int): Response<SalesData>

    /**
     * Synchronization (for offline functionality)
     */
//    @POST("sync")
//    suspend fun syncPendingChanges(@Body syncRequest: SyncRequest): Response<SyncResponse>
}