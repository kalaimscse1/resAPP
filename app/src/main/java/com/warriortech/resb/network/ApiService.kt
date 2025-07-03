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

    @GET("table/table/getTableByAreaId/{area_id}")
    suspend fun getTablesBySection(@Path("area_id") section: Long): List<Table>

    @GET("table/table/getTable/{table_id}")
    suspend fun getTablesByStatus(@Path("table_id") tableId: Long): Table

    @PUT("tables/{id}/status")
    suspend fun updateTableStatus(
        @Path("id") tableId: Long,
        @Query("status") status: String
    ): Response<Void>

    @GET("settings/tax/getTaxSplitByTaxId/{tax_id}")
    suspend fun getTaxSplit(@Path("tax_id") taxId: Long): List<TblTaxSplit>

    @GET("table/table/updateTableAvailabilityByTableId/{table_id}")
    suspend fun updateTableAvailability(
        @Path("table_id") tableId: Long,
        @Query("table_availability") status: String
    ): Int

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
    @POST("order/addOrder")
    suspend fun createOrder(@Body orderRequest: OrderMaster): Response<TblOrderResponse>

    @GET("order/getOrder/{table_id}")
    suspend fun getOrder(@Path("table_id") tableId: Long): Response<TblOrderResponse>

    @GET("order/getOrder/{order_master_id}")
    suspend fun getOrderMasterById(@Path("order_master_id") orderId: Long): Response<TblOrderResponse>

    @POST("order/orderDetails/addAllOrderDetails")
    suspend fun createOrderDetails(@Body orderRequest: List<OrderDetails>): Response<List<OrderDetails>>
    
    @GET("order/getOrderByTableId/{table_id}")
     suspend fun getOpenOrderMasterForTable(@Path("table_id") tableId: Long): Response<TblOrderResponse>

    @GET("order/getOrderNO")
    suspend fun getOrderNo(): Map<String, Int>

    @GET("order/orderDetails/getKotNO")
    suspend fun getKotNo(): Map<String, Int>

    @GET("order/getOrdersByRunning")
    suspend fun getAllOrders(): Response<List<TblOrderResponse>>

    @GET("order/getRunningOrderAmount/{order_master_id}")
    suspend fun getRunningOrderAmount(@Path("order_master_id") orderId: Long): Response<Map<String,Double>>

    @GET("order/getOrderNoForEdit/{table_id}")
    suspend fun getOpenOrderItemsForTable(@Path("table_id") tableId: Long):Response<Map<String, Int>>

    @GET("order/orderDetails/getOrdersDetailsByOrderIdApp/{order_master_id}")
    suspend fun getOpenOrderDetailsForTable(@Path("order_master_id") tableId: Long?):Response<List<TblOrderDetailsResponse>>

    @POST("print/kot")
    suspend fun printKOT(@Body orderRequest: KOTRequest): Response<Map<String, String>>

    @PUT("orders/{orderId}")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: Int,
        @Body statusUpdate: Map<String, String>
    ): Response<Order>


    suspend fun getOrdersByOrderId(lng: Long): List<TblOrderDetailsResponse>

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