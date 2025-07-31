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

    @GET("companyMaster/checkCompanyCode/{companyCode}")
    suspend fun checkIsBlock(@Path("companyCode") companyCode: String): ApiResponse<Boolean>

    @POST("auth/staff")
    suspend fun login(
        @Header("X-Tenant-ID") tenantId: String,
        @Body request: LoginRequest
    ): ApiResponse<AuthResponse>

    @PUT("auth/changePassword/{staff_id}")
    suspend fun changePassword(
        @Path("staff_id") staffId: Long,
        @Body passwordRequest: ChangePasswordRequest,
        @Header("X-Tenant-ID") tenantId: String
    ): ApiResponse<Boolean>

    /**
     * Dashboard Management
     */

    @GET("dashboard/metrics")
    suspend fun getDashboardMetrics(@Header("X-Tenant-ID") tenantId: String): Response<DashboardMetrics>

    @GET("dashboard/running-orders")
    suspend fun getRunningOrders(@Header("X-Tenant-ID") tenantId: String): Response<List<RunningOrder>>

    @GET("dashboard/recent-activity")
    suspend fun getRecentActivity(@Header("X-Tenant-ID") tenantId: String): Response<List<String>>

    @GET("dashboard/getPayModeAmountApp")
    suspend fun getPayModeAmount(@Header("X-Tenant-ID") tenantId: String): Response<List<PaymentModeDataResponse>>

    @GET("dashboard/getWeeklySales")
    suspend fun getWeeklySales(@Header("X-Tenant-ID") tenantId: String): Response<List<WeeklySalesData>>


    /**
     * Area Management
     */

    @GET("table/area/getAreasByIsActive")
    suspend fun getAllAreas(@Header("X-Tenant-ID") tenantId: String): Response<List<Area>>

    @POST("table/area/addArea")
    suspend fun createArea(
        @Body area: Area,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Area>

    @PUT("table/area/updateAreas/{area_id}")
    suspend fun updateArea(
        @Path("area_id") lng: Long,
        @Body area: Area,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Int>

    @DELETE("table/area/deleteAreaById/{area_id}")
    suspend fun deleteArea(
        @Path("area_id") lng: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Int>

    /**
     * Table Management
     */

    @GET("table/table/getTablesByIsActive")
    suspend fun getAllTables(@Header("X-Tenant-ID") tenantId: String): Response<List<Table>>

    @GET("table/table/getTableByAreaId/{area_id}")
    suspend fun getTablesBySection(
        @Path("area_id") section: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<List<Table>>

    @GET("table/table/getTable/{table_id}")
    suspend fun getTablesByStatus(
        @Path("table_id") tableId: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Table

    @PUT("tables/{id}/status")
    suspend fun updateTableStatus(
        @Path("id") tableId: Long,
        @Query("status") status: String,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Void>

    @POST("table/table/addTable")
    suspend fun createTable(
        @Body table: TblTable,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Table>

    @PUT("table/table/updateTables/{table_id}")
    suspend fun updateTable(
        @Path("table_id") lng: Long,
        @Body table: TblTable,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Int>

    @DELETE("table/table/deleteTableById/{table_id}")
    suspend fun deleteTable(
        @Path("table_id") lng: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Int>

    @GET("table/table/updateTableAvailabilityByTableId/{table_id}")
    suspend fun updateTableAvailability(
        @Path("table_id") tableId: Long,
        @Query("table_availability") status: String,
        @Header("X-Tenant-ID") tenantId: String
    ): Int

    @DELETE("table/table/deleteTableById/{table_id}")
    suspend fun deleteTable(
        @Path("table_id") id: Int,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Unit>

    /**
     * Menu Management
     */

    @GET("menu/getMenusByIsActive")
    suspend fun getAllMenus(@Header("X-Tenant-ID") tenantId: String): Response<List<Menu>>

    @POST("menu/addMenu")
    suspend fun createMenu(
        @Body menu: Menu,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Menu>

    @PUT("menu/updateMenus/{menu_id}")
    suspend fun updateMenu(
        @Path("menu_id") id: Long,
        @Body menu: Menu,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Int>

    @DELETE("menu/deleteMenuById/{menu_id}")
    suspend fun deleteMenu(
        @Path("menu_id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Int>

    @GET("menu/getMaxOrderBy")
    suspend fun getOrderBy(@Header("X-Tenant-ID") tenantId: String): Response<Map<String, Long>>

    /**
     * MenuCategory Management
     */

    @GET("menu/itemCategory/getItemCategoryByIsActive")
    suspend fun getAllMenuCategories(@Header("X-Tenant-ID") tenantId: String): Response<List<MenuCategory>>

    @POST("menu/itemCategory/addItemCategory")
    suspend fun createMenuCategory(
        @Body category: MenuCategory,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<MenuCategory>

    @PUT("menu/itemCategory/updateItemCategory/{item_cat_id}")
    suspend fun updateMenuCategory(
        @Path("item_cat_id") id: Long,
        @Body category: MenuCategory,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<MenuCategory>

    @DELETE("menu/itemCategory/deleteItemCategoryById/{item_cat_id}")
    suspend fun deleteMenuCategory(
        @Path("item_cat_id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Unit>

    @GET("menu/itemCategory/getMaxOrderBy")
    suspend fun getMenuCategoryOrderBy(@Header("X-Tenant-ID") tenantId: String): Response<Map<String, Long>>

    /**
     * MenuItem Management
     */

    @POST("menu/menuItem/addMenuItem")
    suspend fun createMenuItem(
        @Body menuItem: MenuItem,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<MenuItem>

    @PUT("menu/menuItem/updateMenuItems/{menu_item_id}")
    suspend fun updateMenuItem(
        @Path("id") id: Long,
        @Body menuItem: MenuItem,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<MenuItem>

    @DELETE("menu/deleteMenuItemById/{menu_item_id}")
    suspend fun deleteMenuItem(
        @Path("id") id: Int,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Any>

    @GET("menu/menuItem/getMenuItemsByIsActive")
    suspend fun getMenuItems(@Header("X-Tenant-ID") tenantId: String): Response<List<MenuItem>>

    @GET("menu/menuItem/getMenuItemsByIsActive")
    suspend fun getAllMenuItems(@Header("X-Tenant-ID") tenantId: String): Response<List<MenuItem>>

    @GET("menu/menuItem/search")
    suspend fun searchMenuItems(
        @Query("query") query: String,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<List<MenuItem>>

    @GET("menu/menuItem/getMaxOrderBy")
    suspend fun getMenuItemOrderBy(@Header("X-Tenant-ID") tenantId: String): Response<Map<String, Long>>

    /**
     * Order Management
     */

    @POST("order/addOrder")
    suspend fun createOrder(
        @Body orderRequest: OrderMaster,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<TblOrderResponse>

    @GET("order/getOrder/{order_master_id}")
    suspend fun getOrderMasterById(
        @Path("order_master_id") orderId: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<TblOrderResponse>

    @POST("order/orderDetails/addAllOrderDetails")
    suspend fun createOrderDetails(
        @Body orderRequest: List<OrderDetails>,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<List<TblOrderDetailsResponse>>

    @GET("order/getOrderByTableId/{table_id}")
    suspend fun getOpenOrderMasterForTable(
        @Path("table_id") tableId: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<TblOrderResponse>

    @GET("order/getOrderNO")
    suspend fun getOrderNo(@Header("X-Tenant-ID") tenantId: String): Map<String, Int>

    @GET("order/orderDetails/getKotNO")
    suspend fun getKotNo(@Header("X-Tenant-ID") tenantId: String): Map<String, Int>

    @GET("order/getOrdersByRunning")
    suspend fun getAllOrders(@Header("X-Tenant-ID") tenantId: String): Response<List<TblOrderResponse>>

    @GET("order/getRunningOrderAmount/{order_master_id}")
    suspend fun getRunningOrderAmount(
        @Path("order_master_id") orderId: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Map<String, Double>>

    @GET("order/getOrderNoForEdit/{table_id}")
    suspend fun getOpenOrderItemsForTable(
        @Path("table_id") tableId: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Map<String, Int>>

    @GET("order/orderDetails/getOrdersDetailsByOrderIdApp/{order_master_id}")
    suspend fun getOpenOrderDetailsForTable(
        @Path("order_master_id") tableId: Long?,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<List<TblOrderDetailsResponse>>

    @GET("order/orderDetails/getOrdersDetailsByIsActive")
    suspend fun getAllOrderDetails(@Header("X-Tenant-ID") tenantId: String): Response<List<TblOrderDetailsResponse>>

    @POST("print/kot")
    suspend fun printKOT(
        @Body orderRequest: KOTRequest,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<ByteArray>

    @PUT("orders/{orderId}")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: Int,
        @Body statusUpdate: Map<String, String>,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Order>

    @GET("settings/printer/getPrinterByIpAddress/{kitchen_cat_name}")
    suspend fun getIpAddresss(
        @Path("kitchen_cat_name") kitchenCatName: String,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<String>

    /**
     * Settings Management
     */

    /**
     * CounterSettings Management
     */

    @GET("settings/counter/getCounterByIsActive")
    suspend fun getCounters(@Header("X-Tenant-ID") tenantId: String): List<TblCounter>

    @GET("settings/counter/getCounter/{counter_id}")
    suspend fun getCounterById(
        @Path("counter_id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): TblCounter

    @POST("settings/counter/addCounter")
    suspend fun createCounter(
        @Body counter: TblCounter,
        @Header("X-Tenant-ID") tenantId: String
    ): TblCounter

    @PUT("settings/counter/updateCounter/{counter_id}")
    suspend fun updateCounter(
        @Path("counter_id") id: Long,
        @Body counter: TblCounter,
        @Header("X-Tenant-ID") tenantId: String
    ): Int

    @DELETE("settings/counter/deleteCounterById/{counter_id}")
    suspend fun deleteCounter(
        @Path("id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    )

    /**
     * RoleSettings Management
     */

    @GET("role/getRoleByIsActive")
    suspend fun getRoles(@Header("X-Tenant-ID") tenantId: String): Response<List<Role>>

    @GET("role/{role_id}")
    suspend fun getRoleById(
        @Path("role_id") id: Int,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Role>

    @POST("role/addRole")
    suspend fun createRole(
        @Body role: Role,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Role>

    @PUT("role/updateRole/{role_id}")
    suspend fun updateRole(
        @Path("role_id") id: Long,
        @Body role: Role,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Int>

    @DELETE("role/deleteRoleById/{role_id}")
    suspend fun deleteRole(
        @Path("role_id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    )

    /**
     * TaxSettings Management
     */

    @GET("settings/tax/getTaxByIsActive")
    suspend fun getTaxes(@Header("X-Tenant-ID") tenantId: String): List<Tax>

    @GET("settings/tax/getTax/{tax_id}")
    suspend fun getTaxById(
        @Path("tax_id") id: Int,
        @Header("X-Tenant-ID") tenantId: String
    ): Tax

    @POST("settings/tax/addTax")
    suspend fun createTax(
        @Body tax: Tax,
        @Header("X-Tenant-ID") tenantId: String
    ): Tax

    @PUT("settings/tax/updateTax/{id}")
    suspend fun updateTax(
        @Path("id") id: Long,
        @Body tax: Tax,
        @Header("X-Tenant-ID") tenantId: String
    ): Int

    @DELETE("settings/tax/deleteTaxById/{id}")
    suspend fun deleteTax(
        @Path("id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    )

    /**
     * TaxSplitSettings Management
     */

    @GET("settings/tax/getTaxSplitByIsActive")
    suspend fun getTaxSplits(@Header("X-Tenant-ID") tenantId: String): Response<List<TblTaxSplit>>

    @GET("settings/tax/getTaxSplit/{tax_split_id}")
    suspend fun getTaxSplitById(
        @Path("tax_split_id") id: Int,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<TblTaxSplit>

    @POST("settings/tax/addTaxSplit")
    suspend fun createTaxSplit(
        @Body taxSplit: TaxSplit,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<TblTaxSplit>

    @PUT("settings/tax/updateTaxSplit/{tax_split_id}")
    suspend fun updateTaxSplit(
        @Path("tax_split_id") id: Long,
        @Body taxSplit: TaxSplit,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Int>

    @DELETE("settings/tax/deleteTaxSplitById/{tax_split_id}")
    suspend fun deleteTaxSplit(
        @Path("tax_split_id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    )

    @GET("settings/tax/getTaxSplitByTaxId/{tax_id}")
    suspend fun getTaxSplit(
        @Path("tax_id") taxId: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): List<TblTaxSplit>

    /**
     * RestaurantProfileSettings Management
     */

    @GET("company/getCompany/{company_code}")
    suspend fun getRestaurantProfile(
        @Path("company_code") companyCode: String,
        @Header("X-Tenant-ID") tenantId: String
    ): RestaurantProfile

    @PUT("company/updateCompany/{company_code}")
    suspend fun updateRestaurantProfile(
        @Path("company_code") companyCode: String,
        @Body profile: RestaurantProfile,
        @Header("X-Tenant-ID") tenantId: String
    ): RestaurantProfile

    @GET("company/addCompany")
    suspend fun addRestaurantProfile(
        @Body profile: RestaurantProfile,
        @Header("X-Tenant-ID") tenantId: String
    ): RestaurantProfile

    /**
     * GeneralSettings Management
     */

    @GET("settings/generalSetting/getAllGeneralSetting")
    suspend fun getGeneralSettings(@Header("X-Tenant-ID") tenantId: String): Response<List<GeneralSettings>>

    @PUT("settings/generalSetting/updateSetting/{id}")
    suspend fun updateGeneralSettings(
        @Path("id") id: Long, @Body settings: GeneralSettings,
        @Header("X-Tenant-ID") tenantId: String
    ): GeneralSettings

    /**
     * VoucherSettings Management
     */

    @GET("settings/voucher/getVoucherByIsActive")
    suspend fun getVouchers(@Header("X-Tenant-ID") tenantId: String): List<Voucher>

    @GET("settings/voucher/getVoucher/{voucher_id}")
    suspend fun getVoucherById(
        @Path("voucher_id") id: Int,
        @Header("X-Tenant-ID") tenantId: String
    ): Voucher

    @POST("settings/voucher/addVoucher")
    suspend fun createVoucher(
        @Body voucher: Voucher,
        @Header("X-Tenant-ID") tenantId: String
    ): Voucher

    @PUT("settings/voucher/updateVoucher/{voucher_id}")
    suspend fun updateVoucher(
        @Path("voucher_id") id: Long,
        @Body voucher: Voucher,
        @Header("X-Tenant-ID") tenantId: String
    ): Voucher

    @DELETE("settings/voucher/deleteVoucherById/{voucher_id}")
    suspend fun deleteVoucher(
        @Path("voucher_id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    )

    @GET("settings/voucher/getVoucherByCounterId/{counter_id}")
    suspend fun getVoucherByCounterId(
        @Path("counter_id") counterId: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<TblVoucherResponse>

    /**
     * PrinterSettings Management
     */

    @GET("settings/printer/getPrinterByIsActive")
    suspend fun getPrinters(@Header("X-Tenant-ID") tenantId: String): List<Printer>

    @GET("settings/printer/getPrinter/{printer_id}  ")
    suspend fun getPrinterById(
        @Path("printer_id") id: Int,
        @Header("X-Tenant-ID") tenantId: String
    ): Printer

    @POST("settings/printer/addPrinter")
    suspend fun createPrinter(
        @Body printer: Printer,
        @Header("X-Tenant-ID") tenantId: String
    ): Printer

    @PUT("settings/printer/updatePrinter/{printer_id}")
    suspend fun updatePrinter(
        @Path("printer_id") id: Long,
        @Body printer: Printer,
        @Header("X-Tenant-ID") tenantId: String
    ): Printer

    @DELETE("settings/printer/deletePrinterById/{printer_id}")
    suspend fun deletePrinter(
        @Path("printer_id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    )

    /**
     * Payment Management
     */

    @GET("payment/getBillNoByCounterId")
    suspend fun getBillNoByCounterId(
        @Query("counter_id") counterId: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Map<String, String>>

    @POST("payment/addPayment")
    suspend fun addPayment(
        @Body paymentRequest: TblBillingRequest,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<TblBillingResponse>

    @GET("paid-bills")
    suspend fun getAllPaidBills(@Header("X-Tenant-ID") tenantId: String): Response<List<PaidBillSummary>>

    @GET("paid-bills/{id}")
    suspend fun getPaidBillById(
        @Path("id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<PaidBill>

    @PUT("paid-bills/{id}")
    suspend fun updatePaidBill(
        @Path("id") id: Long,
        @Body billData: PaidBill,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<PaidBill>

    @DELETE("paid-bills/{id}")
    suspend fun deletePaidBill(
        @Path("id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Unit>

    @POST("paid-bills/{id}/refund")
    suspend fun refundBill(
        @Path("id") id: Long,
        @Body refundData: Map<String, Any>,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<PaidBill>

    @GET("paid-bills/search")
    suspend fun searchPaidBills(
        @Query("q") query: String,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<List<PaidBillSummary>>

    /**
     * Reports Management
     */

    @GET("reports/today-sales")
    suspend fun getTodaySales(@Header("X-Tenant-ID") tenantId: String): Response<TodaySalesReport>

    @GET("reports/gst-summary")
    suspend fun getGSTSummary(@Header("X-Tenant-ID") tenantId: String): Response<GSTSummaryReport>

    @GET("reports/sales-summary/{date}")
    suspend fun getSalesSummaryByDate(
        @Path("date") date: String,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<SalesSummaryReport>

    /**
     * Customers Management
     */

    @GET("customer")
    suspend fun getAllCustomers(@Header("X-Tenant-ID") tenantId: String): Response<List<Customer>>

    @POST("customer")
    suspend fun createCustomer(
        @Body customer: Customer,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Customer>

    @PUT("customer/{id}")
    suspend fun updateCustomer(
        @Path("id") id: Long,
        @Body customer: Customer,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Customer>

    @DELETE("customer/{id}")
    suspend fun deleteCustomer(
        @Path("id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Unit>

    /**
     * Staff Management
     */

    @GET("auth/getStaffByIsActive")
    suspend fun getAllStaff(@Header("X-Tenant-ID") tenantId: String): Response<List<TblStaff>>

    @POST("auth/addStaff")
    suspend fun createStaff(
        @Body staff: TblStaff,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<TblStaff>

    @PUT("auth/updateStaff/{staff_id}")
    suspend fun updateStaff(
        @Path("staff_id") id: Long,
        @Body staff: TblStaff,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<TblStaff>

    @DELETE("auth/deleteStaffById/{staff_id}")
    suspend fun deleteStaff(
        @Path("staff_id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Unit>

    /**
     * Modifiers Management
     */

    @GET("menu/addOn/getAddOnByIsActive")
    suspend fun getAllModifiers(@Header("X-Tenant-ID") tenantId: String): Response<List<Modifiers>>

    @GET("modifiers/category/{categoryId}")
    suspend fun getModifiersByCategory(
        @Path("categoryId") categoryId: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): List<Modifiers>

    @GET("modifiers/menu-item/{menuItemId}")
    suspend fun getModifiersByMenuItem(
        @Path("menuItemId") menuItemId: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): List<Modifiers>

    @POST("modifiers")
    suspend fun createModifier(
        @Body modifier: Modifiers,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Modifiers>

    @PUT("modifiers/{id}")
    suspend fun updateModifier(
        @Path("id") id: Long,
        @Body modifier: Modifiers,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Modifiers>

    @DELETE("modifiers/{id}")
    suspend fun deleteModifier(
        @Path("id") id: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<Int>


    @GET("menu/addOn/getAddOnByCategoryId/{item_cat_id}")
    suspend fun getModifierGroupsForMenuItem(
        @Path("item_cat_id") menuItemId: Long,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<List<Modifiers>>

    @GET("modifiers/groups")
    suspend fun getAllModifierGroups(@Header("X-Tenant-ID") tenantId: String): Response<List<Modifiers>>

    /**
     * Kitchen KOT Management
     */

    @GET("kitchen/kots")
    suspend fun getKitchenKOTs(@Header("X-Tenant-ID") tenantId: String): Response<KitchenKOTResponse>

    @PUT("kitchen/kot/{kotId}/status")
    suspend fun updateKOTStatus(
        @Path("kotId") kotId: Int,
        @Body statusUpdate: KOTStatusUpdate,
        @Header("X-Tenant-ID") tenantId: String
    ): Response<KOTUpdateResponse>


    /**
     * Register Management
     */

    @POST("companyMaster/createCompanyMaster")
    suspend fun registerCompany(@Body registrationRequest: RegistrationRequest): RegistrationResponse

    @GET("companyMaster/getCompanyCode")
    suspend fun getCompanyCode(): Map<String, String>

}