import com.warriortech.resb.model.*

@POST("paid-bills/{id}")
    suspend fun updatePaidBill(@Path("id") id: Long, @Body paidBill: PaidBillRequest): PaidBillResponse

    @DELETE("paid-bills/{id}")
    suspend fun deletePaidBill(@Path("id") id: Long): PaidBillResponse

    @POST("register")
    suspend fun registerCompany(@Body registrationRequest: RegistrationRequest): RegistrationResponse