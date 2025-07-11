package com.warriortech.resb.network

import android.content.SyncRequest
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
        @Body request: LoginRequest
    ): ApiResponse<AuthResponse>

    /**
     * Dashboard Management
     */
    @GET("dashboard/metrics")
    suspend fun getDashboardMetrics(): Response<DashboardMetrics>

    @GET("dashboard/running-orders")
    suspend fun getRunningOrders(): Response<List<RunningOrder>>

    @GET("dashboard/recent-activity")
    suspend fun getRecentActivity(): Response<List<String>>


    /**
     * Area Management
     */

    @GET("table/area/getAreasByIsActive")
    suspend fun getAllAreas(): Response<List<Area>>

    @POST("table/area/addArea")
    suspend fun createArea(@Body area: Area) : Response<Area>

    @PUT("table/area/updateArea/{area_id}")
    suspend fun updateArea(
        @Path("area_id") lng: Long,
        @Body area: Area) : Response<Int>

    @DELETE("table/area/deleteAreaById/{area_id}")
    suspend fun deleteArea(
        @Path("area_id") lng: Long) : Response<Int>

    /**
     * Table Management
     */
    @GET("table/table/getTablesByIsActive")
    suspend fun getAllTables(): Response<List<Table>>

    @GET("table/table/getTableByAreaId/{area_id}")
    suspend fun getTablesBySection(@Path("area_id") section: Long): List<Table>

    @GET("table/table/getTable/{table_id}")
    suspend fun getTablesByStatus(@Path("table_id") tableId: Long): Table

    @PUT("tables/{id}/status")
    suspend fun updateTableStatus(
        @Path("id") tableId: Long,
        @Query("status") status: String
    ): Response<Void>

    @POST("table/table/addTable")
    suspend fun createTable(
        @Body table: Table) : Response<Table>

    @PUT("table/table/updateTables/{table_id}")
    suspend fun updateTable(
        @Path("table_id") lng: Long,
        @Body table: Table) : Response<Int>

    @DELETE("table/table/deleteTableById/{table_id}")
    suspend fun deleteTable(lng: Long) : Response<Int>

    @GET("table/table/updateTableAvailabilityByTableId/{table_id}")
    suspend fun updateTableAvailability(
        @Path("table_id") tableId: Long,
        @Query("table_availability") status: String
    ): Int

    @DELETE("table/table/deleteTableById/{table_id}")
    suspend fun deleteTable(@Path("table_id") id: Int): Response<Unit>

    /**
     * Menu Management
     */

    @GET("menu/getMenusByIsActive")
    suspend fun getAllMenus(): Response<List<Menu>>

    @POST("menu/addMenu")
    suspend fun createMenu(@Body menu: Menu): Response<Menu>

    @PUT("menu/updateMenus/{menu_id}")
    suspend fun updateMenu(@Path("menu_id") id: Long, @Body menu: Menu): Response<Int>

    @DELETE("menu/deleteMenuById/{menu_id}")
    suspend fun deleteMenu(@Path("menu_id") id: Long): Response<Int>

    /**
     * MenuCategory Management
     */

    @GET("menu/itemCategory/getItemCategoryByIsActive")
    suspend fun getAllMenuCategories(): Response<List<MenuCategory>>

    @POST("menu/itemCategory/addItemCategory")
    suspend fun createMenuCategory(@Body category: MenuCategory): Response<MenuCategory>

    @PUT("menu/itemCategory/updateItemCategory/{item_cat_id}")
    suspend fun updateMenuCategory(@Path("item_cat_id") id: Long, @Body category: MenuCategory): Response<MenuCategory>

    @DELETE("menu/itemCategory/deleteItemCategoryById/{item_cat_id}")
    suspend fun deleteMenuCategory(@Path("item_cat_id") id: Long): Response<Unit>

    /**
     * MenuItem Management
     */

    @POST("menu/menuItem/addMenuItem")
    suspend fun createMenuItem(@Body menuItem: MenuItem): Response<MenuItem>

    @PUT("menu/menuItem/updateMenuItems/{menu_item_id}")
    suspend fun updateMenuItem(@Path("id") id: Long, @Body menuItem: MenuItem): Response<MenuItem>

    @DELETE("menu/deleteMenuItemById/{menu_item_id}")
    suspend fun deleteMenuItem(@Path("id") id: Int): Response<Unit>

    @GET("menu/menuItem/getMenuItemsByIsActive")
    suspend fun getMenuItems(): Response<List<MenuItem>>

    @GET("menu/menuItem/getMenuItemsByIsActive")
    suspend fun getAllMenuItems(): Response<List<MenuItem>>

    @GET("menu/menuItem/search")
    suspend fun searchMenuItems(@Query("query") query: String): Response<List<MenuItem>>

    /**
     * Order Management
     */
    @POST("order/addOrder")
    suspend fun createOrder(@Body orderRequest: OrderMaster): Response<TblOrderResponse>

    @GET("order/getOrder/{order_master_id}")
    suspend fun getOrderMasterById(@Path("order_master_id") orderId: Long): Response<TblOrderResponse>

    @POST("order/orderDetails/addAllOrderDetails")
    suspend fun createOrderDetails(@Body orderRequest: List<OrderDetails>): Response<List<TblOrderDetailsResponse>>

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

    @GET("order/orderDetails/getOrdersDetailsByIsActive")
    suspend fun getAllOrderDetails(): Response<List<TblOrderDetailsResponse>>

    @POST("print/kot")
    suspend fun printKOT(@Body orderRequest: KOTRequest): Response<Map<String, String>>

    @PUT("orders/{orderId}")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: Int,
        @Body statusUpdate: Map<String, String>
    ): Response<Order>

    /**
     * Settings Management
     */

    @GET("settings/voucher/getVoucherByCounterId/{counter_id}")
    suspend fun getVoucherByCounterId(@Path("counter_id") counterId: Long): Response<TblVoucherResponse>

    @GET("settings/tax/getTaxSplitByTaxId/{tax_id}")
    suspend fun getTaxSplit(@Path("tax_id") taxId: Long): List<TblTaxSplit>

    /**
     * Payment Management
     */

    @GET("payment/getBillNoByCounterId")
    suspend fun getBillNoByCounterId(@Query("counter_id") counterId: Long): Response<Map<String, String>>

    @POST("payment/addPayment")
    suspend fun addPayment(@Body paymentRequest: TblBillingRequest): Response<TblBillingResponse>



    /**
     * Reports Management
     */

    @GET("reports/today-sales")
    suspend fun getTodaySales(): Response<TodaySalesReport>

    @GET("reports/gst-summary")
    suspend fun getGSTSummary(): Response<GSTSummaryReport>

    @GET("reports/sales-summary/{date}")
    suspend fun getSalesSummaryByDate(@Path("date") date: String): Response<SalesSummaryReport>


    /**
     * Customers Management
     */

    @GET("customers")
    suspend fun getAllCustomers(): Response<List<Customer>>

    @POST("customers")
    suspend fun createCustomer(@Body customer: Customer): Response<Customer>

    @PUT("customers/{id}")
    suspend fun updateCustomer(@Path("id") id: Long, @Body customer: Customer): Response<Customer>

    @DELETE("customers/{id}")
    suspend fun deleteCustomer(@Path("id") id: Long): Response<Unit>

    /**
     * Staff Management
     */

    @GET("auth/getStaffByIsActive")
    suspend fun getAllStaff(): Response<List<TblStaff>>

    @POST("auth/addStaff")
    suspend fun createStaff(@Body staff: TblStaff): Response<TblStaff>

    @PUT("auth/updateStaff/{staff_id}")
    suspend fun updateStaff(@Path("staff_id") id: Long, @Body staff: TblStaff): Response<TblStaff>

    @DELETE("auth/deleteStaffById/{staff_id}")
    suspend fun deleteStaff(@Path("staff_id") id: Long): Response<Unit>

    /**
     * Modifiers Management
     */

    @GET("modifiers")
    suspend fun getAllModifiers(): Response<List<Modifiers>>

    @GET("modifiers/category/{categoryId}")
    suspend fun getModifiersByCategory(@Path("categoryId") categoryId: Long): List<Modifiers>

    @GET("modifiers/menu-item/{menuItemId}")
    suspend fun getModifiersByMenuItem(@Path("menuItemId") menuItemId: Long): List<Modifiers>

    @POST("modifiers")
    suspend fun createModifier(@Body modifier: Modifiers): Response<Modifiers>

    @PUT("modifiers/{id}")
    suspend fun updateModifier(@Path("id") id: Long, @Body modifier: Modifiers): Response<Modifiers>

    @DELETE("modifiers/{id}")
    suspend fun deleteModifier(@Path("id") id: Long): Response<Int>

    /**
     * Kitchen KOT Management
     */

    @GET("kitchen/kots")
    suspend fun getKitchenKOTs(): Response<KitchenKOTResponse>

    @PUT("kitchen/kot/{kotId}/status")
    suspend fun updateKOTStatus(
        @Path("kotId") kotId: Int,
        @Body statusUpdate: KOTStatusUpdate
    ): Response<KOTUpdateResponse>
}