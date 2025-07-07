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
    @GET("menu/menuItem/getMenuItemsByIsActive")
    suspend fun getMenuItems(): Response<List<MenuItem>>

    @GET("menu/menuItem/getMenuItemsByIsActive")
    suspend fun getAllMenuItems(): List<MenuItem>

    @GET("menu/menuItem/search")
    suspend fun searchMenuItems(@Query("query") query: String): Response<List<MenuItem>>

    // Customer endpoints
    @GET("customers/search")
    suspend fun searchCustomers(@Query("q") query: String): Response<CustomerSearchResponse>

    @POST("customers")
    suspend fun createCustomer(@Body customer: CreateCustomerRequest): Response<CreateCustomerResponse>

    // Order endpoints
    @POST("order/addOrder")
    suspend fun createOrder(@Body orderRequest: OrderMaster): Response<TblOrderResponse>

    @GET("order/getOrder/{table_id}")
    suspend fun getOrder(@Path("table_id") tableId: Long): Response<TblOrderResponse>

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

    @POST("table/area/addArea")
    suspend fun createArea(@Body area: Area) : Response<Area>

    @PUT("table/area/updateArea/{area_id}")
    suspend fun updateArea(
        @Path("area_id") lng: Long,
        @Body area: Area) : Response<Int>

    @DELETE("table/area/deleteAreaById/{area_id}")
    suspend fun deleteArea(
        @Path("area_id") lng: Long) : Response<Int>

    @POST("table/table/addTable")
    suspend fun createTable(
        @Body table: Table) : Response<Table>

    @PUT("table/table/updateTables/{table_id}")
    suspend fun updateTable(
        @Path("table_id") lng: Long,
        @Body table: Table) : Response<Int>

    @DELETE("table/table/deleteTableById/{table_id}")
    suspend fun deleteTable(lng: Long) : Response<Int>

    @GET("dashboard/metrics")
    suspend fun getDashboardMetrics(): Response<DashboardMetrics>

    // Report API endpoints
    @GET("reports/today-sales")
    suspend fun getTodaySales(): Response<TodaySalesReport>

    @GET("reports/gst-summary")
    suspend fun getGSTSummary(): Response<GSTSummaryReport>

    @GET("reports/sales-summary/{date}")
    suspend fun getSalesSummaryByDate(@Path("date") date: String): Response<SalesSummaryReport>

    @GET("dashboard/running-orders")
    suspend fun getRunningOrders(): Response<List<RunningOrder>>

    @GET("dashboard/recent-activity")
    suspend fun getRecentActivity(): Response<List<String>>

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
    @GET("menus")
    suspend fun getAllMenus(): List<Menu>

    @POST("menus")
    suspend fun createMenu(@Body menu: Menu): Menu

    @PUT("menus/{id}")
    suspend fun updateMenu(@Path("id") id: Long, @Body menu: Menu): Menu

    @DELETE("menus/{id}")
    suspend fun deleteMenu(@Path("id") id: Long)

    // Menu Category management
    @GET("menu-categories")
    suspend fun getAllMenuCategories(): List<MenuCategory>

    @POST("menu-categories")
    suspend fun createMenuCategory(@Body category: MenuCategory): MenuCategory

    @PUT("menu-categories/{id}")
    suspend fun updateMenuCategory(@Path("id") id: Long, @Body category: MenuCategory): MenuCategory

    @DELETE("menu-categories/{id}")
    suspend fun deleteMenuCategory(@Path("id") id: Long)

    // Menu Item management

    @POST("menu-items")
    suspend fun createMenuItem(@Body item: MenuItem): MenuItem

    @PUT("menu-items/{id}")
    suspend fun updateMenuItem(@Path("id") id: Long, @Body item: MenuItem): MenuItem

    @DELETE("menu-items/{id}")
    suspend fun deleteMenuItem(@Path("id") id: Long)

    // Customer management
    @GET("customers")
    suspend fun getAllCustomers(): List<Customer>

    @POST("customers")
    suspend fun createCustomer(@Body customer: Customer): Customer

    @PUT("customers/{id}")
    suspend fun updateCustomer(@Path("id") id: Long, @Body customer: Customer): Customer

    @DELETE("customers/{id}")
    suspend fun deleteCustomer(@Path("id") id: Long)

    // Staff management
    @GET("staff")
    suspend fun getAllStaff(): List<Staff>

    @POST("staff")
    suspend fun createStaff(@Body staff: Staff): Staff

    @PUT("staff/{id}")
    suspend fun updateStaff(@Path("id") id: Long, @Body staff: Staff): Staff

    @DELETE("staff/{id}")
    suspend fun deleteStaff(@Path("id") id: Long)

    // Role management
    @GET("roles")
    suspend fun getAllRoles(): List<Role>

    @POST("roles")
    suspend fun createRole(@Body role: Role): Role

    @PUT("roles/{id}")
    suspend fun updateRole(@Path("id") id: Long, @Body role: Role): Role

    @DELETE("roles/{id}")
    suspend fun deleteRole(@Path("id") id: Long)

    // Printer management
    @GET("printers")
    suspend fun getAllPrinters(): List<Printer>

    @POST("printers")
    suspend fun createPrinter(@Body printer: Printer): Printer

    @PUT("printers/{id}")
    suspend fun updatePrinter(@Path("id") id: Long, @Body printer: Printer): Printer

    @DELETE("printers/{id}")
    suspend fun deletePrinter(@Path("id") id: Long)

    // Tax management
    @GET("taxes")
    suspend fun getAllTaxes(): List<Tax>

    @POST("taxes")
    suspend fun createTax(@Body tax: Tax): Tax

    @PUT("taxes/{id}")
    suspend fun updateTax(@Path("id") id: Long, @Body tax: Tax): Tax

    @DELETE("taxes/{id}")
    suspend fun deleteTax(@Path("id") id: Long)

    // Tax Split management
    @GET("tax-splits")
    suspend fun getAllTaxSplits(): List<TaxSplit>

    @POST("tax-splits")
    suspend fun createTaxSplit(@Body taxSplit: TaxSplit): TaxSplit

    @PUT("tax-splits/{id}")
    suspend fun updateTaxSplit(@Path("id") id: Long, @Body taxSplit: TaxSplit): TaxSplit

    @DELETE("tax-splits/{id}")
    suspend fun deleteTaxSplit(@Path("id") id: Long)

    // Restaurant Profile management
    @GET("restaurant-profile")
    suspend fun getRestaurantProfile(): RestaurantProfile

    @PUT("restaurant-profile")
    suspend fun updateRestaurantProfile(@Body profile: RestaurantProfile): RestaurantProfile

    // General Settings management
    @GET("general-settings")
    suspend fun getGeneralSettings(): GeneralSettings

    @PUT("general-settings")
    suspend fun updateGeneralSettings(@Body settings: GeneralSettings): GeneralSettings

    // Voucher management
    @GET("vouchers")
    suspend fun getAllVouchers(): List<Voucher>

    @POST("vouchers")
    suspend fun createVoucher(@Body voucher: Voucher): Voucher

    @PUT("vouchers/{id}")
    suspend fun updateVoucher(@Path("id") id: Long, @Body voucher: Voucher): Voucher

    @DELETE("vouchers/{id}")
    suspend fun deleteVoucher(@Path("id") id: Long)

    // Counter management
    @GET("counters")
    suspend fun getAllCounters(): List<Counter>

    @POST("counters")
    suspend fun createCounter(@Body counter: Counter): Counter

    @PUT("counters/{id}")
    suspend fun updateCounter(@Path("id") id: Long, @Body counter: Counter): Counter

    @DELETE("counters/{id}")
    suspend fun deleteCounter(@Path("id") id: Long): Response<Int>

    // Modifier API endpoints
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

    // Report API Endpoints
    @POST("reports/sales-summary")
    suspend fun getSalesSummary(@Body request: Map<String, Any>): Response<SalesSummaryResponse>

    // Kitchen API endpoints
    @GET("kitchen/kots")
    suspend fun getKitchenKOTs(): Response<KitchenKOTResponse>

    @PUT("kitchen/kot/{kotId}/status")
    suspend fun updateKOTStatus(
        @Path("kotId") kotId: Int,
        @Body statusUpdate: KOTStatusUpdate
    ): Response<KOTUpdateResponse>
}