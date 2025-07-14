import com.warriortech.resb.model.Area
import com.warriortech.resb.model.Counter
import com.warriortech.resb.model.Customer
import com.warriortech.resb.model.GeneralSettings
import com.warriortech.resb.model.Menu
import com.warriortech.resb.model.MenuCategory
import com.warriortech.resb.model.MenuItem
import com.warriortech.resb.model.Printer
import com.warriortech.resb.model.RestaurantProfile
import com.warriortech.resb.model.Role
import com.warriortech.resb.model.Staff
import com.warriortech.resb.model.Table
import com.warriortech.resb.model.Tax
import com.warriortech.resb.model.TaxSplit
import com.warriortech.resb.model.Voucher

// Staff endpoints
    @GET("staff")
    suspend fun getStaff(): List<Staff>

    @GET("staff/{id}")
    suspend fun getStaffById(@Path("id") id: Int): Staff

    @POST("staff")
    suspend fun createStaff(@Body staff: Staff): Staff

    @PUT("staff/{id}")
    suspend fun updateStaff(@Path("id") id: Int, @Body staff: Staff): Staff

    @DELETE("staff/{id}")
    suspend fun deleteStaff(@Path("id") id: Int)

    // Role endpoints
    @GET("roles")
    suspend fun getRoles(): List<Role>

    @GET("roles/{id}")
    suspend fun getRoleById(@Path("id") id: Int): Role

    @POST("roles")
    suspend fun createRole(@Body role: Role): Role

    @PUT("roles/{id}")
    suspend fun updateRole(@Path("id") id: Int, @Body role: Role): Role

    @DELETE("roles/{id}")
    suspend fun deleteRole(@Path("id") id: Int)

    // Printer endpoints
    @GET("printers")
    suspend fun getPrinters(): List<Printer>

    @GET("printers/{id}")
    suspend fun getPrinterById(@Path("id") id: Int): Printer

    @POST("printers")
    suspend fun createPrinter(@Body printer: Printer): Printer

    @PUT("printers/{id}")
    suspend fun updatePrinter(@Path("id") id: Int, @Body printer: Printer): Printer

    @DELETE("printers/{id}")
    suspend fun deletePrinter(@Path("id") id: Int)

    // Counter endpoints
    @GET("counters")
    suspend fun getCounters(): List<Counter>

    @GET("counters/{id}")
    suspend fun getCounterById(@Path("id") id: Int): Counter

    @POST("counters")
    suspend fun createCounter(@Body counter: Counter): Counter

    @PUT("counters/{id}")
    suspend fun updateCounter(@Path("id") id: Int, @Body counter: Counter): Counter

    @DELETE("counters/{id}")
    suspend fun deleteCounter(@Path("id") id: Int)

    // Voucher endpoints
    @GET("vouchers")
    suspend fun getVouchers(): List<Voucher>

    @GET("vouchers/{id}")
    suspend fun getVoucherById(@Path("id") id: Int): Voucher

    @POST("vouchers")
    suspend fun createVoucher(@Body voucher: Voucher): Voucher

    @PUT("vouchers/{id}")
    suspend fun updateVoucher(@Path("id") id: Int, @Body voucher: Voucher): Voucher

    @DELETE("vouchers/{id}")
    suspend fun deleteVoucher(@Path("id") id: Int)

    // Tax endpoints
    @GET("taxes")
    suspend fun getTaxes(): List<Tax>

    @GET("taxes/{id}")
    suspend fun getTaxById(@Path("id") id: Int): Tax

    @POST("taxes")
    suspend fun createTax(@Body tax: Tax): Tax

    @PUT("taxes/{id}")
    suspend fun updateTax(@Path("id") id: Int, @Body tax: Tax): Tax

    @DELETE("taxes/{id}")
    suspend fun deleteTax(@Path("id") id: Int)

    // TaxSplit endpoints
    @GET("tax-splits")
    suspend fun getTaxSplits(): List<TaxSplit>

    @GET("tax-splits/{id}")
    suspend fun getTaxSplitById(@Path("id") id: Int): TaxSplit

    @POST("tax-splits")
    suspend fun createTaxSplit(@Body taxSplit: TaxSplit): TaxSplit

    @PUT("tax-splits/{id}")
    suspend fun updateTaxSplit(@Path("id") id: Int, @Body taxSplit: TaxSplit): TaxSplit

    @DELETE("tax-splits/{id}")
    suspend fun deleteTaxSplit(@Path("id") id: Int)

    // Restaurant Profile endpoints
    @GET("restaurant-profile")
    suspend fun getRestaurantProfile(): RestaurantProfile

    @PUT("restaurant-profile")
    suspend fun updateRestaurantProfile(@Body profile: RestaurantProfile): RestaurantProfile

    // General Settings endpoints
    @GET("general-settings")
    suspend fun getGeneralSettings(): GeneralSettings

    @PUT("general-settings")
    suspend fun updateGeneralSettings(@Body settings: GeneralSettings): GeneralSettings