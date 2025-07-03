import com.warriortech.resb.model.TblOrderDetailsResponse
import com.warriortech.resb.model.TblStaffResponse
import com.warriortech.resb.model.TblTableResponse
import com.warriortech.resb.model.MenuCategory
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.Order
import com.warriortech.resb.model.OrderItem
import com.warriortech.resb.model.OrderRequest
import com.warriortech.resb.model.KOTRequest
import com.warriortech.resb.model.SettingsModels
import com.warriortech.resb.model.DashboardMetrics
import com.warriortech.resb.model.RunningOrder
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("areas")
    suspend fun getAllAreas(): List<Area>

    @POST("areas")
    suspend fun createArea(@Body area: Area): Area

    @PUT("areas/{id}")
    suspend fun updateArea(@Path("id") id: Long, @Body area: Area): Area

    @DELETE("areas/{id}")
    suspend fun deleteArea(@Path("id") id: Long)

    @GET("tables")
    suspend fun getAllTables(): List<Table>

    @GET("tables/{section_id}")
    suspend fun getTablesBySection(@Path("section_id") sectionId: Long): List<Table>

    @POST("tables")
    suspend fun createTable(@Body table: Table): Table

    @PUT("tables/{id}")
    suspend fun updateTable(@Path("id") id: Long, @Body table: Table): Table

    @DELETE("tables/{id}")
    suspend fun deleteTable(@Path("id") id: Long)

    // Menu management
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
    @GET("menu-items")
    suspend fun getAllMenuItems(): List<MenuItem>

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
    suspend fun deleteCounter(@Path("id") id: Long)

    /**
     * Order Management
     */
    @POST("orders")
    suspend fun createOrder(@Body orderRequest: OrderRequest): Order

    @GET("orders")
    suspend fun getOrders(): Response<List<Order>>

    @GET("orders/details/{orderId}")
    suspend fun getOrdersByOrderId(@Path("orderId") lng: Long): List<TblOrderDetailsResponse>

    /**
     * Dashboard
     */
    @GET("dashboard/metrics")
    suspend fun getDashboardMetrics(): Response<DashboardMetrics>

    @GET("dashboard/running-orders")
    suspend fun getRunningOrders(): Response<List<RunningOrder>>

    @GET("dashboard/recent-activity")
    suspend fun getRecentActivity(): Response<List<String>>

    /**
     * Analytics
     */
}